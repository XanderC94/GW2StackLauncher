package controller

import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Logger {
    private val logger = LoggerFactory.getLogger("GW2StackLogger")

    fun instance() : Logger {
        return logger
    }
}