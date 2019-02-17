package extentions

import javafx.scene.control.ListView
import javafx.scene.control.TextField
import model.ontologies.gw2.Argument

fun TextField.isValueSettable(last: Argument, maxChars:Int): Boolean {
    return this.text.isNotEmpty() &&
            this.text.length < maxChars
            && last.hasValue && last.isActive
}

fun TextField.isStatusCorrect(last: Argument) : Boolean {
    return !last.hasValue || last.hasValue &&
            (this.text.isEmpty() && !last.isActive ||
                    !this.text.isEmpty() && last.isActive)
}

fun ListView<Pair<String, Boolean>>.selectAndFocus(to: String, then : (String) -> Unit) : Int{
    val idx = this.items.indexOfFirst { it.first == to }
    if (idx > -1) {
        this.selectionModel.select(idx)
        this.focusModel.focus(idx)
        then(to)
    }
    return idx
}

fun ListView<Pair<String, Boolean>>.selectFocusAndScroll(to: String, then: (String) -> Unit) : Int {
    val idx = this.selectAndFocus(to, then)
    this.scrollTo(if (idx > 1) idx - 1 else idx)
    return idx
}