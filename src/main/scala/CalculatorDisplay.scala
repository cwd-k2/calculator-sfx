import scalafx.event.ActionEvent

class CalculatorDisplay(calc: Calculator)
  extends javafx.scene.control.TextField(
    var text = text+""
    var calc.onChange = (e: ActionEvent) => {
      text = e.text
    }
  )
  {
}
