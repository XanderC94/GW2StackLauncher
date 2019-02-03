package model.json.dumper

import com.google.gson.GsonBuilder
import model.Dumper
import java.nio.charset.Charset
import java.nio.file.Path

object GenericJsonDumper : Dumper<Any> {

    private val gson = GsonBuilder().setPrettyPrinting().create()

    override fun dump(obj: Any): String {

        return gson.toJson(obj)
    }

    override fun dump(obj: Any, path: Path, charset: Charset) {

        try {
            path.toFile().writeText(this.dump(obj), charset)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

    }


}