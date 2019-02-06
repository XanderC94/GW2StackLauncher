package model.objects

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

data class GW2SLConfig(
        val argumentListLocation: String,
        val addOnListLocation: String
    ) {

    companion object Adapter : JsonDeserializer<GW2SLConfig> {

        override fun deserialize(json: JsonElement?,
                                 typeOfT: Type?,
                                 context: JsonDeserializationContext?): GW2SLConfig {

            val jsonObject = json?.asJsonObject!!

            return GW2SLConfig(
                    jsonObject["argumentListLocation"].asString,
                    jsonObject["addOnListLocation"].asString
            )
        }

    }
}