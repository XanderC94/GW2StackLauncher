@file:Suppress("RedundantSetter", "Unused")

package model.objects

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement

@JacksonXmlRootElement(localName = "GSA_SDK")
class GW2GFXSettings(
        @JacksonXmlProperty(localName = "APPLICATION")
        application: Application,
        @JacksonXmlProperty(localName = "GAMESETTINGS")
        gameSettings: GameSettings,
        @JacksonXmlProperty(localName = "Version", isAttribute = true)
        version: String) {

    val application: Application = application
        @JacksonXmlProperty(localName = "APPLICATION")
        get() = field

    val gameSettings: GameSettings = gameSettings
        @JacksonXmlProperty(localName = "GAMESETTINGS")
        get() = field

    val version: String = version
        @JacksonXmlProperty(localName = "Version", isAttribute = true)
        get() = field
}

@JacksonXmlRootElement(localName = "APPLICATION")
class Application(
        @JacksonXmlProperty(localName = "DISPLAYNAME")
        displayName: DisplayName,
        @JacksonXmlProperty(localName = "VERSIONNAME")
        versionName: VersionName,
        @JacksonXmlProperty(localName = "INSTALLPATH")
        installPath: InstallPath,
        @JacksonXmlProperty(localName = "CONFIGPATH")
        configPath: ConfigPath,
        @JacksonXmlProperty(localName = "EXECUTABLE")
        executable: Executable,
        @JacksonXmlProperty(localName = "EXECCMD")
        execCmd: ExecCmd) {

    val displayName: DisplayName = displayName
        @JacksonXmlProperty(localName = "DISPLAYNAME")
        get() = field
    val versionName: VersionName = versionName
        @JacksonXmlProperty(localName = "VERSIONNAME")
        get() = field

    val installPath: InstallPath = installPath
        @JacksonXmlProperty(localName = "INSTALLPATH")
        get() = field

    val configPath: ConfigPath = configPath
        @JacksonXmlProperty(localName = "CONFIGPATH")
        get() = field

    val executable: Executable = executable
        @JacksonXmlProperty(localName = "EXECUTABLE")
        get() = field

    val execCmd: ExecCmd = execCmd
        @JacksonXmlProperty(localName = "EXECCMD")
        get() = field

}

class DisplayName(
        @JacksonXmlProperty(localName = "Value", isAttribute = true)
        value: String) {

    val value: String = value
        @JacksonXmlProperty(localName = "Value", isAttribute = true)
        get() = field

    override fun toString(): String {
        return value
    }

}

class VersionName(
        @JacksonXmlProperty(localName = "Value", isAttribute = true)
        value: String) {

    val value: String = value
        @JacksonXmlProperty(localName = "Value", isAttribute = true)
        get() = field

    override fun toString(): String {
        return value
    }

}

class InstallPath(
        @JacksonXmlProperty(localName = "Value", isAttribute = true)
        value: String) {

    val value: String = value
        @JacksonXmlProperty(localName = "Value", isAttribute = true)
        get() = field

    override fun toString(): String {
        return value
    }

}

class ConfigPath(
        @JacksonXmlProperty(localName = "Value", isAttribute = true)
        value: String) {

    val value: String = value
        @JacksonXmlProperty(localName = "Value", isAttribute = true)
        get() = field

    override fun toString(): String {
        return value
    }

}

class Executable(
    @JacksonXmlProperty(localName = "Value", isAttribute = true)
    value: String) {

    val value: String = value
        @JacksonXmlProperty(localName = "Value", isAttribute = true)
        get() = field

    override fun toString(): String {
        return value
    }

}

class ExecCmd(
        @JacksonXmlProperty(localName = "Value", isAttribute = true)
        value: String) {

    val value: String = value
        @JacksonXmlProperty(localName = "Value", isAttribute = true)
        get() = field

    override fun toString(): String {
        return value
    }

}


@JacksonXmlRootElement(localName = "GAMESETTINGS")
class GameSettings(
        @JacksonXmlProperty(localName = "RESOLUTION")
        resolution: Resolution,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JacksonXmlProperty(localName = "OPTION")
        option: List<Option>?) {

    var resolution: Resolution = resolution
        @JacksonXmlProperty(localName = "RESOLUTION")
        get() = field
        set(value) {
            field = value
        }

    var option: List<Option>? = option
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JacksonXmlProperty(localName = "OPTION")
        @JacksonXmlElementWrapper(localName = "OPTION", useWrapping = false)
        get() = field
        set(value) {
            field = value
        }

}

@JacksonXmlRootElement(localName = "OPTION")
class Option(
        @JacksonXmlProperty(localName = "Name", isAttribute = true)
        name: String,
        @JacksonXmlProperty(localName = "Registered", isAttribute = true)
        registered: String,
        @JacksonXmlProperty(localName = "Type", isAttribute = true)
        type: String,
        @JacksonXmlProperty(localName = "Value", isAttribute = true)
        value: String,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JacksonXmlProperty(localName = "ENUM")
        possibleValues: List<Enum>?,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JacksonXmlProperty(localName = "RANGE")
        valuesRange: Range?) {

    var name: String = name
        @JacksonXmlProperty(localName = "Name", isAttribute = true)
        get() = field
        set(value) {
            field = value
        }

    var registered: String = registered
        @JacksonXmlProperty(localName = "Registered", isAttribute = true)
        get() = field
        set(value) {
            field = value
        }

    var type: String = type
        @JacksonXmlProperty(localName = "Type", isAttribute = true)
        get() = field
        set(value) {
            field = value
        }

    var value: String = value
        @JacksonXmlProperty(localName = "Value", isAttribute = true)
        get() = field
        set(value) {
            field = value
        }

    var possibleValues: List<Enum>? = possibleValues
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JacksonXmlProperty(localName = "ENUM")
        @JacksonXmlElementWrapper(localName = "ENUM", useWrapping = false)
        get() = field
        set(value) {
            field = value
        }

    var valuesRange: Range? = valuesRange
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JacksonXmlProperty(localName = "RANGE")
        get() = field
        set(value) {
            field = value
        }

}

class Enum(
        @JacksonXmlProperty(localName = "EnumValue", isAttribute = true)
        value: String) {

    val value: String = value
        @JacksonXmlProperty(localName = "EnumValue", isAttribute = true)
        get() = field

    override fun toString(): String {
        return value
    }

}

class Range(
        @JacksonXmlProperty(localName = "MinValue", isAttribute = true)
        min: String,
        @JacksonXmlProperty(localName = "MaxValue", isAttribute = true)
        max: String,
        @JacksonXmlProperty(localName = "NumSteps", isAttribute = true)
        nSteps: String) {

    val min: String = min
        @JacksonXmlProperty(localName = "MinValue", isAttribute = true)
        get() = field

    val max: String = max
        @JacksonXmlProperty(localName = "MaxValue", isAttribute = true)
        get() = field

    val nSteps: String = nSteps
        @JacksonXmlProperty(localName = "NumSteps", isAttribute = true)
        get() = field

}

class Resolution(
        @JacksonXmlProperty(localName = "Width", isAttribute = true)
        width: String,
        @JacksonXmlProperty(localName = "Height", isAttribute = true)
        height: String,
        @JacksonXmlProperty(localName = "RefreshRate", isAttribute = true)
        refreshRate: String){

    val width: String = width
        @JacksonXmlProperty(localName = "Width", isAttribute = true)
        get() = field

    val height: String = height
        @JacksonXmlProperty(localName = "Height", isAttribute = true)
        get() = field

    val refreshRate: String = refreshRate
        @JacksonXmlProperty(localName = "RefreshRate", isAttribute = true)
        get() = field

}
