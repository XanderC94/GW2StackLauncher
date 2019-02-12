package model.utils

import java.util.*

object SystemUtils {

    const val appName = "GW2SL"
    private val winAppData = System.getenv("APPDATA")
    private val userHome = System.getProperty("user.home")
    private val gw2WindowsUserDirectory = "${System.getenv("APPDATA")}/Guild Wars 2"
    private val osxAppData = "~/Library/Application Support/"
    private val gw2OSX64UserDirectory = "~/Library/Application Support/Guild Wars 2"
    private val gw2OSX32UserDirectory = "~/Library/Application Support/Guild Wars 2/p_drive/User/Application Data/Guild Wars 2"

    fun OSName() : String {
        return System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH)
    }

    fun isWindows() : Boolean {
        val osName = this.OSName()
        return osName.contains("windows", ignoreCase = true)
    }

    fun isOSX64() : Boolean {
        val osName = this.OSName()

        return (osName.contains("mac", ignoreCase = true)
                    || osName.contains("darwin", ignoreCase = true))
                && osName.contains("64")
    }

    fun isOSX32() : Boolean {
        val osName = this.OSName()

        return (osName.contains("mac", ignoreCase = true)
                || osName.contains("darwin", ignoreCase = true))
                && !osName.contains("64")
    }

    fun GW2UserDirectory() : String? {

        return when {
            isWindows() -> gw2WindowsUserDirectory.replace('\\', '/')
            isOSX64() -> gw2OSX64UserDirectory
            isOSX32() -> gw2OSX32UserDirectory
            else -> null
        }

    }

    fun dataDir() : String? {
        return when {
            isWindows() -> winAppData.replace('\\', '/')
            isOSX64() || isOSX32() -> osxAppData
            else -> null
        }
    }

    fun gw2slDir() : String? {
        val dataDir = dataDir()
        return when {
            dataDir != null -> "$dataDir/${SystemUtils.appName}"
            else -> null
        }
    }

    fun appWorkspace() : String {
        return System.getProperty("user.dir").replace('\\', '/')
    }
}