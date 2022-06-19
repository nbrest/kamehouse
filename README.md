## Dev branch status:

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?branch=dev&project=nbrest_kamehouse&metric=alert_status)](https://sonarcloud.io/dashboard?id=nbrest_kamehouse&branch=dev)
[![GitHub last commit (branch)](https://img.shields.io/github/last-commit/nbrest/kamehouse/dev)](https://github.com/nbrest/kamehouse/tree/dev)
 

# Description:

This application (forever under construction) contains the following modules: 

[Admin](kamehouse-admin/README.md)

[Cmd](kamehouse-cmd/README.md)

[Commons](kamehouse-commons/README.md)

[Commons Core](kamehouse-commons-core/README.md)

[Commons Test](kamehouse-commons-test/README.md)

[GRoot](kamehouse-groot/README.md)

[Media](kamehouse-media/README.md)

[Mobile](kamehouse-mobile/README.md)

[Shell](kamehouse-shell/README.md)

[TennisWorld](kamehouse-tennisworld/README.md)

[TestModule](kamehouse-testmodule/README.md)

[UI](kamehouse-ui/README.md)

[VlcRc](kamehouse-vlcrc/README.md)

The modules **commons**, **commons-core** and **commons-test** are jar libraries imported by the other java modules. 

**Cmd** is a command line tool written in java for tasks that are better to execute through the command
 line rather than through a webapp.

**Mobile** is a module to build native apps for android and ios that connect to my kamehouse servers and load the content from there using apache cordova.

**GRoot** is a webapp built with js and php to do admin stuff I usually do through the command line.

**Shell** is a collection of most of my shell scripts. Some of them are called from GRoot to execute admin tasks.

All the other modules are webapps deployable to tomcat.
All of them are API based except for the UI module that contains the frontend code and connects
 to the other modules through their APIs.

The main idea of this application is to keep improving and learning best practices of software
 development with Java and frontend technologies. 
 So if you are a software developer and can look through the code and see vulnerabilities or
  things to improve I'd be more than happy to hear about them!

The project uses **Maven** as a **SCM**. It is configured to validate the test coverage with **jacoco**, validate code with **spotbugs** and the style with **checkstyle**.

### Java frameworks/libraries:

* Apache Commons
* Bouncy Castle
* Ehcache
* Hibernate
* Hsqldb
* Jsoup
* Quartz Scheduler
* Spring (Core, Security, Session, Web)

### Javascript frameworks/libraries:

* Angular
* jQuery
* SockJs
* Stomp

### Software Configuration Management:

* Maven 
* Jenkins for CI
* Trello for organizing tasks
* Postman for API tests
* Sonarcube quality checks

*********************

# Live demo:

This application is hosted in https://www.nicobrest.com/kame-house/ so you can check it out and play around with it and report any issues :) 

It's currently running on a Raspberry Pi, so might not be up all the time

# Docker demo:

As described in [Docker Setup](docker-setup.md), you can have a local demo of kamehouse running to test most of the functionality by executing one docker command

It's much simpler than going through the complex process of doing all the manual steps to have an installation in your local described in [Installation](installation.md)

I have a sample docker container running at https://docker-demo.nicobrest.com/kame-house/ (not always online)

# Target Devices:

The responsive layout was developed and tested for Samsung S8, Note8+ (and Pixel 2 using chrome dev tools). It's neither tested nor supported in other mobile devices. It was also tested mainly on Chrome and Firefox on desktop. It most certainly needs several more breakpoints and fixes for other devices and browsers.

*********************

# External dependencies:

This web application interacts with other applications that need to be installed on the server to execute certain functionality. These external dependencies are:

* **VLC Player** (https://www.videolan.org/)
* **VNC Server**. Any vnc server will do (https://www.tightvnc.com/)
* **VNCDoTool** (https://github.com/sibson/vncdotool)
* **gnome-screensaver-command** in **Ubuntu** (http://manpages.ubuntu.com/manpages/trusty/man1/gnome-screensaver-command.1.html)

The application will load even without these installed, however some functionality will not work without them.

# Stream videos to chromecast:

- In VLC player > Playback > Renderer > Select chromcast device to stream to

*********************

## Other Sections:

[Installation](installation.md)

[Execution](execution.md)

[Docker Setup](docker-setup.md)

[Dev Environment Setup](dev-environment-setup.md)

[Troubleshoot Issues](troubleshoot-issues.md)

[ChangeLog](changelog.md)
