package model.objects

data class GW2LocalAddOns(val addOns: List<String>) {
    companion object : () -> GW2LocalAddOns {
        override fun invoke(): GW2LocalAddOns {
            return GW2LocalAddOns(listOf())
        }
    }
}