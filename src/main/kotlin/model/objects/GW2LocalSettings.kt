package model.objects

data class GW2LocalSettings(var arguments:List<String>) {

    companion object : () -> GW2LocalSettings {
        override fun invoke(): GW2LocalSettings {
            return GW2LocalSettings(listOf())
        }
    }
}
