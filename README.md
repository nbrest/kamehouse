### Dev branch status:
[![Build Status](https://travis-ci.org/nbrest/java.web.kamehouse.svg?branch=dev)](https://travis-ci.org/nbrest/java.web.kamehouse)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?branch=dev&project=com.nicobrest%3Ajava-web-kame-house&metric=alert_status)](https://sonarcloud.io/dashboard?id=com.nicobrest%3Ajava-web-kame-house&branch=dev)
[![CodeFactor](https://www.codefactor.io/repository/github/nbrest/java.web.kamehouse/badge/dev)](https://www.codefactor.io/repository/github/nbrest/java.web.kamehouse/overview/dev)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/3d9e85a73da34684b042a6c85bd35607)](https://www.codacy.com/manual/nbrest/java.web.kamehouse?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=nbrest/java.web.kamehouse&amp;utm_campaign=Badge_Grade)
[![codecov](https://codecov.io/gh/nbrest/java.web.kamehouse/branch/dev/graph/badge.svg)](https://codecov.io/gh/nbrest/java.web.kamehouse)

![GitHub last commit (branch)](https://img.shields.io/github/last-commit/nbrest/java.web.kamehouse/dev)

# Description:

This application (still heavily under construction) will contain the following modules: 

* Manage my media files (store their location and information in a database and create interfaces to play them)
* Control multiple VLC Players through a custom interface using VLC's http API
* Control the server running the application (shutdown, suspend, lock screen, unlock screen, etc)
* Login system
* Application administration view (secured through Spring Security)
* About and contact us
* Newsletter functionality
* Test API endpoints to practice different frontend frameworks
* Integration with social networks and other popular APIs

The main idea of this application is to keep improving and learning best practices of software development with Java and frontend technologies, so if you are a software developer and can look through the code and see vulnerabilities or things to improve I'd be more than happy to hear about them!

The project uses **Maven** as a **SCM**. It is configured to validate the test coverage with **cobertura**, validate code with **findbugs** and the style with **checkstyle**.

##### Java frameworks/libraries:

* Spring
* Spring Security
* Hibernate
* Hsqldb
* Ehcache

##### Javascript frameworks/libraries:

* Angular
* jQuery

##### SCM:

* Maven 
* Trello for organizing development tasks

*********************
# Compilation:

* Compile using `mvn clean install [compilation option]` .

| Compilation option | Usage | Description | 
| ------------------ | ----- | ----------- |
| -P | -P:prod -P:qa -P:dev | Default profile is prod. It uses mysql.qa uses oracle and dev uses hsql in memory db |

*********************
# Execution in eclipse:

* To run from eclipse, deploy into a configured tomcat server inside eclipse.

*************
# Installation:

* Deploy copying the war into the webapps directory of your tomcat installation

*************
# External dependencies:

This web application interacts with other applications that need to be installed on the server to execute certain functionality. These external dependencies are:

* VLC Player (https://www.videolan.org/)
* VNC Server. Any vnc server will do (https://www.tightvnc.com/)
* VNCDoTool (https://github.com/sibson/vncdotool)
* gnome-screensaver-command in Ubuntu (http://manpages.ubuntu.com/manpages/trusty/man1/gnome-screensaver-command.1.html)

The application will load even without these installed, however some functionality will not work without them.

*********************
# Live demo:

This application is hosted in https://www.nicobrest.com/kame-house/ so you can check it out and play around with it and report any issues :) It's hosted in an Amazon AWS Free Tier EC2 Ubuntu 18 running with mysql. Most of the external dependencies are not installed though and it's not configured to execute shutdown or lock commands. So you can test the UI (remember I'm a backend developer :p) and some of the functionality in the VLC player and test module with some limitations. For example, AWS EC2 Ubuntu's kernel isn't compiled with audio modules (not even dummy), so audio will always revert to 0%, even if you update it with the buttons or slider.

The responsive layout was developed and tested for Samsung S8, Note8+ (and Pixel 2 using chrome dev tools). It's neither tested nor supported in other mobile devices. It was also tested mainly on Chrome and Firefox on desktop. It most certainly needs several more breakpoints and fixes for other devices and browsers.

*********************
# Other notes:

### Setup/Troubleshoot linux commands:

* Make sure the user running tomcat has sudo set for the commands that require it in CommandLine.java. I tried setting those without sudo and they don't work (tested on Ubuntu 16).

### Setup/Troubleshoot VLC start and stop commands:

* Make sure vlc executable is in the user's PATH. In linux it's added by default when vlc is installed. In windows I need to manually add the path to the executable to my user's PATH environment variable. To test that it works, open a command prompt and type vlc to see if it finds the executable or if it throws an error that it can't find it.

* The commands to start and stop vlc (and possibly other system commands) don't work if tomcat is run as a service in windows, even if it's configured to run as a service with my user. To fix this, uninstall the service. Download tomcat and extract it to $HOME/programs/apache-tomcat. Add a shortcut to the $HOME/programs/apache-tomcat/bin/startup.bat script in the windows startup folder (Currently in windows 10 it's $HOME\AppData\Roaming\Microsoft\Windows\Start Menu\Programs\Startup) so tomcat runs when I logon. Edit the windows shortcut and in the field 'Start in' change from $HOME/programs/apache-tomcat/bin to $HOME/programs/apache-tomcat otherwise it will create the application logs in $HOME/programs/apache-tomcat/bin/logs instead of $HOME/programs/apache-tomcat/logs

* To make the command windows start minimized, update catalina.bat and in the line where it says 'set _EXECJAVA=start "%TITLE%" %_RUNJAVA%' add /min after the start: 'set _EXECJAVA=start /min "%TITLE%" %_RUNJAVA%'

* For the vlc start and stop (and possibly other system commands) to work in Linux, if I have any tomcat installed as a service, run sudo apt-get remove tomcatX, sudo apt-get purge tomcatX, remove everything. Download the zip or tar.gz from the tomcat website, unpack it in $HOME/programs/apache-tomcat and start it with $HOME/programs/apache-tomcat/bin/startup.sh. Tomcat should start with my current user and the command to start and stop vlc should work.
* To run on startup, with my current user, create a script where I cd to $HOME/programs/apache-tomcat (TOMCAT_HOME) and then run ./bin/startup.sh in that script. I need to do it this way because I need to be on $HOME/programs/apache-tomcat when I run startup.sh. If I run the script from $HOME/programs/apache-tomcat/bin, it will create the application logs in $HOME/programs/apache-tomcat/bin/logs instead of $HOME/programs/apache-tomcat/logs. Then edit my cron jobs with 'crontab -e' and add the following entry: 

'@reboot /bin/bash /PATH-TO-MY-SCRIPT/tomcat-startup.sh'

* Also update the script startup.sh and as the second line add 'export DISPLAY=:0' otherwise vlc start will fail because DISPLAY env variable won't be set at reboot time when tomcat is being started. I don't need to set it if I run startup.sh from my desktop but if I schedule it with cron, startup.sh needs to be updated with that export.

### Setup/Troubleshoot lock, unlock and wake-up screen commands:

* Setup a vnc server (I use tightvnc on windows and the native desktop sharing tool in ubuntu) running in the same server as the application. Unlock screen is done through vncdotool.
* Install vncdotool (follow https://vncdotool.readthedocs.io/en/latest/install.html) in the same server that runs the application. Test it to make sure you can execute commands through it using the command line.
* Encode user password with base64 for the user and store it in a file specified by the property unlock.screen.pwd.file. This file should be readable only by the user, hidden from anyone else. The application will decode and type this password to unlock the screen.
* If the vnc server is configured with a password (it should!), also set the file pointed by vnc.server.pwd.file with the vnc server password encoded. This password will be used by vncdo to execute the commands through vnc. Again, this file contains an encoded password so it should be only readable by the user owning this process.
* Make sure vncdo in installed to /usr/local/bin/vncdo in linux or update CommandLine.java to point to where it is installed. Using just vncdo without the absolute path got me command not found. It needs the absolute path or some other fix.
* Using a vnc server and vncdotool is the only way I found to unlock the screen remotely on windows 10 (also works on ubuntu). If you are reading this and have a better solution, please contact me.
* Lock screen command on linux relies on gnome-screensaver-command to do the lock. Install it with sudo apt-get install gnome-screensaver. The command line could easily be changed to use vncdo and hotkeys to lock the screen for other linux versions (tested on ubuntu 16).

*********************
# ChangeLog:

#### v1.00

##### Huge backend code refactor to improve the code quality and make it easier to add new entities, commands and tests with much less overhead. The backend code is very different from the previous version to this one.

* Implemented fixes to issues reported by sonarcloud
* Added abstract classes in all layers to group common functionality
* Removed a lot of duplicated code
* Refactored tests in all layers to reduce the overhead of adding new tests
* Created abstract test classes to group common test functionality
* Created test utils for each entity in the application to simplify creation of test data and validation of all attributes of the entities
* Refactored system command service and admin and system commands to make it easier to add new commands
* Refactored admin command controllers to group common functionality
* Decoupled functionality to separate classes and utility classes
* Fixed exception handling
* Added DTOs in the controller layer where needed
* Replaced maps with proper entities in the application
* Fixed several bugs
* Moved angular app to static html from jsp
* Added meta tag to load app in fullscreen from android home
* Fixed header and footer not loading when backend is down
* Removed need for symlinks for static files in the project setup
* Moved error pages content to static html from jsps
* Changed site under construction alert to a modal
* Fixed exception type returned in update entity errors
* Refactored a lot of methods name to make them more standard and readable
* Grouped CRUD operations in abstract classes in all layers in the main code and tests to make it much easier to add new entities
* Fixed @oneToMany persistence issues with ApplicationUsers and ApplicationRoles
* Fixed model-and-view test page
* Removed sensitive session information sent through the session status API
* Made vlc player volume slider rounded
* Split vlc control icons into more rows
* Changed vlc update idle time from 15s to 4s
* Fixed position always coming as 0 bug

#### v0.22

* Updated several vlc player buttons
* Added state to vlc player buttons that have state (pressed, unpressed)
* Updated playlist selected item style
* Updated vlc player layout
* Added get vlcrc status in debug mode
* Updated playlist selector style
* Reduced size of audio buttons
* Updated vlcRcService to return null when not playing vlc
* Added global js object in vlc player
* Updated slider styles
* Added custom logging for frontend code with log level and timestamp
* Fixed getTimestamp in frontend to use the correct timezone
* Moved static content from jsps to html (to be served by apache httpd but also available straight from tomcat)
* Added CustomOnSuccess handler to handle authentication redirects
* Created local maven repo for oracle jdbc and any other future libraries I can't get from the official maven repos (so they can be imported automcatically in CI servers such as travis)
* Added integration to travis CI
* Added integration to online code quality analisys tools (Codacy, CodeFactor, SonarCloud, CodeCov, Coveralls)
* Fixed bugs

#### v0.21

* Completely changed vlc player UI
* Added cache for vlc players
* Updated ehcache configuration for all caches
* Added debug mode toggle to hide/show debug table
* Added collapsible playlist with clickable items and highlighted current item
* Updated animations on buttons in vlc player
* Added sliding bars to display and set time and volume
* Added filename being played display
* Added current/total play time display
* Updated existing buttons
* Added more buttons with more functionality (cycle audio, cycle track, cycle aspect ratio and others)
* Added support for websockets
* Created test websockets page
* Made contact us and newsletter use a darker theme
* Updated favicon
* Fixed some bugs

#### v0.20

* Deployed for the first time to https://www.nicobrest.com/kame-house/ :) 
* Updated initial version of vlc player page with most functionality
* Fixed issue with csrf. Now all requests work with csrf enabled. This fixes angular-1 app in test-module too
* Formatted test-apis page
* Updated header. Made it more responsive, updated nav, updated logged in message displayed
* Changed style to use google fonts 'Varela Round' in all website
* Added font borders to hero banner text
* Updated banners fonts, texts and images.
* Created test-module page to group all test-module related functionality there
* Updated homepage, about and contact us content and layout
* Updated server management layout
* Updated ehcache layout
* Updated buttons to square instead of rounded
* Added animation on hover to home image links
* Restructured css and js files
* Fixed footer positioning on shorter pages
* Fixed mobile layout on test module db tables
* Fixed bugs

#### v0.19

* Added wake up screen functionality (backend and frontend)
* Added suspend server functionality (backend and frontend)
* Removed deprecated vlc-player test page
* Added initial version of vlc player page based on the test page
* Updated header with link to vlc player
* Updated UI to a darker color theme
* Cleaned up test apis page

#### v0.18

* Added lock and unlock screen backend functionality
* Added admin view to manage system shutdown and lock and unlock screen
* Refactored code
* Fixed bugs

#### v0.17

* Added cobertura to the build process to maintain a minimum test coverage
* Added backend functionality to shutdown the pc, cancel a scheduled shutdown or check the status of a shutdown command
* Added backend functionality to get the list of my video playlists
* Added backend functionality to start, stop and get the status of a local VLC player
* Added test page to test new apis
* Added test page to control a local VLC player
* Split the application into different packages (admin, main, media, systemcommand, testmodule, utils and vlcrc) as a first step to eventually make them independent modules/services

#### v0.16

* Added backend functionality to support multiple VLC Players and register them in the application

#### v0.15

* Added backend support to get the current playlist
* Added backend support to browse for a file in the server where VLC Player is running through the browse.json API

#### v0.14

* Added backend support to execute commands and check the status of a VLC Player configured in the application context

#### v0.13

* Added error mapping for status 405
* Moved validations from model to service layer in test models
* Added error mappings for exceptions in web.xml
* Redirect to error pages from angular when getting errors from the backend
* Updated functions to import header and footer and newsletter
* Updated javadocs in java and js
* Minor java and js code refactors
* Added validations to the ApplicationUser in the backend
* Fixed bugs

#### v0.12

* Secured application through spring security
* Implemented login functionality
* Updated jsp error pages
* Reorganized jsp file structure
* Redesigned header
* Updated ehcache endpoint to make it more RESTful
* Fixed bugs

#### v0.11

* Added ehcache and UI to manage it
* Updated UI to dark theme

#### v0.10

* Renamed project to kame-house
* Updated and redesigned frontend with a responsive ui

#### v0.09

* Updated and renamed project to 'base-app' to be used as a base for future web applications

#### v0.08

* CRUD frontend in jsp and in angular for the test endpoints

#### v0.07

* Added a very basic CRUD frontend in jsp for the test endpoints

#### v0.06

* Added api versioning. Api version 1

#### v0.05

* Added support and maven profile for oracle database

#### v0.04

* Test endpoints working with CRUD operations using JPA in both MySQL and HSQLDB
* Defined profiles for prod and dev. Prod is the default profile and uses MySQL and dev uses HSQLDB.
* The unit tests use HSQLDB

#### v0.03

* Test endpoints working with CRUD operations in an InMemory repository

#### v0.02

* Test endpoints working returning preconfigured beans

#### v0.01

* Initial setup.mobile-inspections project

