package controller

import controller.networking.HTTP
import controller.strategies.ExtractionStrategy
import events.AddOnsRequest
import events.AppRequest
import extentions.asFile
import extentions.isDir
import extentions.isFile
import extentions.saveAsJson
import model.ontologies.AddOnMetadata
import model.ontologies.Overlay
import model.ontologies.gw2.AddOn
import model.ontologies.gw2.Application
import model.ontologies.gw2.LocalAddOn
import model.ontologies.gw2.LocalAddOnsWrapper
import model.utils.Nomenclatures
import model.utils.SystemUtils

class UpdateManager : ViewController(), GW2Dipper {

    private lateinit var gw2: Application
    private lateinit var gw2InstallDir : String
    private lateinit var gw2Bin64Dir : String
    private lateinit var gw2AddOnsDir : String

    private val gw2LocalAddOnsPath = "${SystemUtils.gw2slDir()!!}/${Nomenclatures.Files.GW2LocalAddonsJson}"

    init {

        subscribe<AddOnsRequest.DownloadAndUpdateAddOns> { req ->

            if (!gw2AddOnsDir.isDir()) {
                gw2AddOnsDir.asFile().mkdir()
            }

            val activeAddOns = req.addons.filter { it.isActive }

            val metadata = activeAddOns.map { it.name to it.asMetadata() }.toMap()

            log.info("Activated ${activeAddOns.size} AddOns.\n")

            val dlls = activeAddOns.filter { it.type.toLowerCase() == "dll" }

            removeInactiveAddOns(dlls, req.lastActive)

            val addOnsToUpdate = findAddOnsToUpdate(activeAddOns, req.lastActive, metadata)

            log.info("Found ${addOnsToUpdate.size} AddOns out-of-date.")

            val newAddOns = activeAddOns.filter { !req.lastActive.any { ti -> ti.name == it.name } }

            val toDownload = (addOnsToUpdate + newAddOns).filter { metadata[it.name]!!.url.isNotEmpty() }

            downloadAddOns(toDownload, metadata, dlls.size)

            val localWithSignatures = metadata.map { LocalAddOn(it.key, it.value.signature) }

            val localAddOns = LocalAddOnsWrapper(localWithSignatures)
            localAddOns.saveAsJson(gw2LocalAddOnsPath)

            fire(AddOnsRequest.UpdateActiveAddOns(localAddOns))

            // Gather overlays
            val overlays = findOverlays(activeAddOns)

            fire(AppRequest.RunGW2(overlays))
        }

    }

    override fun setGW2Application(gw2: Application) {
        this.gw2 = gw2
        this.gw2InstallDir = gw2.installPath.value.replace("\\", "/").dropLastWhile { it == '/' }
        this.gw2Bin64Dir = "$gw2InstallDir/bin64"
        this.gw2AddOnsDir = "$gw2InstallDir/addons"
        onReady()
    }

    private fun removeInactiveAddOns(dlls : List<AddOn>, lastActive: List<LocalAddOn>) {

        val oldDLLs = dlls.filter { lastActive.any { ti -> ti.name == it.name }  && !it.isActive }

        val getOldName : (AddOn) -> String = {
            if (oldDLLs.size > 1 && it.canBeChainloaded && it.type == "dll") it.chainloadName
            else if (it.type != "dll") it.exe.split(".").first()
            else it.exe
        }

        // Delete only .dll(s) since they are the cheaper in terms of space
        oldDLLs.filter {
                "$gw2Bin64Dir/${getOldName(it)}".isFile()
            }.forEach {
                val dll = "$gw2Bin64Dir/${getOldName(it)}"
                log.info("Deactivating ${it.name}...\n")
                dll.asFile().deleteRecursively()
            }
    }

    private fun findAddOnsToUpdate(activeAddOns : List<AddOn>,
                                   lastActive: List<LocalAddOn>,
                                   metadata: Map<String, AddOnMetadata>) : List<AddOn> {
        return activeAddOns
            .filter {
                lastActive.any { ti -> ti.name == it.name }
            }.filter {
                metadata[it.name]!!.signature.isNotEmpty()
            }.filter { addOn ->

                val localSignature = lastActive.find { it.name == addOn.name }!!.signature

                // Get All those whose remote and local signatures don't match
                // (except for those with empty signature which are excluded)
                !metadata[addOn.name]!!.signature.contains(localSignature, ignoreCase = true) ||
                        !localSignature.contains(metadata[addOn.name]!!.signature)
            }
    }

    private fun downloadAddOns(addOns: List<AddOn>, metadata: Map<String, AddOnMetadata>, activeDLLs: Int) {

        val getName : (AddOn) -> String = {
            if (activeDLLs > 1 && it.canBeChainloaded && it.type == "dll")
                it.chainloadName else it.exe
        }

        addOns.forEach { addOn ->

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
    }

    private fun findOverlays(addOns: List<AddOn>) : Map<String, Overlay> {
        return addOns.filter {
            it.type.toLowerCase() == "overlay" && it.exe.isNotEmpty()
        }.map {
            val dir = it.exe.replace(ExtractionStrategy.fromDelimiterAndBeyondRgx, "")
            it.name to Overlay("$gw2AddOnsDir/$dir", "$gw2AddOnsDir/$dir/${it.exe}")
        }.toMap()
    }
}