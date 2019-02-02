package controller

import org.slf4j.Logger
import org.slf4j.impl.SimpleLoggerFactory
import view.GW2StackLauncherView

object Logger {
    private val logger = SimpleLoggerFactory().getLogger(GW2StackLauncherView::class.java.name)

    fun instance() : Logger {
        return logger
    }
}