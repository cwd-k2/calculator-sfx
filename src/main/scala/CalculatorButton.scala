import scalafx.event.ActionEvent

class CalculatorButton(val text: String, calculator: Calculator)
  extends javafx.scene.control.Button {
    onAction = (e: ActionEvent) => calculator.input(text))
}
