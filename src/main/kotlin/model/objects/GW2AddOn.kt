package model.objects

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

data class GW2AddOn(
        val name: String,
        val url: String,
        val info: String,
        val md5: String,
        val chainloadName: String,
        val type: String,
        var isActive: Boolean
) {
    companion object : () -> GW2AddOn, JsonDeserializer<GW2AddOn> {

        override fun deserialize(json: JsonElement?,
                                 typeOfT: Type?,
                                 context: JsonDeserializationContext?): GW2AddOn {

            val jsonObject = json?.asJsonObject!!

            return GW2AddOn(
                    jsonObject["name"].asString,
                    jsonObject["url"].asString,
                    jsonObject["info"].asString,
                    jsonObject["md5"].asString,
                    jsonObject["chainloadName"].asString,
                    jsonObject["type"].asString,
                    false
            )
        }

        override fun invoke(): GW2AddOn {
            return GW2AddOn("","","","","","", false)
        }
    }
}