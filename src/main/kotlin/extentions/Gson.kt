package extentions

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

fun JsonObject.build(@NotNull property: String, @Nullable value: JsonElement) : JsonObject {
    this.add(property, value)
    return this
}

fun JsonArray.append(@Nullable values: List<String>?) : JsonArray {
    values?.forEach { this.add(JsonPrimitive(it)) }
    return this
}