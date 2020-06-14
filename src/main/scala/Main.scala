import scalafx.Includes._

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage

import scalafx.scene.Scene

import scalafx.scene.layout.VBox
import scalafx.scene.layout.GridPane

import scalafx.scene.control.Button
import scalafx.scene.control.TextField

import scalafx.event.ActionEvent

import scala.util.Try

object Main extends JFXApp {
  val calculator = new Calculator

  def makeButton(a: String) = new Button {
    text = a
    onAction = (e: ActionEvent) => calculator.input(a)
  }

  val textField = calculator.output

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
