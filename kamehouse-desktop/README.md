| [Home](/README.md) | [Docs](/docs/README.md) |
---------------------------------------------------------------

*********************

# Desktop Module:

This module contains kamehouse-desktop app built mostly with python. The idea of the desktop app is to run as the main screen in ztv devices to display a nice UI when there's no video being played with a slideshow of background images and some other UI widgets

- `kamehouse-desktop` is the app that runs as the main UI in the ztv devices. The app contains is built with UI widgets in python using pyqt5 to render all the functionality.

- When using kamehouse-desktop, set the taskbar to autohide so that the desktop app takes the whole screen

- The background slideshow widget can be used to display a slideshow of photos with transition effects

- The ztv player widget is a view only ztv player ui that shows the current file being played on the current server. It connects to kamehouse-vlcrc webapp to get the status of vlc via websockets and http requests as fallback

- The weather widget gets the current weather from openweathermap api

## Install

- Install with `deploy-kamehouse.sh -m desktop`
- This module also depends on kamehouse-shell and kamehouse-snape being installed

- Schedule `keep-alive-kamehouse-desktop.sh` script with cron to keep the kamehouse desktop running

- Set `DEPLOYMENT_RESTART_DESKTOP=true` in `kamehouse.cfg` to restart desktop app automatically on deployment

## Configure

- Almost everything in the UI can be configured using the `kamehouse-desktop.cfg` configuration file. The source image for all logos and icons, the exact pixel position of each UI elemnt, the text styles and effects. 

- The default values place the UI elements properly for `1920x1080` screen resolutions but every value can be overriden for different screen sizes in the configuration file

- Widgets can also be disabled individually in the configuration file. For example you can run the desktop app without the background slideshow widget or without the ztv player widget

- Sync times for http requests can also be adjusted via configuration

- Log level can also be adjusted via configuration and trace logging can be enabled individually for each widget to troubleshoot issues

- During the installation, the `kamehouse-desktop.cfg` config file is deployed to `${HOME}/.kamehouse/config/kamehouse-desktop.cfg` and it's values override the default settings

- The default settings can be found in the `default-kamehouse-desktop.cfg` file

- The configuration file is loaded at startup so you need to restart the desktop app to pickup any changes to the configuration file

## Run

- Start the desktop app with `kamehouse-desktop-startup.sh` or `kamehouse-desktop-restart.sh` installed with `kamehouse-shell`

- Use groot server manager to start and stop the desktop app from the UI and to toggle keep alive scripts to fully turn off the desktop app

- In my tests in rasperry pis 4 and 5 kamehouse-desktop runs with 200-300mb of ram and about 20-30% cpu usage. The background slideshow widget uses 20-25% of cpu, so disabling it reduces significantly the resources used
