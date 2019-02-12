# GW2StackLauncher

Straight-Forward and Minimal Launcher for [Guild Wars 2](https://www.guildwars2.com/en/) by ArenaNet with its addons and command-line parameters.

## What it does

* Automatically enable, update and disable any AddOns available for GW2
* Choose and set-up command-line parameters

## Usage

The UI is composed of 3 main tabs:

* **Overview**: There you can overlook the actual status of your configuration 
regarding chosen Command Line Arguments and AddOns/Plugins/Overlays.

* **Arguments**: This tab is mainly focused on showing descriptions of every single *available* 
command-line argument. Each argument may be activated or deactivated through a toggle button 
on the right side of the Argument, inside the list visible on the left side of the gui.

* **AddOns**: This tab contains a list of the most used AddOns for Guild Wars 2. 
Selecting one of them will display on the integrated [JxBrowser](https://www.teamdev.com/jxbrowser) 
the application web-page/github repository. Each plugin may be toggled similarly to the arguments.
**NOTE**: Each plugin will be installed, organized and renamed accordingly for chain-loading when 
clicking on the "Run Guild Wars 2" button. Moreover, as a key feature of this application, 
add-ons will be updated each new launch (**if possible**).

* **About**: Guess what it does? It display this very ReadMe. 

Other than these tabs there is a bottom component where the GW2 Install Location is displayed 
and the button "Run Guild Wars 2" can be found.

#### Key-Bindings

| Command                   | Key-Bind  |   Component |
|:---                       |:---       |:---:        |
|**BACKWARD**               | ALT + ←   | BROWSER     |
|**RELOAD**                 | F5        | BROWSER     |
|**FORWARD**                | ALT + →   | BROWSER     |
|**FINDER**                 | CTRL + F  | BROWSER     |
|**CLEAR FINDINGS**         | ESC       | BROWSER     |
|**TOGGLE FULL-SCREEN**     | F11       | APP         |

## FAQ

* Q: Will you add multi-boxing support? 
    * A: No, use [Gw2LaunchBuddy](https://github.com/TheCheatsrichter/Gw2_Launchbuddy) instead

* Q: Is this tool safe to use from the point-of-view of 
[GW2 Content Terms of Use](https://www.guildwars2.com/en/legal/guild-wars-2-content-terms-of-use/) ?
    * A: It should be. The only things the tools interact with are:
        * [external resources](https://github.com/XanderC94/GW2SLResources)
        * The Setting.json to store GW2 cmd-line arguments
        * The GFX file where GW2 Installation and Graphics Settings are located
        * An AddOns.local.json where info of the locally installed plugins are stored
        
        It doesn't touch whatever .dat file is present.

* Q: Why this tool does HTTP calls?
    * A: 'cause updated Arguments and AddOns are hosted [here](https://github.com/XanderC94/GW2SLResources)
    so that they can be easily updated for everyone.

* Q: Why a JVM-based language?
    * A: I've chosen Kotlin (sorry Scala, next time) for 2 main reasons: 
        * (A) I haven't used it in a long time and it is essentially Java on steroids
        * (B) JVM-based languages are multi platform without much headache as using C++.
        This way even player that play GW2 on Mac may take advantage of this. 
        Maybe on Linux users as well but it needs some check-up before being completely sure, 
        so be my guest to test it.

For everything else check the source code available.

## To-Do

[Look Here](https://trello.com/b/wTMttZAN/gw2stacklauncher)

# DISCLAIMER

## External Resources

### ArenaNet

This project use some artworks by ArenaNet. All the ownership of such content goes to ArenaNet. 
With the project is provided the Copyright for GW2 material by ArenaNet as stated in their policies.
[GW2 Content Terms of Use](https://www.guildwars2.com/en/legal/guild-wars-2-content-terms-of-use/)

Some AddOns are "officially" permitted by ArenaNet others aren't (for now). So use them at your own risk. 

### TeamDev

GW2StackLauncher uses JxBrowser http://www.teamdev.com/jxbrowser, which is a proprietary software, 
owned by TeamDev Ltd. The use of JxBrowser is governed by JxBrowser Product License Agreement 
http://www.teamdev.com/jxbrowser-licence-agreement. You may not use JxBrowser separately from 
GW2StackLauncher without a separate license from TeamDev Ltd. Use of JxBrowser as part of GW2StackLauncher
in any commercial software requires a commercial license from TeamDev Ltd.