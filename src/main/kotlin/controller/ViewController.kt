package controller

import extentions.className
import tornadofx.*

abstract class ViewController : Controller() {

    protected open fun onReady() {
        log.info("${this.className()} READY!")
    }

}