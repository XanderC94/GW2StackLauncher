package controller

import tornadofx.*

abstract class ViewController : Controller() {

    protected open fun initViewElements() {
        log.info("${this.javaClass.simpleName} READY!")
    }

}