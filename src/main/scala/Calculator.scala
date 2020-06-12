class Calculator {
  private var state = State

  def input(a: String): Unit = {
    // aに応じてなんか色々
  }

  // 逆ポーランド記法に変換してから計算する
  // 逆ポーランド記法の何が嬉しいかっていうのはまあ調べてください...
  // 値は途中で BigDecimal として計算するが返り値は String
  // やりたいこと
  // 1 * (2 + 3) / 4 => 1 2 3 + 4 / * というように変換する
  def calculate(inputs: Array[String]): String = {
    val stack: scala.collection.mutable.Stack[String] = scala.collection.mutable.Stack[String]()
    val queue: scala.collection.mutable.Queue[String] = scala.collection.mutable.Queue[String]()

    var tmpStr: String = ""

    // 共通の処理
    // 数値として扱われるべき文字列を一時的に tmpStr として保存しているが
    // 他の演算子とかが出てきた時にちゃんと一つの塊として認識して計算用のキューに追加する感じです（？）
    def appendNumStr = if (tmpStr.nonEmpty) { queue += tmpStr; tmpStr = "" }

    // トークンを演算子と数値に分けたりする
    for (token <- inputs) {
      token match {
        case "+" | "-" | "*" | "/" => appendNumStr; stack.push(token)
        case "("                   => appendNumStr
        case ")"                   => appendNumStr; queue += stack.pop
        case "="                   => appendNumStr
        case _                     => tmpStr += token
      }
    }

    // 逆ポーランド記法になるように演算子を追加していく
    try {
      while (stack.nonEmpty) queue += stack.pop

      val nStack: scala.collection.mutable.Stack[BigDecimal] = scala.collection.mutable.Stack[BigDecimal]()

      while (queue.nonEmpty) {
        val token = queue.dequeue
        token match {
          case "+" | "-" | "*" | "/" => {
            val rhs = nStack.pop
            val lhs = nStack.pop
            token match {
              case "+" => nStack.push(lhs + rhs)
              case "-" => nStack.push(lhs - rhs)
              case "*" => nStack.push(lhs * rhs)
              case "/" => if(rhs != 0) nStack.push(lhs / rhs) else throw new Exception
            }
          }
          case _ => nStack.push(BigDecimal(token))
        }
      }

      nStack.pop.toString
    } catch {
      case _: Exception  =>  "devided 0"
    }
  }

}