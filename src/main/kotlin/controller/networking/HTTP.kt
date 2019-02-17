package controller.networking

import okhttp3.*
import java.io.IOException

object HTTP {

    init {

    }

    fun aGET(url: String,
             onComplete : (call: Call, response: Response) -> Unit,
             onFailure: (call: Call, e: IOException) -> Unit,
             vararg header: Pair<String, String>) {

        val client = OkHttpClient()
        val builder = Request.Builder().url(url)

        if (header.isNotEmpty()) {
            builder.headers(Headers.of(header.toMap()))
        }

        val req = builder.build()

        client.newCall(req).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                onComplete(call, response)
            }

            override fun onFailure(call: Call, e: IOException) {
                onFailure(call, e)
            }

        })
    }

    fun GET(url: String, vararg header: Pair<String, String>) : Response {

        val client = OkHttpClient()
        val builder = Request.Builder().url(url)

        if (header.isNotEmpty()) {
            builder.headers(Headers.of(header.toMap()))
        }

        val req = builder.build()

        return client.newCall(req).execute()
    }

}