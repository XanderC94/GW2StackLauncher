package view.components

enum class ToggleStatus : () -> String {
    ON, OFF;

    override fun invoke(): String {
        return this.name
    }
}