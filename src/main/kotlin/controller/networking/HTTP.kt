package controller.networking

import okhttp3.*
import java.io.IOException

object HTTP {

    init {

    }

    fun aGET(url: String,
             onComplete : (call: Call, response: Response) -> Unit,
             onFailure: (call: Call, e: IOException) -> Unit) {

        val client = OkHttpClient()

        val req = Request.Builder().url(url).build()

        client.newCall(req).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                onComplete(call, response)
            }

            override fun onFailure(call: Call, e: IOException) {
                onFailure(call, e)
            }

        })
    }

    fun GET(url: String) : Response {

        val client = OkHttpClient()

        val req = Request.Builder().url(url).build()

        return client.newCall(req).execute()
    }

}