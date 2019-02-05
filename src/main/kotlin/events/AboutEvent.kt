package events

import java.net.URL

object AboutRequest {

    class OpenLink(val url: URL) : GenericRequest()

}