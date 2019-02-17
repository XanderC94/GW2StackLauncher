package model.objects

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

data class GitHubAPISimple(
        val publishDate : String,
        val signature: String,
        val url : String
) {
    companion object : () -> GitHubAPISimple, JsonDeserializer<GitHubAPISimple> {

        override fun invoke(): GitHubAPISimple {
            return GitHubAPISimple("","","")
        }

        override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): GitHubAPISimple {

            val obj = if (json!!.isJsonObject) {
                json.asJsonObject
            } else {
                json.asJsonArray.sortedByDescending {
                    it.asJsonObject["published_at"].asString
                }[0].asJsonObject
            }

            if (obj.has("message") && obj["message"].asString.contains("Not Found")) {
                return GitHubAPISimple()
            }

            val assets = obj["assets"].asJsonArray[0].asJsonObject

            return GitHubAPISimple(
                    obj["published_at"].asString,
                    obj["node_id"].asString,
                    assets["browser_download_url"].asString
            )

        }
    }
}