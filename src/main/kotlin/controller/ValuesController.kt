package controller

import controller.networking.Service
import events.GenericRequest
import events.ValuesEvent
import events.ValuesRequest
import model.objects.Application
import model.utils.Nomenclatures.Argument
import model.utils.Nomenclatures.HostName
import model.utils.RTT
import java.io.File

class ValuesController : ViewController(), GW2Dipper {

    private lateinit var gw2: Application

    private val assetsServ = Service(Argument.assetserv,
            listOf(HostName.gw2AssetServHostname)
    )

    private val authServ = Service(Argument.authserv,
            listOf(HostName.gw2OAuthServ1Hostname, HostName.gw2OAuthServ2Hostname)
    )

    init {
        subscribe<ValuesRequest.GetArgValues> { request ->
            when(request.id) {
                Argument.assetserv -> {

                    resolveAndFire(request, assetsServ, request.id)
                }
                Argument.authserv -> {

                    resolveAndFire(request, authServ, request.id)
                }
                Argument.dat -> {

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
        initViewElements()
    }

}