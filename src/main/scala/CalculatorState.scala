import scala.collection.immutable.Vector
import scala.collection.mutable.Queue
import scala.collection.mutable.Stack

// ラッパークラスだと思ってください
sealed trait CalculatorLiteral
case class NumericLiteral(value: String) extends CalculatorLiteral
case class BinaryOperator(value: String) extends CalculatorLiteral
case object Initial      extends CalculatorLiteral
case object ParenLeft    extends CalculatorLiteral
case object ParenRight   extends CalculatorLiteral
case object UnaryMinus   extends CalculatorLiteral
case object DecimalPoint extends CalculatorLiteral

// 状態的な
class CalculatorState(
  private val current:  CalculatorLiteral,
  private val nest:     Int,
  private val tokens:   Vector[String]
) {
  // 渡された文字列がどの要素として扱われるべきか判別してから新しい状態を作る
  def update(input: String): CalculatorState = {
    val next = input match {
      case "-" if current == ParenLeft || current == Initial =>
        UnaryMinus
      case "+" | "-" | "*" | "/" => BinaryOperator(input)
      case "("                   => ParenLeft
      case ")"                   => ParenRight
      case "."                   => DecimalPoint
      case _                     => NumericLiteral(input)
    }
    nextState(next)
  }

  // 計算可能な式の形になっているかを判別する
  def isReady: Boolean =
    current match {
      case BinaryOperator(_) | ParenLeft | Initial | UnaryMinus =>
        false
      case NumericLiteral(v) =>
        v.last != '.'
      case _ =>
        nest == 0
    }

  // 文字列として現在の状態を表現する
  override def toString(): String =
    current match {
      case Initial      => ""
      case ParenLeft    => tokens.foldRight("(")(_ + _)
      case ParenRight   => tokens.foldRight(")")(_ + _)
      case UnaryMinus   => tokens.foldRight("-")(_ + _)
      case DecimalPoint => tokens.foldRight(".")(_ + _) // 起こりえなさそう
      case NumericLiteral(v) => tokens.foldRight(v)(_ + _)
      case BinaryOperator(v) => tokens.foldRight(v)(_ + _)
    }

  // 逆ポーランド記法に変換し, Queue として返す
  // やりたいこと
  // 1 * (2 + 3) / 4 => 1 2 3 + 4 / * というように変換する
  def toIPoland(): Queue[String] = {
    val stack: Stack[String] = Stack[String]()
    val queue: Queue[String] = Queue[String]()

    val ptokens = current match {
      case NumericLiteral(v) => tokens :+ v
      case BinaryOperator(v) => tokens :+ v
      case ParenRight        => tokens :+ ")"
      case _                 => tokens // 起こりえないはず
    }

    // トークンを演算子と数値に分けたりする
    for (token <- ptokens) {
      token match {
        case "+" | "-" | "*" | "/" => stack.push(token)
        case "("                   => ;
        case ")"                   => queue.enqueue(stack.pop)
        case _                     => queue.enqueue(token)
      }
    }

    // 逆ポーランド記法になるように演算子を追加していく
    while (stack.nonEmpty) queue += stack.pop

    return queue
  }

  // 新しい状態を返す
  // ここで受付可能かどうか今の状態から判別している
  // 受け付けられないなら今の自分の状態をそのまま返す
  private def nextState(next: CalculatorLiteral): CalculatorState =
    (current, next) match {
      // 最初の状態の次に来てもよいのは...
      case (Initial, NumericLiteral(_)) =>
        new CalculatorState(next, nest, tokens)

      case (Initial, ParenLeft) =>
        new CalculatorState(next, nest + 1, tokens)

      case (Initial, UnaryMinus) =>
        new CalculatorState(next, nest, tokens)

      // 数値の次に来てもよいのは...
      case (NumericLiteral(u), NumericLiteral(v)) =>
        new CalculatorState(NumericLiteral(u + v), nest, tokens)

      case (NumericLiteral(v), BinaryOperator(_)) if v.last != '.' =>
        new CalculatorState(next, nest, tokens :+ v)

      case (NumericLiteral(v), DecimalPoint) if !v.contains('.') =>
        new CalculatorState(NumericLiteral(v + "."), nest - 1, tokens)

      case (NumericLiteral(v), ParenRight) if nest > 0 && v.last != '.' =>
        new CalculatorState(next, nest - 1, tokens :+ v)

      // 二項演算子の次に来てもよいのは...
      case (BinaryOperator(v), NumericLiteral(_)) =>
        new CalculatorState(next, nest, tokens :+ v)

      case (BinaryOperator(v), ParenLeft) =>
        new CalculatorState(next, nest + 1, tokens :+ v)

      // 開き括弧の次に来てもよいのは...
      case (ParenLeft, NumericLiteral(_)) =>
        new CalculatorState(next, nest, tokens :+ "(")

      case (ParenLeft, ParenLeft) =>
        new CalculatorState(next, nest + 1, tokens :+ "(")

      case (ParenLeft, UnaryMinus) =>
        new CalculatorState(next, nest, tokens :+ "(")

      // 閉じ括弧の次に来てもよいのは...
      case (ParenRight, BinaryOperator(_)) =>
        new CalculatorState(next, nest - 1, tokens :+ ")")

      case (ParenRight, ParenRight) if nest > 0 =>
        new CalculatorState(next, nest - 1, tokens :+ ")")

      // 単項演算子の次に来てもよいのは...
      case (UnaryMinus, NumericLiteral(v)) =>
        new CalculatorState(NumericLiteral("-" + v), nest, tokens)

      case (_, _) => this
    }
}

object CalculatorInitialState extends CalculatorState(Initial, 0, Vector[String]())
