package controller

import controller.networking.Service
import events.GenericRequest
import events.ValuesEvent
import events.ValuesRequest
import extentions.RTT
import model.ontologies.gw2.Application
import model.utils.Nomenclatures.Arguments
import model.utils.Nomenclatures.HostNames
import java.io.File

class ValuesController : ViewController(), GW2Dipper {

    private lateinit var gw2: Application

    private val assetsServ = Service(Arguments.assetserv,
            listOf(HostNames.gw2AssetServHostname)
    )

    private val authServ = Service(Arguments.authserv,
            listOf(HostNames.gw2OAuthServ1Hostname, HostNames.gw2OAuthServ2Hostname)
    )

    init {
        subscribe<ValuesRequest.GetArgValues> { request ->
            when(request.id) {
                Arguments.assetserv -> {

                    resolveAndFire(request, assetsServ, request.id)
                }
                Arguments.authserv -> {

                    resolveAndFire(request, authServ, request.id)
                }
                Arguments.dat -> {

                    val gw2Dir = File(gw2.installPath.value)
                    if (gw2Dir.isDirectory) {
                        val list = gw2Dir.list { _, item -> item.endsWith(".dat") }.map {

                            "${gw2Dir.absolutePath.replace("\\", "/")}/$it"
                        }.map { it to File(it).length() / 1000000000f }

                        fire(ValuesEvent.ArgValues(request, request.id, list))
                    }
                }
                else -> { }
            }
        }

    }

    private fun resolveAndFire(request: GenericRequest, serv: Service, id: String) {
        runAsync {

            val addresses = serv.resolve()
            addresses.parallelStream().forEach {
                val p = it.hostAddress to it.RTT(500)
                fire(ValuesEvent.ArgValue(request, id, p))
            }
        }
    }

    override fun setGW2Application(gw2: Application) {
        this.gw2 = gw2
        onReady()
    }

}