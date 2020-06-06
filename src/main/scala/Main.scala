import scala.collection.mutable.Queue
import scala.collection.mutable.Stack

import scalafx.Includes._

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage

import scalafx.scene.Scene
import scalafx.scene.control.Button
import scalafx.scene.layout.VBox
import scalafx.scene.layout.GridPane
import scalafx.scene.control.TextField

import scalafx.event.ActionEvent
import scalafx.event.EventHandler

object Main extends JFXApp {
  var input: Array[String] = Array.empty

  def makeButton(a: String): Button = {
    new Button {
      text = a
      onAction = (e: ActionEvent) => {
        a match {
          case "="    => input = calculate(input :+ "=").split("")
          case "DEL"  => input = input.dropRight(1)
          case "AC"   => input = Array.empty
          case _      => input = input :+ a
        }
        //println(input.mkString)
        textField.text = input.mkString
      }
    }
  }

  // 逆ポーランド記法に変換してから計算する
  // 逆ポーランド記法の何が嬉しいかっていうのはまあ調べてください...
  // 値は途中で BigDecimal として計算するが返り値は String
  // やりたいこと
  // 1 * (2 + 3) / 4 => 1 2 3 + 4 / * というように変換する
  def calculate(input: Array[String]): String = {
    val stack: scala.collection.mutable.Stack[String] = scala.collection.mutable.Stack[String]()
    val queue: scala.collection.mutable.Queue[String] = scala.collection.mutable.Queue[String]()

    var tmpStr: String = ""

    // 共通の処理
    // 数値として扱われるべき文字列を一時的に tmpStr として保存しているが
    // 他の演算子とかが出てきた時にちゃんと一つの塊として認識して計算用のキューに追加する感じです（？）
    def appendNumStr = if (tmpStr.nonEmpty) { queue += tmpStr; tmpStr = "" }

    // トークンを演算子と数値に分けたりする
    for (token <- input) {
      token match {
        case "+" | "-" | "*" | "/" => appendNumStr; stack.push(token)
        case "("                   => appendNumStr
        case ")"                   => appendNumStr; queue += stack.pop
        case "="                   => appendNumStr
        case _                     => tmpStr += token
      }
    }

    // 逆ポーランド記法になるように演算子を追加していく
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
            case "/" => nStack.push(lhs / rhs)
          }
        }
        case _ => nStack.push(BigDecimal(token))
      }
    }

    nStack.pop.toString
  }

  val textField = new TextField {
    text = "text field"
    editable = false
  }
  val buttonGrid = new GridPane
  buttonGrid.add(makeButton("("),   0, 0, 1, 1)
  buttonGrid.add(makeButton(")"),   1, 0, 1, 1)
  buttonGrid.add(makeButton("DEL"), 2, 0, 1, 1)
  buttonGrid.add(makeButton("AC"),  3, 0, 1, 1)

  buttonGrid.add(makeButton("7"), 0, 1, 1, 1)
  buttonGrid.add(makeButton("8"), 1, 1, 1, 1)
  buttonGrid.add(makeButton("9"), 2, 1, 1, 1)
  buttonGrid.add(makeButton("/"), 3, 1, 1, 1)

  buttonGrid.add(makeButton("4"), 0, 2, 1, 1)
  buttonGrid.add(makeButton("5"), 1, 2, 1, 1)
  buttonGrid.add(makeButton("6"), 2, 2, 1, 1)
  buttonGrid.add(makeButton("*"), 3, 2, 1, 1)

  buttonGrid.add(makeButton("1"), 0, 3, 1, 1)
  buttonGrid.add(makeButton("2"), 1, 3, 1, 1)
  buttonGrid.add(makeButton("3"), 2, 3, 1, 1)
  buttonGrid.add(makeButton("-"), 3, 3, 1, 1)

  buttonGrid.add(makeButton("0"), 0, 4, 1, 1)
  buttonGrid.add(makeButton("."), 1, 4, 1, 1)
  buttonGrid.add(makeButton("="), 2, 4, 1, 1)
  buttonGrid.add(makeButton("+"), 3, 4, 1, 1)

  stage = new PrimaryStage {
    title = "Calculator with ScalaFX"
    scene = new Scene {
      root = new VBox {
        children = Seq(textField, buttonGrid)
      }
    }
  }
}
