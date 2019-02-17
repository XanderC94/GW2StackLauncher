import controller.AppController
import extentions.*
import model.ontologies.GW2SLConfig
import model.utils.Nomenclatures
import model.utils.SystemUtils
import tornadofx.*
import view.GW2SLMainView

class GW2StackLauncher: App(GW2SLMainView::class) {

    private lateinit var appController : AppController

    override fun init() {

        super.init()

        val appConfig : GW2SLConfig = if (
                parameters.named.isNotEmpty() &&
                parameters.named.containsKey("config-path") &&
                parameters.named["config-path"]!!.isFile()) {

            parameters.named["config-path"]!!.asFile().readText().fromJson()
        } else {
            this.getResourceAsText("/${Nomenclatures.File.GW2SLConfigJson}").fromJson()
        }

        importStylesheet(appConfig.mainStyle)

        createAppEnvironment()

        appController = AppController(appConfig)

    }

    private fun createAppEnvironment() {

        val dataDir = SystemUtils.dataDir()

        if (dataDir != null && dataDir.isDir()) {
            val appDir = SystemUtils.gw2slDir()
            if (appDir!= null && !appDir.isDir()) {

                appDir.asFile().mkdir()
                "$appDir/tmp".asFile().mkdir()

            }
        }
    }
}