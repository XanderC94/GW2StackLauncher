package controller

import events.OptionValuesEvent
import events.OptionValuesRequest
import model.utils.Nomenclatures
import tornadofx.*
import java.net.InetAddress
import java.util.*
import kotlin.streams.toList

class OptionValuesController : Controller(){ //--config-path="D:/Xander/Documenti/Projects/GW2StackLauncher/res/Config.json"

    init {
        subscribe<OptionValuesRequest.GetOptionValues> {
            when(it.id) {
                Nomenclatures.Options.assertserv -> {
                    fire(OptionValuesEvent.OptionValues(it, getAssetsServerList()))
                }
                Nomenclatures.Options.authserv -> {
//                    log.info("Banana")
                    fire(OptionValuesEvent.OptionValues(it, getAuthServerList()))
                }
                else -> {
                    fire(OptionValuesEvent.OptionValues(it, listOf()))
                }
            }
        }

        log.info("READY")
    }

    private fun getAssetsServerList() : List<Pair<String, Long>> {
        return getAddressesOf(Nomenclatures.URL.gw2AssetServHostname, 500)
    }

    private fun getAuthServerList() : List<Pair<String, Long>>{

        return getAddressesOf(Nomenclatures.URL.gw2OAuthServ1Hostname, 500) +
                getAddressesOf(Nomenclatures.URL.gw2OAuthServ2Hostname, 500)
    }

    private fun getAddressesOf(url:String, maxRTT:Int) : List<Pair<String, Long>> {
        return InetAddress.getAllByName(url).toList().parallelStream().map {
            it.hostAddress to getRoundTripTime(it, maxRTT)
        }.toList()
    }

    private fun getRoundTripTime(node:InetAddress, timeout:Int) : Long {

        val start = Date().time

        node.isReachable(timeout)

        return Date().time - start

    }

}