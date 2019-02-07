package model.utils

object Nomenclatures {

    object URL {
        const val repo = "https://github.com/XanderC94/GW2StackLauncher"
        const val gw2site = "https://www.guildwars2.com/en/"
    }

    object HostName {

        const val gw2OAuthServ1Hostname = "auth1.101.ArenaNetworks.com"
        const val gw2OAuthServ2Hostname = "auth2.101.ArenaNetworks.com"
        const val gw2AssetServHostname = "assetcdn.101.ArenaNetworks.com"
    }

    object Directory {
        const val icon : String = "/icon/"
        const val style : String = "/style/"
        const val view : String = "/view/"
    }

    object File {

        const val GW2LocalSettingsJson = "Settings.json"
        const val GW2ArgumentsJson = "Arguments.json"
        const val GW2GFXSettings64XML = "GFXSettings.Gw2-64.exe.xml"
        const val GW2AddonsJson = "AddOns.json"
        const val GW2LocalAddonsJson = "AddOns.local.json"
        const val GW2SLConfigJson = "Config.json"
        const val GW2SLStyle = "GW2StackLauncherStyle.css"
        const val GW2SLScene = "GW2StackLauncherView.fxml"

        const val GW2SLIcon = "GW2SL.png"
        const val GitHubLogo = "GitHubLogo.png"
        const val GW2Logo = "GW2Logo.jpg"
        const val GW2PoFLogo = "GW2PoFLogo.png"
    }

    object Component {

        const val gw2slTabPane = "GW2SLTabPane"
        const val overviewTab = "OverviewTab"
        const val optionTab = "ArgsTab"
        const val addOnsTab = "AddOnsTab"

        const val overviewSplitPane = "OverviewSplitPane"
        const val optionsSplitPane = "ArgsSplitPane"
        const val addOnsSplitPane = "AddOnsSplitPane"

        const val availableArgsList = "ArgsListView"
        const val availableAddOnsList = "AddOnsListView"
        const val activeArgsList = "ActiveArgsList"
        const val activeAddOnsList = "ActiveAddOnsList"

        const val argDescriptionArea = "ArgDescriptionArea"
        const val argsPaneHeader = "ArgPaneHeader"
        const val optionValuePane = "ArgValuePane"
        const val argsValueField = "ArgValueField"
        const val argsValueChoiceBox = "ArgValueChoiceBox"

        const val valuesRefreshButton = "ArgValuesRefreshButton"

        const val runGW2Button = "RunGW2Button"
        const val gw2slRunPane = "GW2SLRunPane"
        const val gw2LocationField = "GW2LocationField"

        const val iconsFlowPane = "IconsFlowPane"

        const val addOnsWebView = "AddOnsWebView"
        const val webViewAnchor = "WebViewAnchor"
    }

    object Argument {
        const val assetserv = "-assetsrv"
        const val authserv = "-authsrv"
        const val dat = "-dat"
    }

}