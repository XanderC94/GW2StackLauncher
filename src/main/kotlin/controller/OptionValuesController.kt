package controller

import events.OptionValuesEvent
import events.OptionValuesRequest
import model.objects.Application
import model.utils.Nomenclatures
import tornadofx.*
import java.io.File
import java.net.InetAddress
import java.util.*

class OptionValuesController : Controller(), GW2ApplicationController{ //--config-path="D:/Xander/Documenti/Projects/GW2StackLauncher/res/Config.json"

    private lateinit var gw2: Application

    init {
        subscribe<OptionValuesRequest.GetOptionValues> { request ->
            when(request.id) {
                Nomenclatures.Options.assertserv -> {

                    getAssetsServerList().parallelStream().forEach {
                        val p = it.hostAddress to getRoundTripTime(it, 500)
                        fire(OptionValuesEvent.OptionValue(request, p))
                    }
                }
                Nomenclatures.Options.authserv -> {

                    getAuthServerList().parallelStream().forEach {
                        val p = it.hostAddress to getRoundTripTime(it, 500)
                        fire(OptionValuesEvent.OptionValue(request, p))
                    }
                }
                Nomenclatures.Options.dat -> {

                    val gw2Dir = File(gw2.installPath.value)
                    if (gw2Dir.isDirectory) {
                        val list = gw2Dir.list { _, item -> item.endsWith(".dat") }.map {

                            "${gw2Dir.absolutePath.replace("\\", "/")}/$it"
                        }.map { it to File(it).length() / 1000000000f }

                        fire(OptionValuesEvent.OptionValues(request, list))
                    }
                }
                else -> { }
            }
        }

        log.info("READY")
    }

    private fun getAssetsServerList() : List<InetAddress> {
        return getAddressesOf(Nomenclatures.URLs.gw2AssetServHostname)
    }

    private fun getAuthServerList() : List<InetAddress>{

        return getAddressesOf(Nomenclatures.URLs.gw2OAuthServ1Hostname) +
                getAddressesOf(Nomenclatures.URLs.gw2OAuthServ2Hostname)
    }

    private fun getAddressesOf(url:String) : List<InetAddress> {
        return InetAddress.getAllByName(url).toList()
    }

    private fun getRoundTripTime(node:InetAddress, timeout:Int) : Long {

        val start = Date().time

        node.isReachable(timeout)

        return Date().time - start

    }


    override fun setGW2Application(gw2: Application) {
        this.gw2 = gw2
    }

}