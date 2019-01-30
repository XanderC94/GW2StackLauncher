package model.json.objects

/**
 * POJO representation of a single command line argument
 *
 */
data class GW2Argument(val name: String,
                       val hasValue: Boolean,
                       var value: String,
                       val type: String,
                       val description: String, var isActive: Boolean = false)