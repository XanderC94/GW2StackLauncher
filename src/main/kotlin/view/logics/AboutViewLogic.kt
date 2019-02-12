package view.logics

import events.BrowserRequest
import javafx.event.EventHandler
import model.utils.Nomenclatures
import view.GW2SLMainView

class AboutViewLogic(view: GW2SLMainView) {

    init {
        with(view) {

            aboutTab.onSelectionChanged = EventHandler {
                if (aboutTab.isSelected) {
                    fire(BrowserRequest.LoadURL(Nomenclatures.URL.repo))
                }
            }

        }
    }

}