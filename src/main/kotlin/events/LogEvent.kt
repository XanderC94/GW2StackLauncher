package events

import java.util.logging.Level

object LoggingRequest {

    class OpenLogger : GenericRequest()

    class Log(val level: Level, val string: String) : GenericRequest()
}

object LoggingEvent {

}