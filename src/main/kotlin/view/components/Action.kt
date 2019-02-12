package view.components

import javafx.scene.input.KeyCodeCombination

interface Action<T> : (T) -> Unit {
    val combo : KeyCodeCombination
    val strategy : (T) -> Unit
}