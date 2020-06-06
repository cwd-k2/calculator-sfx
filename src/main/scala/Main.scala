import scalafx.Includes._

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage

import scalafx.scene.Scene
import scalafx.scene.control.Button
import scalafx.scene.layout.VBox
import scalafx.scene.layout.GridPane

import scalafx.event.ActionEvent
import scalafx.event.EventHandler

object Main extends JFXApp {
  def makeButton(a: String): Button =
    new Button {
      text = a
      //onAction = (e: ActionEvent) => println(a)
      onAction = (e: ActionEvent) => a match {
        case "="    =>  println(input.mkString)
        case "DEL"  =>  input = input.dropRight(1)
        case "AC"   =>  input = Array.empty
        case _      =>  input = input :+ a
      }
    }

  val buttonGrid = new GridPane
  var input: Array[String] = Array.empty
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
        children = buttonGrid
      }
    }
  }
}
