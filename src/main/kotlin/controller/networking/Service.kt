package controller.networking

import java.net.InetAddress

open class Service(val name: String, val aliases: List<String>) {

    fun resolve() : List<InetAddress> {
        return aliases.map { InetAddress.getAllByName(it).toList() }.flatten()
    }
}