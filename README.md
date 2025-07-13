| [Home](/README.md) | [Docs](/docs/README.md) |
---------------------------------------------------------------

*********************

## Dev branch status:

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?branch=dev&project=nbrest_kamehouse&metric=alert_status)](https://sonarcloud.io/dashboard?id=nbrest_kamehouse&branch=dev)
[![GitHub last commit (branch)](https://img.shields.io/github/last-commit/nbrest/kamehouse/dev)](https://github.com/nbrest/kamehouse/tree/dev)

# Description:

This application (forever under construction) contains the following modules:

[admin](/kamehouse-admin/README.md)

[auth](/kamehouse-auth/README.md)

[cmd](/kamehouse-cmd/README.md)

[commons](/kamehouse-commons/README.md)

[commons-core](/kamehouse-commons-core/README.md)

[commons-test](/kamehouse-commons-test/README.md)

[desktop](/kamehouse-desktop/README.md)

[groot](/kamehouse-groot/README.md)

[media](/kamehouse-media/README.md)

[mobile](/kamehouse-mobile/README.md)

[shell](/kamehouse-shell/README.md)

[snape](/kamehouse-snape/README.md)

[tennisworld](/kamehouse-tennisworld/README.md)

[testmodule](/kamehouse-testmodule/README.md)

[ui](/kamehouse-ui/README.md)

[vlcrc](/kamehouse-vlcrc/README.md)

The main idea of this application is to keep improving and learning best practices of software development

The backend functionality is built mostly with `java` and `bash` shell scripts. Part of the backend functionality is also built with `php` and `python`

The frontend and mobile app is built in plain `javascript/typescript` mostly without using frameworks like vue, angular or react

The desktop app is built with `python` using `pyqt5` to render the ui components

Follow the readme on each module to see the functionality done by each of them

The project uses **Maven** for building and deployment.

The test coverage is done with **jacoco**. **spotbugs** and **sonarqube** are used for code quality
and **checkstyle** for coding style.

## Node:

- My custom nodejs modules are hosted in the node [modules](/node/modules/) folder

*********************

# Live demo:

This application is hosted in https://www.nicobrest.com/kame-house/ so you can check it out and play
around with it and report any issues :)

It's currently running on a Raspberry Pi, so might not be up all the time

# Docker demo:

As described in [Docker Setup](/docs/docker/docker-setup.md), you can have a local demo of kamehouse
running to test most of the functionality by executing one docker command

It's much simpler than going through the complex process of doing all the manual steps to have an
installation in your local described in [Installation](/docs/installation/installation.md)

I have a sample docker container running at https://docker-demo.nicobrest.com/kame-house/ (not
always online)

# Target Devices:

The responsive layout and mobile app layout was developed and tested for several android phones. It
was also tested mainly on Chrome and Firefox on desktop. It most certainly needs several more
breakpoints and fixes for other devices and browsers.

*********************

# External dependencies:

KameHouse interacts with other applications that need to be installed on the server to execute
certain functionality. These external dependencies are:

### VLC Player

- Download [VLC Player](https://www.videolan.org/)
- One of the main functionalities of kamehouse is to control a vlc player on the local machine or
  remote, through http
- Configure the vlc player to expose it's LUA HTTP Web api so it can be accessed by kamehouse
    - Tools > Preferences > All Settings > Interfaces > Main Interfaces: **[x]** Web
    - Tools > Preferences > All Settings > Interfaces > Main Interfaces > Lua: Set HTTP password to
      1 (or anything else and then update the password in the vlcplayer table in kamehouse)

### VNC Server

- Install any [VNC Server](https://www.tightvnc.com/)
- Store encrypted vnc server password as described in [kamehouse-shell](/kamehouse-shell/README.md)
- VNC protocol is used for example to send a double mouse click to toggle fullscreen on a vlc player,
  which works much more reliably than vlc's native fullscreen toggle. Another usage is to unlock a
  locked screen

### Gnome Screensaver Command (linux)

- Download [gnome-screensaver-command](http://manpages.ubuntu.com/manpages/trusty/man1/gnome-screensaver-command.1.html)

- It is needed lock screen from kamehouse in debian/ubuntu based **Linux**
- Store encrypted user password as described in [kamehouse-shell](/kamehouse-shell/README.md) in order
  to unlock the screen through kamehouse

### Git Bash (windows)

- Download [Git Bash](https://www.git-scm.com/download/win)
- Needed to run kamehouse shell scripts in **Windows**
- Make sure it's installed to `C:\Program Files\Git\bin\bash.exe`

### SSH Server

- This is only really needed in a remote host that will be controlled by a kamehouse docker
  container
- When running kamehouse natively either in windows or linux, an ssh server is not needed

### Unzip

- In most systems, it should be available out of the box
- Used in deployment script to install kamehouse-cmd in **Windows** (Download an unzip tool and make
  sure unzip.exe is on the PATH)

KameHouse will load even without these installed, however some functionality will not work without
them.

*********************

## Other Sections:

[Documentation](/docs/README.md)

[ChangeLog](/changelog.md)
