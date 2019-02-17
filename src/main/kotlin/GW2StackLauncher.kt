import controller.AppController
import extentions.asFile
import extentions.isDir
import model.ontologies.GW2SLConfig
import model.utils.Nomenclatures
import model.utils.SystemUtils
import tornadofx.*
import view.GW2SLMainView

class GW2StackLauncher: App(GW2SLMainView::class) {

    private lateinit var appController : AppController

    override fun init() {

        super.init()

        val localConfig = GW2SLConfig.from("/${Nomenclatures.Files.GW2SLConfigJson}", isClassPath = true)!!

        val extConfig = GW2SLConfig.from(parameters.named["config-path"])

        val coherentConfig = mergeConfigs(localConfig, extConfig)

        importStylesheet(getStyleSheet(coherentConfig.mainStyle))

        createAppEnvironment()

        appController = AppController(coherentConfig)

    }

    private fun mergeConfigs(local: GW2SLConfig, ext: GW2SLConfig?) : GW2SLConfig {

        if (ext == null) {
            return local
        }

        return GW2SLConfig(
                argumentListLocation = if (ext.argumentListLocation.isNotEmpty())
                    ext.argumentListLocation else local.argumentListLocation,
                addOnListLocation = if (ext.addOnListLocation.isNotEmpty())
                    ext.addOnListLocation else local.addOnListLocation,
                userAgent = if (ext.userAgent.isNotEmpty()) ext.userAgent else local.userAgent,
                mainStyle = if (ext.mainStyle.isNotEmpty()) ext.mainStyle else local.mainStyle
        )
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

    private fun getStyleSheet(location: String) : String {
        return if (location.isNotEmpty()) {
            location
        } else {
            "${Nomenclatures.Directories.style}/${Nomenclatures.Files.GW2SLStyle}"
        }
    }
}