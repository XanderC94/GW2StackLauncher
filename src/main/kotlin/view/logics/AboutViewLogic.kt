package view.logics

import events.AboutRequest
import javafx.event.EventHandler
import javafx.geometry.Pos
import model.utils.Nomenclatures
import tornadofx.*
import view.GW2StackLauncherView
import java.net.URL

class AboutViewLogic(view: GW2StackLauncherView) {

    private val dim = 64.0

    val gw2Link = view.imageview {
//        image = Image(this.javaClass
//                .getResourceAsStream(Directory.icon + File.GW2PoFLogo))
        isPreserveRatio = true
        fitHeight = dim
        fitWidth = dim
    }

    val githubLink = view.imageview {
//        image = Image(this.javaClass
//                .getResourceAsStream(Directory.icon + File.GitHubLogo))
        isPreserveRatio = true
        fitHeight = dim
        fitWidth = dim
        onMouseClicked = EventHandler {
            view.fire(AboutRequest.OpenLink(URL(Nomenclatures.URL.repo)))
        }
    }

    init {
        with(view) {

            iconsFlowPane.alignment = Pos.BASELINE_RIGHT

            iconsFlowPane.hgap = 30.0

            iconsFlowPane.add(gw2Link)
            iconsFlowPane.add(githubLink)

        }
    }

}