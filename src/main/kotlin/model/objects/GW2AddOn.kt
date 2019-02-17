package model.objects

import com.google.gson.*
import model.utils.append
import model.utils.build
import java.lang.reflect.Type

data class GW2AddOn(
        val name: String,
        val url: String,
        val info: String,
        val signature: String,
        val exe: String,
        val type: String,
        val chainloader: String,
        val canChainload: Boolean,
        val canBeChainloaded: Boolean,
        val chainloadName: String,
        val bindings: List<String>,
        var isActive: Boolean
) {
    companion object : () -> GW2AddOn, JsonDeserializer<GW2AddOn>, JsonSerializer<GW2AddOn> {

        override fun serialize(src: GW2AddOn?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
            return JsonObject()
                    .build("name", JsonPrimitive(src?.name))
                    .build("url",JsonPrimitive(src?.url))
                    .build("info",JsonPrimitive(src?.info))
                    .build("signature",JsonPrimitive(src?.signature))
                    .build("exe",JsonPrimitive(src?.exe))
                    .build("type",JsonPrimitive(src?.type))
                    .build("chainloadName",JsonPrimitive(src?.chainloadName))
                    .build("chainloader",JsonPrimitive(src?.chainloader))
                    .build("canChainload",JsonPrimitive(src?.canChainload))
                    .build("canBeChainloaded",JsonPrimitive(src?.canBeChainloaded))
                    .build("bindings", JsonArray().append(src?.bindings))
        }

        override fun deserialize(json: JsonElement?,
                                 typeOfT: Type?,
                                 context: JsonDeserializationContext?): GW2AddOn {

            val jsonObject = json?.asJsonObject!!

            return GW2AddOn(
                    name = jsonObject["name"].asString,
                    url = jsonObject["url"].asString,
                    info = jsonObject["info"].asString,
                    signature = jsonObject["signature"].asString,
                    chainloadName = jsonObject["chainloadName"].asString,
                    chainloader = jsonObject["chainloader"].asString,
                    canChainload = jsonObject["canChainload"].asBoolean,
                    canBeChainloaded = jsonObject["canBeChainloaded"].asBoolean,
                    type = jsonObject["type"].asString,
                    exe = if (jsonObject.has("exe")) jsonObject["exe"].asString else "",
                    bindings = jsonObject["bindings"].asJsonArray.map { it.asString },
                    isActive = false
            )
        }

        override fun invoke(): GW2AddOn {
            return GW2AddOn("","","","","","", "", false, false, "", listOf(), false)
        }
    }
}