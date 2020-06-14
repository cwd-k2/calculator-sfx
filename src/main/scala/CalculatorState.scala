// ラッパークラスだと思ってください
sealed trait CalculatorLiteral { val value: String }
case class NumberLiteral(value: String)  extends CalculatorLiteral
case class BinaryOperator(value: String) extends CalculatorLiteral
case class ParenLeft(value: String)      extends CalculatorLiteral
case class ParenRight(value: String)     extends CalculatorLiteral
case class DecimalPoint(value: String)   extends CalculatorLiteral
case class UnaryMinus(value: String)     extends CalculatorLiteral

class CalculatorState(
  private val currentLiteral: CalculatorLiteral,
  private val nestDepth: Int,
  private val acceptDecimalPoint: Boolean,
          val currentTokens: Array[String]
) {
  // 渡された文字列がどの要素として扱われるべきか判別してから新しい状態を作る
  def update(a: String): CalculatorState = {
    val nextLiteral: CalculatorLiteral = a match {
      case "-" if currentLiteral == ParenLeft("(") => UnaryMinus(a)
      case "+" | "-" | "*" | "/" => BinaryOperator(a)
      case "("                   => ParenLeft(a)
      case ")"                   => ParenRight(a)
      case "."                   => DecimalPoint(a)
      case _                     => NumberLiteral(a)
    }
    nextState(nextLiteral)
  }

  // 計算可能な式の形になっているかを判別する
  def isReady: Boolean = {
    currentLiteral match {
      case BinaryOperator(_) | DecimalPoint(_) | ParenLeft(_) | UnaryMinus(_) => false
      case _ => nestDepth == 0
    }
  }

  // 新しい状態を返す
  // ここで受付可能かどうか今の状態から判別している
  // 受け付けられないなら今の自分の状態をそのまま返す
  private def nextState(nextLiteral: CalculatorLiteral): CalculatorState = {
    var nextNestDepth: Int              = nestDepth
    var nextAcceptDecimalPoint: Boolean = acceptDecimalPoint
    var nextTokens: Array[String]       = currentTokens.clone

    (currentLiteral, nextLiteral) match {
      case (NumberLiteral(_), NumberLiteral(_))  => {
        nextTokens(nextTokens.length - 1) += nextLiteral.value
      }
      case (NumberLiteral(_), BinaryOperator(_)) => {
        nextAcceptDecimalPoint = true
        nextTokens = nextTokens :+ nextLiteral.value
      }
      case (NumberLiteral(_), DecimalPoint(_))
      if acceptDecimalPoint => {
        nextAcceptDecimalPoint = false
        nextTokens(nextTokens.length - 1) += nextLiteral.value
      }
      case (NumberLiteral(_), ParenRight(_))
      if nestDepth > 0      => {
        nextNestDepth -= 1
        nextAcceptDecimalPoint = true
        nextTokens = nextTokens :+ nextLiteral.value
      }

      case (BinaryOperator(_), NumberLiteral(_)) => {
        nextTokens = nextTokens :+ nextLiteral.value
      }
      case (BinaryOperator(_), ParenLeft(_))     => {
        nextNestDepth += 1
        nextTokens = nextTokens :+ nextLiteral.value
      }

      case (DecimalPoint(_), NumberLiteral(_)) => {
        nextTokens(nextTokens.length - 1) += nextLiteral.value
      }

      case (ParenLeft(_), NumberLiteral(_)) => {
        nextTokens = nextTokens :+ nextLiteral.value
      }
      case (ParenLeft(_), ParenLeft(_))     => {
        nextNestDepth += 1
        nextTokens = nextTokens :+ nextLiteral.value
      }
      case (ParenLeft(_), UnaryMinus(_))    => {
        nextTokens = nextTokens :+ nextLiteral.value
      }

      case (ParenRight(_), BinaryOperator(_)) => {
        nextTokens = nextTokens :+ nextLiteral.value
      }
      case (ParenRight(_), ParenRight(_))
      if nextNestDepth > 0  => {
        nextNestDepth -= 1
        nextTokens = nextTokens :+ nextLiteral.value
      }

      case (UnaryMinus(_), NumberLiteral(_)) => {
        nextTokens(nextTokens.length - 1) += nextLiteral.value
      }

      case (_, _) => return this
    }

    new CalculatorState(nextLiteral, nextNestDepth, nextAcceptDecimalPoint, nextTokens)
  }
}

class CalculatorInitialState extends CalculatorState(ParenLeft("("), 0, true, Array[String]())
