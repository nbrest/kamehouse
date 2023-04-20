| [Home](/README.md) | [Docs](/docs/README.md) |
---------------------------------------------------------------

*********************

# Mobile:

This module handles the following functionality:

* Build a native mobile app for android and ios using apache cordova

* All the webapp kamehouse functionality is now supported on the native mobile app

* The cordova file plugin is used to persist the app settings

* The cordova advanced-http plugin is used to do all the api calls to the backend server avoiding cors of pure javascript requests

* A link to download the app can be found [here](https://kame.nicobrest.com/kame-house/downloads)

# Android build requirements on a windows host (2022-03-06):

- Install gradle
- Install node
- Install cordova

```sh
npm install -g cordova
```

- Install Android Studio

- Install SDKs:
  - Android Studio > Projects > More Actions > Sdk Manager
  - Sdk Platforms > Install Android 8.1 onwards
  - Sdk Tools >
  - Tick "Show Package Details"
  - Install Android SDK Build-Tools 30.0.3 onwards
  - Install Android SDK Command line tools latest (currently 6.0)
  - Install Android SDK Platform Tools (currently 33.0.0)
  - Install Android Emulator (currently 31.2.8)
  - Install Intel x86 Emulator Accelerator (HAXM Installer)

- Set environment variables: 
  - `ANDROID_SDK_ROOT = C:\Users\USERNAME\AppData\Local\Android\Sdk`
  - Add to `PATH`:
    - `%ANDROID_SDK_ROOT%\platform-tools`
    - `%ANDROID_SDK_ROOT%\cmdline-tools\latest\bin`

# Add required plugins (optional)
- this shouldn't be necessary as they should be part of the project already

```sh
cd kamehouse-mobile

cordova plugin remove cordova-plugin-advanced-http
cordova plugin remove cordova-plugin-inappbrowser
cordova plugin remove cordova-plugin-file

cordova plugin add cordova-plugin-advanced-http
cordova plugin add cordova-plugin-file

```

# Import project in Android Studio (optional)

- I prefer using vscode to edit the html, css, js files of kamehouse-mobile
- Select kamehouse-mobile as the root of the project
- Set the SDK in the project properties 

# Build Instructions

- Use the `build-kamehouse.sh -m mobile` script from the root of kamehouse parent project or from kamehouse-mobile dir to do all these following steps automatically

## To build manually:

- Run the script `kamehouse-mobile-resync-kh-files.sh` of kamehouse-shell to copy the reused files from kamehouse-ui to kamehouse-mobile (or manually copy the folders from kamehouse-ui webapps dir to `kamehouse-media/www/kame-house/`)

- Build the android native app

```sh
cordova build android
```

- The apk generated is in `kamehouse-mobile/platforms/android/app/build/outputs/apk/debug/app-debug.apk`

- Sometimes I need to refresh the build, for example if I delete some files in www/ and want them removed from the apk:

```sh
cordova clean
cordova build android
```

# Install Instructions

- Upload `app-debug.apk` to your android phone and install with the package installer
- It's not signed or verified by playstore so it will show warnings when trying to install it
- By default it's not allowed to install, so you need to enable installing unverified apps
- Easiest way I found to upload frequently during development is to download a free webdav server or sftp server on playstore and upload the apk with winscp. The script `kamehouse-mobile-upload-apk-to-device.sh` automates this step using SSH/SFTP Server - Terminal from googleplay (from Banana Studio) 

# Run in a local cordova browser

- Test in a local cordova browser

```sh
kamehouse-mobile-run-browser.sh
# or manually: kamehouse-mobile-resync-kh-files.sh ; cd kamehouse-mobile; cordova run browser
```

- Then a local browser windows should open in chrome
- Currently the default url is: [http://localhost:8000/index.html](http://localhost:8000/index.html)
- Open chrome dev tools and set the visible width to 391px. That's as similar view as I get to my phone
- Running like this will fail to do any backend api calls. I can test some cordova functionality though like storing/updating the settings file

# Development

- Setup local apache httpd to serve the mobile app frontend code on [http://localhost:9989/kame-house-mobile/settings.html](http://localhost:9989/kame-house-mobile/settings.html) reading the files from the intellij or eclipse dev folder `kamehouse-mobile\www\kame-house-mobile` using the httpd setup scripts mentioned in the [setup docs](/docs/dev-environment/dev-environment-setup-apache.md)
- This is the fastest way to test ui changes to the mobile app or test my javascript logic that runs differently for native mobile app, but it won't be able to connect to the backend server to test any interaction with the backend. For interactions with the backend, I need to upload the apk to my phone and test from my phone. There's no way to test that from my dev laptop directly
- Load the page with ?mockCordova=true&logLevel=trace parameter to mock cordova calls [http://localhost:9989/kame-house-mobile/settings.html?mockCordova=true&logLevel=trace](http://localhost:9989/kame-house-mobile/settings.html?mockCordova=true&logLevel=trace) 
- I can add the url parameter mockCordova=true to any page I load in the browser and test the mobile functionality with mocked cordova for any page, not just the mobile settings page. For example [http://localhost:9989/kame-house/vlc-player.html?mockCordova=true](http://localhost:9989/kame-house/vlc-player.html?mockCordova=true)
- For changes that interact with cordova api, it's better to test it directly on the cordova server [http://localhost:8000/index.html](http://localhost:8000/index.html) but on every ui code change I need to restart the server so it's slower for changes that don't require cordova