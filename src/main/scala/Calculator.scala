import scala.collection.immutable.Vector
import scala.collection.mutable.Stack

import scalafx.scene.control.TextField

class Calculator(val output: TextField) {
  private var state: CalculatorState = CalculatorInitialState
  private var archive: Stack[CalculatorState] = Stack[CalculatorState]()

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
    if (archive.nonEmpty) state = archive.pop
    display(state.toString)
  }

  private def allclear: Unit = {
    state = CalculatorInitialState
    archive = new Stack[CalculatorState]()
    display(state.toString)
  }

  private def commit(a: String): Unit = {
    val newState = state.update(a)
    if (state != newState) {
      archive.push(state)
      state = newState
    }
    display(state.toString)
  }

  // 逆ポーランド記法に変換してから計算する
  // 値は途中で BigDecimal として計算する
  private def calculate: Unit = {
    val queue = state.toIPoland
    val stack = Stack[BigDecimal]()

    allclear

    for (token <- queue) {
      token match {
        case "+" => {
          val rhs, lhs = stack.pop
          stack.push(lhs + rhs)
        }
        case "-" => {
          val rhs, lhs = stack.pop
          stack.push(lhs - rhs)
        }
        case "*" => {
          val rhs, lhs = stack.pop
          stack.push(lhs * rhs)
        }
        case "/" => {
          val rhs, lhs = stack.pop

          if (rhs == 0) // めんどくさい...
            return display("ZeroDivisionError!")

          stack.push(lhs / rhs)
        }
        case _ => stack.push(BigDecimal(token))
      }
    }

    commit(stack.pop.toString)
    display(state.toString)
  }
}
