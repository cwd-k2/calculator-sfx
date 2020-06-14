import scala.collection.mutable.Stack
import scala.collection.mutable.Queue

import scalafx.scene.control.TextField

class Calculator {
  private var state: CalculatorState = new CalculatorInitialState()
  private var stateArchive: Stack[CalculatorState] = new Stack[CalculatorState]()
          val output: TextField = new TextField { editable = false }

  def input(a: String): Unit = {
    // aに応じてなんか色々
    a match {
      case "DEL" => rollback
      case "AC"  => allclear
      case "="   => if (state.isReady) calculate
      case _     => commit(a)
    }
  }

  private def display(str: String): Unit = {
      output.text = str
  }

  private def rollback: Unit = {
    if (stateArchive.nonEmpty) state = stateArchive.pop
    display(state.currentTokens.mkString)
  }

  private def allclear: Unit = {
    state = new CalculatorInitialState()
    stateArchive = new Stack[CalculatorState]()
    display(state.currentTokens.mkString)
  }

  private def commit(a: String): Unit = {
    val newState = state.update(a)
    if (state != newState) {
      stateArchive.push(state)
      state = newState
    }
    display(state.currentTokens.mkString)
  }

  // 逆ポーランド記法に変換してから計算する
  // 逆ポーランド記法の何が嬉しいかっていうのはまあ調べてください...
  // 値は途中で BigDecimal として計算するが返り値は String
  // やりたいこと
  // 1 * (2 + 3) / 4 => 1 2 3 + 4 / * というように変換する
  private def calculate: Unit = {
    val stack: Stack[String] = Stack[String]()
    val queue: Queue[String] = Queue[String]()

    val tokens = state.currentTokens

    // トークンを演算子と数値に分けたりする
    for (token <- tokens) {
      token match {
        case "+" | "-" | "*" | "/" => stack.push(token)
        case "("                   => ;
        case ")"                   => queue.enqueue(stack.pop)
        case _                     => queue.enqueue(token)
      }
    }

    // 逆ポーランド記法になるように演算子を追加していく
    while (stack.nonEmpty) queue += stack.pop

    try {
      val nStack: Stack[BigDecimal] = Stack[BigDecimal]()
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
              case "/" => nStack.push(lhs / rhs)
            }
          }
          case _ => nStack.push(BigDecimal(token))
        }
      }
      allclear
      commit(nStack.pop.toString)
      display(state.currentTokens.mkString)
    } catch {
      case _: Exception  => {
        allclear
        display("ZeroDivisionError!")
      }
    }

  }

}
