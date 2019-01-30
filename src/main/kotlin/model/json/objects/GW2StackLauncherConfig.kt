package model.json.objects

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type
import java.net.URL
import java.nio.file.Path
import java.nio.file.Paths

data class GW2StackLauncherConfig(
        val argumentsListPath: Path,
        val addonsListURL: URL
    ) {

    companion object Adapter : JsonDeserializer<GW2StackLauncherConfig> {

        override fun deserialize(json: JsonElement?,
                                 typeOfT: Type?,
                                 context: JsonDeserializationContext?): GW2StackLauncherConfig {

            val jsonObject = json?.asJsonObject

            return GW2StackLauncherConfig(
                    Paths.get(jsonObject?.get("argumentsListPath")?.asString),
                    URL(jsonObject?.get("addonsListURL")?.asString)
            )
        }

    }
}