package model.ontologies.gw2

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

/**
 * POJO representation of a single command line argument
 *
 */
data class Argument(val name: String,
                    val hasValue: Boolean,
                    var value: String,
                    val type: String,
                    val description: String,
                    var isActive: Boolean = false) {

    companion object : () -> Argument, JsonDeserializer<Argument> {

        override fun invoke(): Argument {
            return Argument("", false, "", "", "", false)
        }

        override fun deserialize(json: JsonElement?,
                                 typeOfT: Type?,
                                 context: JsonDeserializationContext?): Argument {

            val jsonObject = json?.asJsonObject!!

            return Argument(
                    jsonObject["name"].asString,
                    jsonObject["hasValue"].asBoolean,
                    jsonObject["value"].asString,
                    jsonObject["type"].asString,
                    jsonObject["description"].asString,
                    false
            )
        }
    }
}