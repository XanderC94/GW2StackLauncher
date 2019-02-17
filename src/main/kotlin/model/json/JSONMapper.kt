package model.json

import com.google.gson.GsonBuilder
import model.Mapper
import model.ontologies.GitHubAPISimple
import model.ontologies.gw2.AddOn
import model.ontologies.gw2.Argument

object JSONMapper : Mapper {

    private val gson = GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(AddOn::class.java, AddOn)
            .registerTypeAdapter(Argument::class.java, Argument)
            .registerTypeAdapter(GitHubAPISimple::class.java, GitHubAPISimple)
            .create()

    override fun <T> from(string: String, toClazz: Class<T>): T {
        return gson.fromJson(string, toClazz)
    }

    override fun <T> toString(obj: T): String {
        return gson.toJson(obj)
    }
}