package extentions

import java.net.InetAddress
import java.util.*

/**
 * Get RoundTripTime to the specified INetAddress with the given timeout
 *
 */
fun InetAddress.RTT(timeout:Int) : Long {

    val start = Date().time

    this.isReachable(timeout)

    return Date().time - start

}