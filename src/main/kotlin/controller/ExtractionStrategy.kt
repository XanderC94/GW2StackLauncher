package controller

import controller.networking.HTTP
import model.utils.asFile
import model.utils.toHex
import org.zeroturnaround.zip.ZipUtil
import java.io.InputStream
import java.security.MessageDigest

enum class ExtractionStrategy(
        val addOn: String,
        val alt: String,
        val strategy : (InputStream, String, String) -> Unit) : (String, String, String) -> String {

    GW2Radial("GW2 Radial", "GW2Radial", { zip, dst, name ->
        val bool = ZipUtil.unpack(zip, dst.asFile()) {
            when (it) {
                "d3d9.dll" -> "bin64/$name"
                else -> null
            }
        }

    }),
    D912PXY("d912pxy", "d912pxy", { zip, dst, name ->
        val fname = name.split(".").first()
        val rgx = Regex("d912pxy\\/dll\\/release\\/.*\\..*")
        ZipUtil.unpack(zip, dst.asFile()) {
            when {
                it.contains("shader") -> it
                it.contains("pck") -> it
                it.matches(rgx) ->
                    "bin64/$fname.${it.split(".").last()}"
                else -> null
            }
        }
    }),
    GW2Hook("GW2 Hook", "GW2Hook", { zip, dst, name ->
        ZipUtil.unpack(zip, dst.asFile()) {
            when {
                it.startsWith("addons") -> it
                it.startsWith("bin64") -> it.replace(Regex("(?<=\\/)\\w+(?=\\.)"), name)
                else -> null
            }
        }
    }),
    GW2AugTyr("Augmented Tyria", "AugTyr", { zip, dst, name ->

        unpackZipWithRootDir(zip, GW2AugTyr.alt, dst, name)
    }),
    GW2Navi("GW2Navi", "GW2Timer", { zip, dst, name ->

        unpackZipWithRootDir(zip, GW2Navi.name, dst, name)
    }),
    Default("", "", { zip, dst, name ->
        val fname = name.split(".").first()
        ZipUtil.unpack(zip, dst.asFile()) { "addons/$fname/$it" }
    });

    override fun invoke(url: String, dest: String, name: String) : String {

        return if (url.contains("zip")) {
            // Unzip
            val zip = HTTP.GET(url).body()!!.byteStream()
            val digest = MessageDigest.getInstance("MD5")
            // Invoke Distribution Strategy
            this.strategy(zip, dest, name)

            digest.digest(zip.readBytes()).toHex()
        } else {
            ""
        }
    }

    companion object : (String) -> ((String, String, String) -> Unit) {

        val fromDelimiterAndBeyondRgx = Regex("[-_].+|\\.\\w+")

        override fun invoke(id: String): (String, String, String) -> Unit {

            val trimId = trimmer(id)

            val exStr = ExtractionStrategy.values()
                    .firstOrNull {
                        trimmer(it.addOn) == trimId || trimmer(it.alt) == trimId || trimmer(it.name) == trimId
                    }

            return { url, dst, name ->
                if (exStr != null) exStr(url, dst, name) else Default(url, dst, name)
            }
        }

        private fun trimmer(str: String) : String{
            return str.replace(" ", "").toLowerCase().replace("gw2", "")
        }

        fun unpackZipWithRootDir(zip: InputStream, root: String, dst: String, name: String) {
            val fname = name.replace(fromDelimiterAndBeyondRgx, "")
            val rootDeleterRgx = Regex("$root[-_][a-zA-Z0-9-_. ]+(?=\\/|\\\\)")
            ZipUtil.unpack(zip, dst.asFile()) {
                when {
                    it.startsWith(root) -> {
                        "addons/$fname/${it.replace(rootDeleterRgx, "")}"
                    }
                    else -> null
                }
            }
        }
    }
}