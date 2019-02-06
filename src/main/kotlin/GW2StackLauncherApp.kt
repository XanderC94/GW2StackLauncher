import controller.AppController
import model.utils.Nomenclatures
import tornadofx.*
import view.GW2StackLauncherView

class GW2StackLauncherApp: App(GW2StackLauncherView::class) {

    private val appController: AppController by inject()

    init {
        importStylesheet(Nomenclatures.Directories.style + Nomenclatures.Files.GW2SLStyle)
    }

    override fun init() {

        super.init()

        appController.addProgramParameters(parameters.named)

    }



}