# Mobile:

This module handles the following functionality:

* Build a native mobile app for android and ios

* The app uses the inAppBrowser plugin to load kamehouse-ui and kamehouse-groot from the server and render it's mobile view 

# Android build requirements on a windows host (2022-03-06):

- Install node
- Install cordova

`npm install -g cordova`

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

# Add required plugins 
- this shouldn't be necessary as they should be part of the project already

```sh
cd kamehouse-media

# Add inappbrowser plugin
cordova plugin add cordova-plugin-inappbrowser
```

# Import project in Android Studio (optional)

- Select kamehouse-mobile as the root of the project
- Set the SDK in the project properties 

# Build Instructions

- Build the android native app

`cordova build android`

- The apk generated is in `kamehouse-mobile/platforms/android/app/build/outputs/apk/debug/app-debug.apk`

# Install Instructions

- Upload `app-debug.apk` to your android phone and install with the package installer
- It's not signed or verified by playstore so it will show warnings when trying to install it
- By default it's not allowed to install, so you need to enable installing unverified apps
- Easiest way I found to upload frequently during development is to download a free webdav server on playstore and upload the apk with winscp

# Run in an emulator

## Run in a local browser

- Test in a local browser

`cordova run browser`

- Then a local browser windows should open in chrome
- Currently the default url is: [http://localhost:8000/index.html](http://localhost:8000/index.html)

## Run in an emulated android device

- In Android Studio > Projects > More Settings > Virtual Device Manager
- Follow the prompts to create a new virtual device (I tried Pixel 5)
- Start the virtual device on Android Studio
- Execute the command from the project root to deploy to the virtual device

`cordova emulate android`

- *__Note__: The emulator didn't pickup `http://niko-server`, I had to update the code to use the server IP address `http://192.168.0.109` for the emulator to connect to the backend and pull kamehouse*
