package model.json.dumper

import com.google.gson.GsonBuilder
import java.nio.file.Path

object GenericJsonDumper : JsonDumper<Any>{

    private val gson = GsonBuilder().create()

    override fun dump(obj: Any): String {

        return gson.toJson(obj)
    }

    override fun dump(obj: Any, path: Path) {

        val file = path.toFile()

        file.writeText(this.dump(obj), Charsets.UTF_8)

    }


}