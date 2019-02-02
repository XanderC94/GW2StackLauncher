package view

enum class ToggleStatus(val signature: String) : () -> String {
    ON("ON"), OFF("OFF");

    override fun invoke(): String {
        return signature
    }
}