| [Home](/README.md) | [Docs](/docs/README.md) |
---------------------------------------------------------------

*********************

# Mobile:

This module handles the following functionality:

* Build a native mobile app for android (and ios eventually) using apache cordova

* Kamehouse ui and groot frontend code are built into the mobile app and supported natively connecting to the backend server configurable in the settings page of the native mobile app 

* The cordova file plugin is used to persist the app settings

* The cordova advanced-http plugin is used to do all the api calls to the backend server avoiding cors of pure javascript requests

* A link to download the app can be found [here](https://kame.nicobrest.com/kame-house/downloads)

- The android build requires Java JDK 11

# Android build requirements on a windows host:

- Install gradle (currently v5.6.2)
- Install node (currently v16)
- Install cordova

```sh
npm install -g cordova
```

- Install Android Studio

- Install SDKs:
  - Android Studio > Projects > More Actions > Sdk Manager
  - Sdk Platforms > Install Android 8.1 onwards (until v33 currently)
  - Sdk Tools >
  - Tick "Show Package Details"
  - Install Android SDK Build-Tools 30.0.3 onwards (until 33.0.2)
  - Install Android SDK Command line tools latest (currently 9.0)
  - Install Android SDK Platform Tools (currently 33.0.0)

- Set environment variables: 
  - `ANDROID_SDK_ROOT = C:\Users\USERNAME\AppData\Local\Android\Sdk`
  - Add to `PATH`:
    - `%ANDROID_SDK_ROOT%\platform-tools`
    - `%ANDROID_SDK_ROOT%\cmdline-tools\latest\bin`

# Android build requirements on a linux host (Ubuntu):

- Install gradle (currently v4.4.1)
```sh
sudo apt-get install gradle
```

- Install node (currently v16)
```sh
cd ~
curl -sL https://deb.nodesource.com/setup_16.x | sudo bash -
sudo apt-get -y install nodejs
```

- Install cordova
```sh
sudo npm install -g cordova
```

- Install Android Studio
  - Download tar.gz install file from android studio website
  - Extract to ${HOME}/programs/android-studio
  - Follow install steps on android studio website for linux to install required libraries

- Install SDKs:
  - Android Studio > Projects > More Actions > Sdk Manager
  - Sdk Platforms > Install Android 8.1 onwards (until v33 currently)
  - Sdk Tools >
  - Tick "Show Package Details"
  - Install Android SDK Build-Tools 30.0.3 onwards (until 33.0.2)
  - Install Android SDK Command line tools latest (currently 9.0)
  - Install Android SDK Platform Tools (currently 33.0.0)

- Check that android sdk is installed to: `${HOME}/Android/Sdk`. That's where the build script expects it. It's the default behavior in the current android studio release (v2022.1.2.20)

# Build Instructions:

- One time actions. On kamehouse-mobile root folder:
```sh
# Add android platform
cd kamehouse-mobile
cordova platform add android

# Setup encryption key
echo "" >> ${HOME}/.kamehouse/.shell/.cred
echo "KAMEHOUSE_MOBILE_ENCRYPTION_KEY=yourkey" >> ${HOME}/.kamehouse/.shell/.cred
echo "" >> ${HOME}/.kamehouse/.shell/.cred
```

- Then use the `build-kamehouse.sh -m mobile` or `build-kamehouse.sh -m mobile -u` script from the root of kamehouse parent project or from kamehouse-mobile dir to build the apk for android (ios build not supported yet). Or use `deploy-kamehouse.sh -m mobile` to build and deploy it to kame.com

# Install Instructions:

- Upload `app-debug.apk` to your android phone and install with the package installer
- It's not signed or verified by playstore so it will show warnings when trying to install it
- By default it's not allowed to install, so you need to enable installing unverified apps

# Install issues:

## APK signed with different debug.keystore

- When building the app on different devices, each device signs it with it's android studio debug key. If I install the app built and signed on one device, then when I build it on another and try to install an update, it will fail. I need to uninstall the existing app and then re install it. 
- To avoid this, use the same debug keystore on all devices used to build the apk. 
- Copy and use the same `${HOME}/.android/debug.keystore` to all devices where I build the mobile app.

# Run in a local cordova browser:

- Test in a local cordova browser
- From the project root or kamehouse-mobile run:

```sh
kamehouse-mobile-run-browser.sh
```

- Then a local browser windows should open in chrome
- Currently the default url is: [http://localhost:8000/kame-house/index.html](http://localhost:8000/kame-house/index.html)
- Open chrome dev tools and set the visible width to 391px. That's as similar view as I get to my phone
- Running like this will fail to do any backend api calls. I can test some cordova functionality though like storing/updating the settings file

# Development:

## Test in computer using browser
- Setup local apache httpd to serve the mobile app frontend code on [http://localhost:9989/kame-house-mobile/settings.html](http://localhost:9989/kame-house-mobile/settings.html) reading the files from the intellij or eclipse dev folder `kamehouse-mobile\www\kame-house-mobile` using the httpd setup scripts mentioned in the [setup docs](/docs/dev-environment/dev-environment-setup-apache.md)
- This is the fastest way to test ui changes to the mobile app or test my javascript logic that runs differently for native mobile app, but it won't be able to connect to the backend server to test any interaction with the backend. For interactions with the backend, I need to upload the apk to my phone and test from my phone. There's no way to test that from my dev laptop directly
- Load the page with ?mockCordova=true&logLevel=trace parameter to mock cordova calls [http://localhost:9989/kame-house-mobile/settings.html?mockCordova=true&logLevel=trace](http://localhost:9989/kame-house-mobile/settings.html?mockCordova=true&logLevel=trace) 
- I can add the url parameter mockCordova=true to any page I load in the browser and test the mobile functionality with mocked cordova for any page, not just the mobile settings page. For example [http://localhost:9989/kame-house/vlc-player.html?mockCordova=true](http://localhost:9989/kame-house/vlc-player.html?mockCordova=true)
- For changes that interact with cordova api, it's better to test it directly on the cordova server [http://localhost:8000/kame-house/index.html](http://localhost:8000/kame-house/index.html) but on every ui code change I need to restart the server so it's slower for changes that don't require cordova

## Upload APK to device
- Start the SSH/SFTP Server - Terminal from googleplay on the phone
- Execute `kamehouse-mobile-upload-apk-to-device.sh` script to build the mobile app and upload it to the `Downloads` folder in the device
- Update the app on the mobile device

## Install from google drive
- The kamehouse build script uploads the APK to google drive. Install it on the mobile devices straight from google drive
