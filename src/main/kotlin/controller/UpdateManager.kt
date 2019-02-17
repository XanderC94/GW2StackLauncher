package controller

import controller.networking.HTTP
import events.AddOnsRequest
import events.AppRequest
import model.objects.*
import model.utils.*
import tornadofx.*
import java.nio.charset.Charset
import java.util.regex.Pattern

class UpdateManager : Controller(), GW2Dipper {

    private lateinit var gw2: Application

    val versionInPagePattern = Pattern.compile("Release:? ?([\\d+.?]+)")
    val versionInURLPattern = Pattern.compile("(?<=_)(?:\\w+)+")

    data class Metadata(val signature: String, val url: String)
    data class Executable(val dir: String, val exe: String)

    private fun Pair<String, String>.toMetadata() : Metadata {
        return Metadata(this.first, this.second)
    }

    init {

        subscribe<AddOnsRequest.DownloadAndUpdateAddOns> { req ->

            val gw2InstallDir = gw2.installPath.value.replace("\\", "/").dropLastWhile { it == '/' }

            val gw2Bin64Dir = "$gw2InstallDir/bin64"
            val gw2AddOnsDir = "$gw2InstallDir/addons"

            log.info(gw2InstallDir)
            log.info(gw2Bin64Dir)
            log.info(gw2AddOnsDir)

            if (!gw2AddOnsDir.isDir()) {
                gw2AddOnsDir.asFile().mkdir()
            }

            val activeAddOns = req.addons.filter { it.isActive }

            val metadata = activeAddOns.map {
                it.name to getMetadata(it)
            }.toMap()

            log.info("Activated ${activeAddOns.size} AddOns.\n")

            val dlls = activeAddOns.filter { it.type.toLowerCase() == "dll" }

            val oldDLLs = dlls.filter { req.lastActive.any { ti -> ti.name == it.name } }

            val getName : (GW2AddOn) -> String = {
                if (dlls.size > 1 && it.canBeChainloaded && it.type == "dll")
                    it.chainloadName else it.exe
            }

            val getOldName : (GW2AddOn) -> String = {
                if (oldDLLs.size > 1 && it.canBeChainloaded && it.type == "dll") it.chainloadName
                else if (it.type != "dll") it.exe.split(".").first()
                else it.exe
            }

            // Delete only .dll(s) since they are the cheaper in terms of space
            req.addons
                .filter { req.lastActive.any { ti -> ti.name == it.name } && !it.isActive }
                .filter {
                    "$gw2Bin64Dir/${getOldName(it)}".isFile() && it.type == "dll"
                }.forEach {
                    val dll = "$gw2Bin64Dir/${getOldName(it)}"

                    log.info("Deactivating ${it.name}...\n")
                    dll.asFile().deleteRecursively()
                }

            val addOnsToUpdate = activeAddOns
                .filter {
                    req.lastActive.any { ti -> ti.name == it.name }
                }.filter {
                    metadata[it.name]!!.signature.isNotEmpty()
                }.filter { addOn ->

                    val localSignature = req.lastActive.find { it.name == addOn.name }!!.signature

                    // Get All those whose remote and local signatures don't match
                    // (except for those with empty signature which are excluded)
                    !metadata[addOn.name]!!.signature.contains(localSignature, ignoreCase = true) ||
                            !localSignature.contains(metadata[addOn.name]!!.signature)
                }

            log.info("Found ${addOnsToUpdate.size} out-of-date.")

            val newAddOns = activeAddOns.filter { !req.lastActive.any { ti -> ti.name == it.name } }

            val toDownload = (addOnsToUpdate + newAddOns).filter { metadata[it.name]!!.url.isNotEmpty() }

            log.info(metadata.toString())
            log.info(toDownload.toString())

            toDownload.forEach { addOn ->

                val dlUrl = metadata[addOn.name]!!.url
                val name = getName(addOn)

                log.info("Downloading ${addOn.name} from $dlUrl as $name.")

                when {
                    addOn.type == "Launcher" -> {
                        val f = "${SystemUtils.userDownloadDir}/${dlUrl.split("/").last()}".asFile()
                        f.writeBytes(HTTP.GET(dlUrl).body()!!.byteStream().readBytes())
                    }
                    dlUrl.contains("zip") -> {
                        ExtractionStrategy(addOn.name)(dlUrl, gw2InstallDir, name)
                    }
                    else -> {
                        val f = "$gw2Bin64Dir/$name".asFile()
                        f.writeBytes(HTTP.GET(dlUrl).body()!!.byteStream().readBytes())
                    }
                }
            }

            val localWithSignatures = metadata.map {
                GW2LocalAddOn(it.key, it.value.signature)
            }

            val gw2LocalAddOnsPath = "${SystemUtils.gw2slDir()!!}/${Nomenclatures.File.GW2LocalAddonsJson}"

            GW2LocalAddOns(localWithSignatures).saveAsJson(gw2LocalAddOnsPath)

            val overlays = activeAddOns.filter {
                it.type.toLowerCase() == "overlay" && it.exe.isNotEmpty()
            }.map {
                val dir = it.exe.replace(ExtractionStrategy.fromDelimiterAndBeyondRgx, "")
                it.name to Executable("$gw2AddOnsDir/$dir", "$gw2AddOnsDir/$dir/${it.exe}")
            }.toMap()

            createGW2SLBatch(overlays, "Guild Wars 2" to Executable(gw2.installPath.value, gw2.execCmd.value))

            fire(AppRequest.RunGW2())
        }

    }

    private fun getGitHubAPIData(url: String) : GitHubAPISimple {
        return HTTP.GET(url).body()!!.string().fromJson()
    }

    private fun getMD5Signature(url: String) : String {
        return HTTP.GET(url).body()!!.string().split(" ").first()
    }

    private fun parseAddOnVersion(url: String) : String {
        return {
            val html = HTTP.GET(url).body()!!.string()
            val matcher = versionInPagePattern.matcher(html)
            if (matcher.find()) matcher.group(1).replace(".", "_") else ""
        }()
    }

    private fun getMetadata(addOn: GW2AddOn) : Metadata {
        return when {
            addOn.signature.contains("md5", true) -> {
                (getMD5Signature(addOn.signature) to addOn.url).toMetadata()
            }
            addOn.url.contains("api.github.com", true) -> {
                val api = getGitHubAPIData(addOn.url)
                (api.publishDate to api.url).toMetadata()
            }
            else -> {
                val version = parseAddOnVersion(addOn.info)
                val url = addOn.url.replaceIf {
                    version.isNotEmpty()
                } (versionInURLPattern.toRegex(), version)

                (version to url).toMetadata()
            }
        }
    }

    private fun createStartString(name: String, dir: String, exe: String) : String {
        return "START \"$name\" /D $dir \"$exe\"\ntimeout /t 5\n\n"
    }

    private fun createGW2SLBatch(overlays: Map<String, Executable>, game: Pair<String, Executable>) {

        var bat = this.getResourceAsText("/run.gw2.template.bat").reader().readText() + "\n"

        bat += createStartString(game.first, game.second.dir, game.second.exe)

        overlays.forEach {
            bat += createStartString(it.key, it.value.dir, it.value.exe)
        }

        "${gw2.installPath.value}/gw2sl.bat".asFile().writeText(bat, Charset.defaultCharset())
    }

    override fun setGW2Application(gw2: Application) {
        this.gw2 = gw2
    }
}