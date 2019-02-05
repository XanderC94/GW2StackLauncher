package view.components

import javafx.scene.control.ListCell
import javafx.scene.layout.BorderPane
import tornadofx.*

class ListViewItem(val toggle: (String, Boolean) -> Unit) : ListCell<Pair<String, Boolean>>() {

    val label = label(""){}

    val toggleButton = togglebutton(text = ToggleStatus.OFF(), selectFirst = false) {
        action {
            this.text = if (this.isSelected) ToggleStatus.ON() else ToggleStatus.OFF()
            toggle(label.text, this.isSelected)
        }
    }

    val pane: BorderPane = borderpane {
        left = label
        right = toggleButton
        paddingAll = 5.0
    }

    override fun updateItem(item: Pair<String, Boolean>?, empty: Boolean) {
        super.updateItem(item, empty)
        if (!empty) {
            this.label.text = item!!.first
            this.toggleButton.isSelected = item.second
            this.toggleButton.text = if (item.second) ToggleStatus.ON() else ToggleStatus.OFF()
            this.graphic = pane
        }
    }
}