import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafx.scene.paint.Color._
//import scalafx.scene.shape.Rectangle
import scalafx.geometry.Insets
import scalafx.scene.layout.HBox
import scalafx.scene.text.Text


object Main extends JFXApp {
  stage = new JFXApp.PrimaryStage {
    title.value = "Calculator ScalaFX"
    width  = 600
    height = 450

    scene  = new Scene {
      fill = LightGreen
      content = new HBox {
        padding  = Insets(10)
        children = Seq(
          new Text {
            text = "1"
            style = "-fx-font-size: 48pt"
          },
          new Text {
            text = "2"
            style = "-fx-font-size: 48pt"
          })
      }
    }
  }
}
