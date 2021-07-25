## Dev branch status:
[![Build Status](https://travis-ci.org/nbrest/java.web.kamehouse.svg?branch=dev)](https://travis-ci.org/nbrest/java.web.kamehouse)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?branch=dev&project=com.nicobrest%3Ajava-web-kame-house&metric=alert_status)](https://sonarcloud.io/dashboard?id=com.nicobrest%3Ajava-web-kame-house&branch=dev)
[![CodeFactor](https://www.codefactor.io/repository/github/nbrest/java.web.kamehouse/badge/dev)](https://www.codefactor.io/repository/github/nbrest/java.web.kamehouse/overview/dev)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/3d9e85a73da34684b042a6c85bd35607)](https://www.codacy.com/manual/nbrest/java.web.kamehouse?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=nbrest/java.web.kamehouse&amp;utm_campaign=Badge_Grade)
[![codecov](https://codecov.io/gh/nbrest/java.web.kamehouse/branch/dev/graph/badge.svg)](https://codecov.io/gh/nbrest/java.web.kamehouse)
[![GitHub last commit (branch)](https://img.shields.io/github/last-commit/nbrest/java.web.kamehouse/dev)](https://github.com/nbrest/java.web.kamehouse/tree/dev)
 
# Description:

This application (forever under construction) contains the following modules: 

[Admin](kamehouse-admin/README.md)

[Cmd](kamehouse-cmd/README.md)

[Commons](kamehouse-commons/README.md)

[Commons Core](kamehouse-commons-core/README.md)

[Commons Test](kamehouse-commons-test/README.md)

[GRoot](kamehouse-groot/README.md)

[Media](kamehouse-media/README.md)

[TennisWorld](kamehouse-tennisworld/README.md)

[TestModule](kamehouse-testmodule/README.md)

[UI](kamehouse-ui/README.md)

[VlcRc](kamehouse-vlcrc/README.md)

The modules commons, commons-core and commons-test are jar libraries imported by the other modules. 

Cmd is a command line tool written in java for tasks that are better run through the command line
 rather than through a webapp.

GRoot is a webapp built with js and php to do admin stuff I usually do through the command line.

All the other modules are webapps deployable to tomcat.
All of them are API based except for the UI module that contains the frontend code and connects
 to the other modules through their APIs.

The main idea of this application is to keep improving and learning best practices of software
 development with Java and frontend technologies. 
 So if you are a software developer and can look through the code and see vulnerabilities or
  things to improve I'd be more than happy to hear about them!

The project uses **Maven** as a **SCM**. It is configured to validate the test coverage with **jacoco**, validate code with **findbugs** and the style with **checkstyle**.

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
* Travis-ci and Jenkins for CI
* Trello for organizing tasks
* Postman for automated API tests
* Sonarcube, Codacy, Codefactor, Codecov for quality checks

*********************

# Live demo:

This application is hosted in https://www.nicobrest.com/kame-house/ so you can check it out and play around with it and report any issues :) 

It's currently running on a Raspberry Pi, so might not be up all the time

~~It's hosted in an Amazon AWS Free Tier EC2 Ubuntu 18 running with mysql. Most of the external
 dependencies are not installed though and it's not configured to execute shutdown or lock
  commands. So you can test the UI (remember I'm a backend developer :p) and some of the
   functionality in the VLC player and test module with some limitations. For example, AWS EC2
    Ubuntu's kernel isn't compiled with audio modules (not even dummy), so audio will always
     revert to 0%, even if you update it with the buttons or slider.~~

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

*********************

## Other Sections:

[Dev Environment Setup](dev-environment-setup.md)

[Compilation](compilation.md)

[Installation](installation.md)

[Troubleshoot Issues](troubleshoot-issues.md)

[Logging Strategy](logging-strategy.md)

[ChangeLog](changelog.md)