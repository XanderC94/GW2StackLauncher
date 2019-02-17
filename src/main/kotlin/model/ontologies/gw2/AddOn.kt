package model.ontologies.gw2

import com.google.gson.*
import controller.networking.HTTP
import extentions.append
import extentions.asMetadata
import extentions.build
import extentions.replaceIf
import model.ontologies.AddOnMetadata
import model.ontologies.GitHubAPISimple
import java.lang.reflect.Type

data class AddOn(
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

    fun asMetadata() : AddOnMetadata {
        return when {
            this.signature.contains("md5", true) -> {
                getMD5Signature(this.signature) to this.url
            }
            this.url.contains("api.github.com", true) -> {
                val api = GitHubAPISimple.from(this.url)
                api.publishDate to api.url
            }
            else -> {
                val version = parseAddOnVersion(this.info)
                val url = this.url.replaceIf {
                    version.isNotEmpty()
                } (AddOnMetadata.versionInURLPattern.toRegex(), version)

                version to url
            }
        }.asMetadata()
    }

    companion object : () -> AddOn, JsonDeserializer<AddOn>, JsonSerializer<AddOn> {


        override fun invoke(): AddOn {
            return AddOn("", "", "", "", "", "", "", false, false, "", listOf(), false)
        }

        override fun serialize(src: AddOn?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
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
                                 context: JsonDeserializationContext?): AddOn {

            val jsonObject = json?.asJsonObject!!

            return AddOn(
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

        private fun getMD5Signature(url: String) : String {
            return HTTP.GET(url).body()!!.string().split(" ").first()
        }

        private fun parseAddOnVersion(url: String) : String {
            return {
                val html = HTTP.GET(url).body()!!.string()
                val matcher = AddOnMetadata.versionInPagePattern.matcher(html)
                if (matcher.find()) matcher.group(1).replace(".", "_") else ""
            }()
        }
    }
}