/*
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.effect.DropShadow
import scalafx.scene.layout.HBox
import scalafx.scene.paint.Color._
import scalafx.scene.paint.{Stops, LinearGradient}
import scalafx.scene.text.Text

object Main extends JFXApp {

  stage = new PrimaryStage {
    title = "ScalaFX Hello World"
    scene = new Scene {
      fill = Black
      content = new HBox {
        padding = Insets(20)
        children = Seq(
          new Text {
            text = "Hello "
            style = "-fx-font-size: 48pt"
            fill = new LinearGradient(
              endX = 0,
              stops = Stops(PaleGreen, SeaGreen))
          },
          new Text {
            text = "World!!!"
            style = "-fx-font-size: 48pt"
            fill = new LinearGradient(
              endX = 0,
              stops = Stops(Cyan, DodgerBlue)
            )
            effect = new DropShadow {
              color = DodgerBlue
              radius = 25
              spread = 0.25
            }
          }
        )
      }
    }
  }
}
*/

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.control.Button
import scalafx.scene.layout.VBox

import scalafx.scene.layout.GridPane

//val all_buttons = ("1", "2", "3", "4",
//                  "5", "6", "7", "8",
//                  "9", "0", "/", "*",
//                  "-", "+", "=", "C")

object Main extends JFXApp {
  stage = new PrimaryStage {
    title = "Hello"
    scene = new Scene {
      root = new VBox {
        /*
        children = new Button("Hello Button") {
          onMouseClicked = handle {
            println("hello")
          }
        }
        */
        children = Seq(
          GridPane = {
            for(button <- ("1", "2", "3", "4",
            "5", "6", "7", "8",
            "9", "0", "/", "*",
            "-", "+", "=", "C")) yield new Button(button)
          }
          //new Button("("),
          //new Button(")"),
        )
      }
    }
  }
}