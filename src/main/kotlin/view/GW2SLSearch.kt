package view

import events.SearchRequest
import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.control.TextField
import javafx.scene.layout.FlowPane
import tornadofx.*
import view.components.KeyAction


object GW2SLSearch : Fragment("Search"){

    override val root : FlowPane by fxml()

    val searchField : TextField by fxid("SearchField")
    val matchCase : CheckBox by fxid("MatchCase")
    val next : Button by fxid("Next")

    var isNext = false

    init {
        handlers()
        subscriptions()
    }

    private fun handlers() {
        searchField.onKeyPressed = EventHandler {
            KeyAction(this@GW2SLSearch, it)
            if (!isNext) {
                isNext = true
            }
        }

        next.action {
            isNext = true
            KeyAction.Find(this@GW2SLSearch)
        }
    }

    private fun subscriptions() {
        subscribe<SearchRequest.Open> {
            synchronized(this) {
                searchField.text = it.string
                isNext = false
                KeyAction.Find(this@GW2SLSearch)
            }
        }
    }
}