package controller

import events.AppRequest
import extentions.asFile
import extentions.getResourceAsText
import model.ontologies.Overlay
import model.ontologies.gw2.Application
import java.nio.charset.Charset

class GW2Runner : ViewController(), GW2Dipper {

    private lateinit var gw2: Application

    init {

        subscribe<AppRequest.RunGW2> {
            createGW2SLBatch(it.overlays, "Guild Wars 2" to Overlay(gw2.installPath.value, gw2.execCmd.value))

            //ToDo: "Run GW2 through the batch file."
        }

    }

    override fun setGW2Application(gw2: Application) {
        this.gw2 = gw2
    }


    private fun createStartString(name: String, dir: String, exe: String) : String {
        return "ECHO Starting \"$name\"...\n" +
                "START \"$name\" /D \"$dir\" \"$exe\"\n" +
                "timeout 5 > NUL\n\n"
    }

    private fun createGW2SLBatch(overlays: Map<String, Overlay>, game: Pair<String, Overlay>) {

        var bat = this.getResourceAsText("/run.gw2.template.bat").reader().readText() + "\n"

        bat += createStartString(game.first, game.second.dir, game.second.exe)

        overlays.forEach {
            bat += createStartString(it.key, it.value.dir, it.value.exe)
        }

        "${gw2.installPath.value}/gw2sl.bat".asFile().writeText(bat, Charset.defaultCharset())
    }
}