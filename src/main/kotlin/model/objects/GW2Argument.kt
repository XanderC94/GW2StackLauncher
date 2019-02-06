package model.objects

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

/**
 * POJO representation of a single command line argument
 *
 */
data class GW2Argument(val name: String,
                       val hasValue: Boolean,
                       var value: String,
                       val type: String,
                       val description: String,
                       var isActive: Boolean = false) {

    companion object : () -> GW2Argument, JsonDeserializer<GW2Argument> {

        override fun invoke(): GW2Argument {
            return GW2Argument("", false, "", "", "", false)
        }

        override fun deserialize(json: JsonElement?,
                                 typeOfT: Type?,
                                 context: JsonDeserializationContext?): GW2Argument {

            val jsonObject = json?.asJsonObject!!

            return GW2Argument(
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