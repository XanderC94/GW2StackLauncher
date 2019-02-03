package model.objects

/**
 * POJO representation of a single command line argument
 *
 */
data class GW2Argument(val name: String,
                       val hasValue: Boolean,
                       var value: String,
                       val type: String,
                       val description: String, var isActive: Boolean = false) {

    companion object Empty : () -> GW2Argument{
        override fun invoke(): GW2Argument {
            return GW2Argument("", false, "", "", "", false)
        }
    }
}