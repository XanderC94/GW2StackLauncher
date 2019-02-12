package view.components

import events.SearchRequest
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyEvent
import javafx.stage.Stage
import view.GW2SLSearch

enum class KeyAction(
        override val combo : KeyCodeCombination,
        override val strategy : (Any) -> Unit) : Action<Any>{

    FullScreen(KeyCodeCombination(KeyCode.F11), { stage ->
        if (stage is Stage) {
            stage.isFullScreen = !stage.isFullScreen
        }

    }),
    Find(KeyCodeCombination(KeyCode.ENTER), { search ->
        if (search is GW2SLSearch) {
            search.fire(SearchRequest.Find(search.searchField.text, search.matchCase.isSelected, search.isNext))
        }
    });

    override fun invoke(stage: Any) {
        this.strategy(stage)
    }

    companion object : (Any, KeyEvent) -> Unit {

        override fun invoke(browser: Any, combo: KeyEvent) {
            val action = get(combo)
            if (action != null) {
                action(browser)
            }
        }

        private fun get(event: KeyEvent) : KeyAction? {
            return KeyAction.values().firstOrNull { it.combo.match(event) }
        }
    }
}