# Description:

This application (still heavily under construction) will contain the following modules: 
* Manage my media files
* Control multiple VLC Players through their web API
* Test API endpoints to practice different frontend frameworks
* Login system
* Application administration view 
* Integration with social networks and popular APIs
* About and contact us
* Newsletter functionality

The main idea of this application is to keep improving and learning best practices of software development with Java and frontend technologies, so if you are a software developer and can look through the code and see vulnerabilities or things to improve i'd be more than happy to hear about them!

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
************
# Compilation:
- Compile using `mvn clean package [compilation option]`.

| Compilation option | Usage | Description | 
| ------------------ | ----- | ----------- |
| -P | -P:qa , -P:prod , -P:dev | profile to set variables depending on the environment |

*********************
# Execution in eclipse:
- To run from eclipse, deploy into a configured tomcat server inside eclipse.

*************
# Installation:
- Deploy copying the war into the webapps directory of your tomcat installation

*********************
# Other notes:

### Make VLC start and stop commands work:
- Make sure vlc executable is in the user's PATH. In linux it's added by default when vlc is installed. In windows I need to manually add the path to the executable to my user's PATH environment variable. To test that it works, open a command prompt and type vlc to see if it finds the executable or if it throws an error that it can't find it.

- The commands to start and stop vlc (and possibly other system commands) don't work if tomcat is run as a service in windows, even if it's configured to run as a service with my user. To fix this, uninstall the service, add a shortcut to the $HOME/programs/apache-tomcat/bin/startup.bat script in the windows startup folder (Currently in windows 10 it's $HOME\AppData\Roaming\Microsoft\Windows\Start Menu\Programs\Startup) so tomcat runs when I logon. Edit the windows shortcut and in the field 'Start in' change from $HOME/programs/apache-tomcat/bin to $HOME/programs/apache-tomcat otherwise it will create the application logs in $HOME/programs/apache-tomcat/bin/logs instead of $HOME/programs/apache-tomcat/logs

- To make the command windows start minimized, update catalina.bat and in the line where it says 'set _EXECJAVA=start "%TITLE%" %_RUNJAVA%' add /min after the start: 'set _EXECJAVA=start /min "%TITLE%" %_RUNJAVA%'

- For the vlc start and stop (and possibly other system commands) to work in Linux, if I have any tomcat installed as a service, run sudo apt-get remove tomcatX, sudo apt-get purge tomcatX, remove everything. Download the zip or tar.gz from the tomcat website, unpack it in $HOME/programs/apache-tomcat and start it with $HOME/programs/apache-tomcat/bin/startup.sh. Tomcat should start with my current user and the command to start and stop vlc should work. 
- To run on startup, with my current user, create a script where I cd to $HOME/programs/apache-tomcat (TOMCAT_HOME) and then run ./bin/startup.sh in that script. I need to do it this way because I need to be on $HOME/programs/apache-tomcat when I run startup.sh. If I run the script from $HOME/programs/apache-tomcat/bin, it will create the application logs in $HOME/programs/apache-tomcat/bin/logs instead of $HOME/programs/apache-tomcat/logs. Then edit my cron jobs with 'crontab -e' and add the following entry: 
'@reboot /bin/bash /PATH-TO-MY-SCRIPT/tomcat-startup.sh'
- Also update the script startup.sh and as the second line add 'export DISPLAY=:0' otherwise vlc start will fail because DISPLAY env variable won't be set at reboot time when tomcat is being started. I don't need to set it if I run startup.sh from my desktop but if I schedule it with cron, startup.sh needs to be updated with that export.

*********************
# ChangeLog:
#### v0.17
- Added cobertura to the build process to maintain a minimum test coverage
- Added backend functionality to shutdown the pc, cancel a scheduled shutdown or check the status of a shutdown command
- Added backend functionality to get the list of my video playlists
- Added backend functionality to start, stop and get the status of a local VLC player
- Added test page to test new apis
- Added test page to control a local VLC player
#### v0.16
- Added backend functionality to support multiple VLC Players and register them in the application
#### v0.15
- Added backend support to get the current playlist
- Added backend support to browse for a file in the server where VLC Player is running through the browse.json API
#### v0.14
- Added backend support to execute commands and check the status of a VLC Player configured in the application context
#### v0.13
- Added error mapping for status 405
- Moved validations from model to service layer in test models
- Added error mappings for exceptions in web.xml
- Redirect to error pages from angular when getting errors from the backend
- Updated functions to import header and footer and newsletter
- Updated javadocs in java and js
- Minor java and js code refactors
- Added validations to the ApplicationUser in the backend
- Fixed bugs
#### v0.12
- Secured application through spring security
- Implemented login functionality
- Updated jsp error pages
- Reorganized jsp file structure
- Redesigned header
- Updated ehcache endpoint to make it more RESTful
- Fixed bugs
#### v0.11
- Added ehcache and UI to manage it
- Updated UI to dark theme
#### v0.10
- Renamed project to kame-house
- Updated and redesigned frontend with a responsive ui
#### v0.09
- Updated and renamed project to 'base-app' to be used as a base for future web applications
#### v0.08
- CRUD frontend in jsp and in angular for the test endpoints
#### v0.07
- Added a very basic CRUD frontend in jsp for the test endpoints
#### v0.06
- Added api versioning. Api version 1
#### v0.05
- Added support and maven profile for oracle database
#### v0.04
- Test endpoints working with CRUD operations using JPA in both MySQL and HSQLDB
- Defined profiles for prod and dev. Prod is the default profile and uses MySQL and dev uses HSQLDB. 
- The unit tests use HSQLDB
#### v0.03
- Test endpoints working with CRUD operations in an InMemory repository
#### v0.02
- Test endpoints working returning preconfigured beans
#### v0.01
- Initial setup. mobile-inspections project