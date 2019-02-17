package model.json

import com.google.gson.GsonBuilder
import model.Mapper
import model.objects.GW2AddOn
import model.objects.GW2Argument
import model.objects.GitHubAPISimple

object JSONMapper : Mapper {

    private val gson = GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(GW2AddOn::class.java, GW2AddOn)
            .registerTypeAdapter(GW2Argument::class.java, GW2Argument)
            .registerTypeAdapter(GitHubAPISimple::class.java, GitHubAPISimple)
            .create()

    override fun <T> from(string: String, toClazz: Class<T>): T {
        return gson.fromJson(string, toClazz)
    }

    override fun <T> toString(obj: T): String {
        return gson.toJson(obj)
    }
}