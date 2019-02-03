package model.objects

class Tuple(vararg val values: Any) {

    override fun toString(): String {

        return values.toList().map {
            it.toString()
        }.reduce { acc, s -> acc.plus(",$s") }
    }
}