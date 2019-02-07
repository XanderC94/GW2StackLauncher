import controller.AppController
import model.utils.Nomenclatures
import tornadofx.*
import view.GW2StackLauncherView

class GW2StackLauncherApp: App(GW2StackLauncherView::class) {

    private lateinit var appController : AppController

    init {
        importStylesheet(Nomenclatures.Directory.style + Nomenclatures.File.GW2SLStyle)
    }

    override fun init() {

        super.init()

        appController = AppController(parameters.named)

    }
}