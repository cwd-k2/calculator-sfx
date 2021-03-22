// ラッパークラスだと思ってください
sealed trait CalculatorLiteral
case class  NumberLiteral(value: String)  extends CalculatorLiteral
case class  BinaryOperator(value: String) extends CalculatorLiteral
case object ParenLeft                     extends CalculatorLiteral
case object ParenRight                    extends CalculatorLiteral
case object DecimalPoint                  extends CalculatorLiteral
case object UnaryMinus                    extends CalculatorLiteral

class CalculatorState(
  private val currentLiteral: CalculatorLiteral,
  private val nestDepth: Int,
  private val acceptDecimalPoint: Boolean,
          val currentTokens: Array[String]
) {
  // 渡された文字列がどの要素として扱われるべきか判別してから新しい状態を作る
  def update(a: String): CalculatorState = {
    val nextLiteral = a match {
      case "-" if currentLiteral == ParenLeft => UnaryMinus
      case "+" | "-" | "*" | "/" => BinaryOperator(a)
      case "("                   => ParenLeft
      case ")"                   => ParenRight
      case "."                   => DecimalPoint
      case _                     => NumberLiteral(a)
    }
    nextState(nextLiteral)
  }

  // 計算可能な式の形になっているかを判別する
  def isReady: Boolean =
    currentLiteral match {
      case BinaryOperator(_) | DecimalPoint | ParenLeft | UnaryMinus => false
      case _ => nestDepth == 0
    }

  // 新しい状態を返す
  // ここで受付可能かどうか今の状態から判別している
  // 受け付けられないなら今の自分の状態をそのまま返す
  private def nextState(nextLiteral: CalculatorLiteral): CalculatorState = {
    var nextNestDepth          = nestDepth
    var nextAcceptDecimalPoint = acceptDecimalPoint
    var nextTokens             = currentTokens.clone

    (currentLiteral, nextLiteral) match {
      case (NumberLiteral(_), NumberLiteral(v))  => {
        nextTokens(nextTokens.length - 1) += v
      }
      case (NumberLiteral(_), BinaryOperator(v)) => {
        nextAcceptDecimalPoint = true
        nextTokens = nextTokens :+ v
      }
      case (NumberLiteral(_), DecimalPoint)
      if acceptDecimalPoint => {
        nextAcceptDecimalPoint = false
        nextTokens(nextTokens.length - 1) += "."
      }
      case (NumberLiteral(_), ParenRight)
      if nestDepth > 0      => {
        nextNestDepth -= 1
        nextAcceptDecimalPoint = true
        nextTokens = nextTokens :+ ")"
      }

      case (BinaryOperator(_), NumberLiteral(v)) => {
        nextTokens = nextTokens :+ v
      }
      case (BinaryOperator(_), ParenLeft)     => {
        nextNestDepth += 1
        nextTokens = nextTokens :+ "("
      }

      case (DecimalPoint, NumberLiteral(v)) => {
        nextTokens(nextTokens.length - 1) += v
      }

      case (ParenLeft, NumberLiteral(v)) => {
        nextTokens = nextTokens :+ v
      }
      case (ParenLeft, ParenLeft)     => {
        nextNestDepth += 1
        nextTokens = nextTokens :+ "("
      }
      case (ParenLeft, UnaryMinus)    => {
        nextTokens = nextTokens :+ "-"
      }

      case (ParenRight, BinaryOperator(v)) => {
        nextTokens = nextTokens :+ v
      }
      case (ParenRight, ParenRight)
      if nextNestDepth > 0  => {
        nextNestDepth -= 1
        nextTokens = nextTokens :+ ")"
      }

      case (UnaryMinus, NumberLiteral(v)) => {
        nextTokens(nextTokens.length - 1) += v
      }

      case (_, _) => return this
    }

    new CalculatorState(nextLiteral, nextNestDepth, nextAcceptDecimalPoint, nextTokens)
  }
}

object CalculatorInitialState extends CalculatorState(ParenLeft, 0, true, Array[String]())
