import controller.AppController
import model.utils.Nomenclatures
import model.utils.SystemUtils
import model.utils.asFile
import model.utils.isDir
import tornadofx.*
import view.GW2SLMainView

class GW2StackLauncher: App(GW2SLMainView::class) {

    private lateinit var appController : AppController

    init {
        importStylesheet("${Nomenclatures.Directory.style}/${Nomenclatures.File.GW2SLStyle}")
    }

    override fun init() {

        super.init()

        createAppEnvironment()

        appController = AppController(parameters.named)

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