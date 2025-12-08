| [Home](/README.md) | [Docs](/docs/README.md) |
---------------------------------------------------------------

*********************

# ChangeLog:

#### v10.12

- Renamed vlc player to ztv player
- Renamed openwrt to pegasus
- Add support for HEIC images in kamehouse desktop background slideshow
- Fixed bugs

#### v10.11

- Added kamehouse-openwrt to default servers
- Added functions to log to dmeg and uncolored logs
- Updated default home network settings
- Updated default position of desktop elements
- Updated ztv player icons
- Added reboot server button to vlc player debug mode
- Updated default location to Madrid in docker container and desktop weather widget
- Fixed bugs

#### v10.10 (vMaradona.Messi)

- Added shell script and ui in groot to get the lists of successful and invalid desktop backgrounds processed
- Updated default lan ips to 192.168.99.x
- Added kamehouse-desktop tab in groot server manager
- Added functionality to list successful and invalid backgrounds into list files in desktop
- Updated default styles and position of desktop widgets
- Added background slideshow widget to kamehouse desktop
- Added weather widget to kamehouse desktop
- Updated groot server manager and crud icons
- Added outline to text widgets
- Added option to restart kamehouse desktop on deployment in kamehouse.cfg
- Added ztv player widget to kamehouse desktop with sync of vlc status both via websocket and http
- Split non tomcat modules into separate tables in groot server manager
- Fixed sonar bugs
- Fixed bugs

#### v10.09

- Added script to update kamehouse config values
- Added toggle for keep alive scripts in groot server manager
- Added buttons to start and stop kamehouse desktop from groot server manager
- Updated websockets endpoints to use single endpoint for vlc status and playlist
- Moved vlc status and playlist websockets from polling from the clients to pushing the updates from the server to the topic
- Removed redundant sync loops for vlc status and playlist now that the updates on the view are pushed from the server
- Added vlc status websocket endpoint for desktop app
- Added kamehouse logo widget
- Added world cup logo widget
- Added kamehouse katakana widget
- Added clock widget
- Added kamehouse-desktop.cfg to configure desktop app
- Reorganized dockerfile to easier to manage scripts
- Moved kamehouse-desktop python script to desktop module
- Replaced label with hostname in desktop
- Added kamehouse logo to desktop icon
- Allow single instance of desktop app
- Added desktop status script
- Improved keep alive desktop script
- Fixed bugs

#### v10.08

- Added initial version of kamehouse-desktop module
- Added initial version of kamehouse-snape module
- Reorganized kamehouse shell scripts by module and functionality
- Fixed bugs

#### v10.07

- Updated mobile build version on settings page
- Created a separate module auth for login, authentication and session status and moved that logic away from ui module
- Moved remaining backend endpoints from ui module to test module. UI module now only contains static frontend code
- Removed buildVersion and buildDate from the session objects and loaded them directly on the footer from the ui build-info.json file
- Removed scattered .txt, .data and .cfg files loaded from the ui and webapps and replaced them with build-info.json
- Removed server name from the banner in some pages
- Updated footer backend offline message
- Added functionality to hide undeployed modules from groot server manager ui
- Added functionality to update footer with groot session on groot pages
- Added config to skip deploying certain kamehouse modules to kamehouse.cfg
- Added script name to start and end log lines
- Moved shell source files import check to one liners
- Updated ci full build script to deploy shell before all other modules
- Simplified source function import checks on shell
- Fixed bugs

#### v10.06

- Added manual decrypt/encrypt kamehouse secrets scripts for initialization of secrets file
- Fixed bugs

#### v10.05

- Moved init script env functions to top of scripts
- Cleaned up shell import functions error messages
- Fixed bugs

#### v10.04

- Refactored kamehouse shell scripts to set initial global config in functions
- Added disabled entry in ui debug mode log level dropdown
- Fixed bugs

#### v10.03

- Added log level disabled in kamehouse shell and ui
- Added option to log plain message to kamehouse shell log framework
- Refactored kamehouse shell framework to execute all setup functions from mainWrapper
- Added link to stop scrolling page from test module
- Removed fallback to unencrypted kamehouse secrets file
- Added pre parse cmd arguments function to shell scripts framework
- Refactored functions to execute ssh, scp and sftp commands from kamehouse shell
- Refactored mobile backend servers configuration to use more explicit values
- Refactored kamehouse servers list configuration to use more explicit values
- Refactored true/false parameters in kamehouse shell functions to use more explicit values
- Use get-kamehouse-secret.sh script from groot to access the required kamehouse secrets
- Fixed bugs

#### v10.02

- Moved all credentials to load from .kamehouse-secrets.cfg.enc
- Added support to encrypt .kamehouse-secrets.cfg to .kamehouse-secrets.cfg.enc
- Moved shell.pwd to .kamehouse-secrets.cfg
- Renamed media server to kamehouse-r2d2
- Added vlc stats view to ui in debug mode
- Added patch script to patch other kamehouse servers with current working copy
- Fixed vlc stats for mp3
- Fixed bugs

#### v10.01

- Added support for music playlists on vlc
- Updated default playlists path to .kamehouse/data/playlists
- Added option to use visualization filter for audio playlists
- Moved .kamehouse/ files to .kamehouse/config/
- Moved scripts data to .kamehouse/data/
- Added option to disable keep alive scripts in kamehouse.cfg
- Allowed to login from mobile app to backend servers running groot only without tomcat
- Added build version and date to deployment logs for all modules
- Merged groot ui into kamehouse-ui. Module kamehouse-groot is now only the backend apis
- Added vlc running process info file and vlc stats and tail log scripts
- Updated input validation in most endpoints to block forbidden chars for shell execution
- Added isDaemon option to execute shell scripts via groot
- Added html output lists to KameHouseCommandResult and moved conversion of outputs to html to the backend
- Moved standardOutput to be returned as a list rather than a string in groot execute as well
- Renamed references to old groot execute api response fields in the ui
- Fixed vlc start/stop and load playlists and playlist content from docker controlling remote host
- Moved vlc playlist and file handling to use linux paths in all places upto vlc-start script
- Refactored SystemCommand to KameHouseShellScript and added a KameHouseCommand interface
- Extracted SystemCommand.Output to a separate class KameHouseCommandResult
- Removed unnecessary layer of KameHouseSystemCommands
- Renamed SystemCommand references in the ui to KameHouseCommand
- Added option to skip building static code
- Moved remote control of docker host from ssh to https via groot when running from a docker container
- Refactored kamehouse system commands to go through kamehouse-shell for all commands
- Added a stop scrolling page
- Refactored parseArguments to allow extended arguments
- Fixed release version number on ui

#### v10.00

- Refactored reload playlist and playlist browser in vlc
- Added advanced options to vlc settings. Added options to hide playlist and playlist browser content
- Swapped fullscreen toggle buttons in vlc between main view and debug mode
- Added kamehouse startup service configuration to kamehouse.cfg
- Moved default values in kamehouse.cfg to a separate file
- Renamed local kamehouse servers
- Fixed initial http api sync configuration to reduce timeouts on large playlists
- Fixed vlc websockets message size and timeout configuration
- Fixed view resetting on api errors in vlc
- Fixed bugs

#### v9.03

- Created ${HOME}/.kamehouse/kamehouse.cfg configuration to modify build and run parameters
- Renamed kamehouse .config to kamehouse.cfg and .cred to shell.pwd
- Increased integration tests timeout to 20 minutes
- Added script to export minimal kamehouse shell to use on other projects
- Removed all hardcoded local network configurations and moved everything to be configurable in kamehouse.cfg
- Made the vlc video playlists path configurable on each server, so each server can load playlists from different paths
- Removed logic that uses the hostname to determine which playlists to access
- Moved hardcoded configured kamehouse servers and made them editable in ${HOME}/.kamehouse/kamehouse.cfg
- Enabled docker containers to control any host by setting docker host parameters in kamehouse.cfg
- Moved mobile app servers list to kamehouse.cfg to build the app to control a custom list of servers
- Fixed sync of data and files from remote host to docker container for any remote host
- Moved windows vlc start over ssh to use psexec when using docker to control a remote windows host
- Fixed bugs

#### v9.02

- **Moved javascript code to typescript**
- Refactored completely frontend code build process using typescript and npm
- Refactored and simplified build and deploy scripts for both standard and dev deployments and other shell scripts
- Refactored set-java-home script
- Renamed rc-local service to kamehouse-startup-service
- Embedded android apk status into an iframe on the downloads page
- Fixed scripts that need to avoid logging to file
- Removed option to tail-log in remote servers
- Removed ide specific setup and script parameters
- Updated under construction modal
- Updated vscode debugger configurations
- Updated groot tail log wait time
- Simplified apache httpd dev setup and configuration
- Removed access to static kamehouse-ui files from tomcat. Only accessible through apache httpd
- Updated log script runtime function
- Added more bash colors
- Added script name to log entries
- Moved shell scripts to default to log output to file
- Added esc and enter key press buttons in vlc player debugger
- Updated groot server manager tabs layout
- Started logging stderr in kamehouse-shell log files
- Added undeploy rc-local script
- Fixed sftp get error with bashrc login message 
- Fixed bugs

#### v9.01

- Updated debug mode styles and layout
- Updated footer logos
- Fixed header layout in low res desktop view
- Added toggle for sync loops in vlc player debug mode
- Fixed mobile requests data serializer
- Refactored collapsible div utils
- Added new image header component
- Updated vlc player buttons layout and added a config tab
- Added mobile class to get button function
- Added vlc player playlist and playlist browser size
- Added row number on playlist of current element playing
- Fixed sync issue between shown request and response in the ui debugger
- Moved ui debugger response body to the request/response table
- Added toggle visibility of request and response data on ui debugger
- Set ui debugger http request/response table to use a single instance of the template
- Added option to skip scrolling logs for ui debugger console log
- Extended ui debugger console log size
- Added rewind and fast forward 10 seconds buttons to vlc player
- Fixed new ui sonar bugs
- Add id to request/response logs in the ui
- Updated vlcrc status api to return initialized object rather than 404
- Removed default outline from buttons and select elements
- Updated during click button styles to a fading background
- Reduced ci container startup time wait
- Removed jquery from most of the code and cleaned up duplicate methods in dom utils
- Set kame logo spinning wheel for cursor wait on both webapp and mobile
- Disabled hover animation on image buttons on mobile app
- Added url to http response and api error logs in the ui
- Fixed mockCordova url parameter to test mobile app on browser
- Added single left click on vlc player debug mode 
- Fixed spinning wheel on mobile position
- Fixed JAVA_HOME for tomcat startup script
- Refactored jvncsender commands and added right click support
- Updated spinning wheel on mobile to kame logo
- Updated jvncsender to version 1.08
- Added ui and api to send mouse clicks to the server
- Restructured php code. Separated class definition php files from endpoint files
- Added docker status check on kamehouse docker server
- Added docker tab to groot server manager
- Added loading logo while loading groot session
- Updated global uncaught error handler function
- Added groot logo to kamehouse header
- Updated groot menu icons
- Updated groot homepage links
- Added kamehouse logo to spinning wheel modal and divs
- Added background light to logged user icons
- Fixed broken mock localhost on mobile
- Increased width of groot menu icons
- Added deploy kamehouse on all servers to ci full build
- Updated error banner text styles
- Updated tomcat log colors
- Updated banner text styles for mobile
- Added loading kamehouse.js on error pages when available to replace header and footer
- Added loading text in the header while loading the session
- Added suspend server button to vlc player debugger
- Added top command to admin system commands
- Removed extra permissions from kamehouse db user on mariadb
- Refactored send key press commands and added extra key press buttons
- Cleaned up unused kamehouse shell scripts
- Updated remove special chars script to handle audio files and playlists too
- Fixed remove special chars script to group multiple special chars into one
- Reorganized kamehouse shell scripts folders and bashrc functions
- Moved static groot php admin pages to html using js authorization like kamehouse admin pages 
- Added more banners
- Remove media tab from groot server manager
- Removed deprecated scripts from kamehouse shell
- Updated groot splash screen and header logos
- Removed unused tail logs from groot tail log
- Updated groot user icon
- Removed splashscreen text
- Fixed docker host ip on linux
- Fixed error banners background positions on mobile
- Fixed bugs

#### v9.00

- **The frontend code from this release is very different compared to the previous version**
- Refactored all javascript code to use classes
- Refactored all php code to use classes
- Updated vlc player connection error log level
- Added button for create all audio playlists in groot
- Moved screen controller buttons to a separate page from server management
- Added ESC, ENTER and ALT+TAB, WIN+TAB and right arrow key press buttons in the screen controller page
- Updated jvncsender to version 1.04
- Moved jvncsender commands to execute direcly from webapps rather than going through kamehouse-cmd except mouse clicks when running on docker
- Replaced mouse clicks from vncdo with jvncsender. Deprecated all use of vncdo
- Replaced mariadb nikolqs user with user kamehouse
- Removed hardcoded database credentials
- Updated mariadb setup scripts to work on windows as well
- Updated mariadb user and schema
- Moved home-synced to .kamehouse folder
- Removed dependency on kamehouse user's home for apache httpd on linux
- Automated setup of sudoers on linux
- Added parameter to set the log level of shell scripts on deployment
- Fixed permissions on .kamehouse folder
- Added validations of command line arguments for exec-script.sh
- Restricted sudo access for www-data and user running kamehouse
- Added script to excecute the full build on ci and added it to groot tail log
- Added integration tests for groot apis
- Removed database config from kamehouse-cmd that was causing random timeouts in integration tests
- Added timeout to system commands and to ssh commands
- Updated vlc create video playlists scripts 
- Moved error pages to be fully self contained under /kame-house/error directory
- Updated shell scripts exit status codes and exit loggings
- Added logging of shell scripts runtime
- Removed docker profile prod-ext
- Fixed long delay closing the modal in the ui on vlc start from docker on linux
- Fixed broken execute on docker host commands
- Fixed bugs

#### v8.22

- Added build-kamehouse log to groot tail logs
- Updated error view on tail log errors on groot
- Added more colors to maven builds
- Refactored vlc player sync loops
- Added periodic health check to vlc player loops
- Updated mobile settings styles
- Updated mobile config persistence functions
- Added vscode jsconfig.json
- Fixed most sonar bugs
- Fixed bugs

#### v8.21

- Updated github actions build
- Added sonar maven plugin
- Added sonarcloud scan to jenkins build
- Added color to maven build logs
- Added validation to docker run when running with tags
- Updated documentation
- Updated mysql references to mariadb
- Removed git pull all from deploy all servers script
- Updated docker host's subnet to use host's public ip for ssh
- Fixed links at the end of docker init script
- Fixed sonar bugs

#### v8.20

- **Upgraded project to Java 17, tomcat 10 and maven 3.9.3**
- Upgraded maven dependencies to latest versions
- Removed maven profiles dev (h2) and qa (oracle)
- Updated login and logout buttons to work on mobile as they work on desktop
- Added logo on header while loading session
- Fixed bugs

#### v8.19

- Added global generic exception handler to exception handler controller
- Added error output to integration tests run script for troubleshooting
- Fixed broken login and retries to perfectgym tennisworld
- Fixed bugs

#### v8.18

- Refactored api error response controller
- Fixed bugs

#### v8.17

- Updated error responses on all webapps and groot to always return a json with a standard error format
- Fixed kamehouse-shell scripts permissions
- Updated redirect modal with response code on unsuccessful authorization on mobile
- Updated docker build/run scripts
- Added more apis to mobile mock server
- Fixed bugs

#### v8.16

- Added java8-* and java11-* scripts to generate docker images for all major kamehouse releases from v0.10 to v8.14
- Fixed bugs

#### v8.15

- Updated docker scripts to allow building and running kamehouse release tags from v8.15 onward
- Updated android apk status page with build version
- Updated edit and delete icons
- Extended tail log timeout
- Updated tennisworld, testmodule and admin home styles
- Updated spring session config to run cleanup at default times
- Added retries to the booking request to allow to set custom number of retries
- Updated dockerfile to use a specific maven version
- Fixed bugs

#### v8.14

- Updated more banners to Japanese
- Added initial mock offline server to mobile app to test all pages offline
- Updated tennis world booking schedule job config execution times
- Added wrappers for json functions
- Updated login background
- Updated stringify identation
- Updated start/stop tomcat module buttons
- Added handler for all js uncaught errors
- Updated several icon sizes and replaced some other icons
- Updated webapp tabs info-image for ui
- Added info image to groot server manager command execution
- Added option to set title at the top or bottom on info image
- Added option to set Japanese banner title on crud manager
- Added dockerignore file
- Updated dockerfile to combine multiple run commands into one
- Updated dockerfile and docker build scripts to skip docker cache with a command line parameter
- Created docker containers for the first release of kamehouse (v0.10) and old java 8 releases and pushed them to docker hub
- Updated login and logout buttons
- Added timeout to more http requests on vlc player and groot
- Updated slideshow styles
- Limited length of log messages on js console and debugger
- Added option to skip ssl check on mobile http requests
- Added login and logout buttons in mobile settings tab
- Added shell script to rebuild latest docker images after every commit on jenkins automatically
- Updated groot header server manager icon 
- Updated build script to set environment for android build on linux
- Added steps on mobile readme to build apk on linux
- Added step on jenkins to deploy mobile app automatically on every commit
- Fixed bugs

#### v8.13

- **Refactored completely the UI compared to the previous release**
- Updated groot menu styles on mobile app and settings page
- Moved loading varela round fonts locally for offline connections
- Fixed loading static content directly from tomcat
- Moved jsps to load from /jsp
- Simplified httpd config
- Simplified spring security config
- Fixed login/logout messages
- Fixed authentication of logged in user on servlets not managed by spring
- Moved all static pages to load from httpd
- Fixed unexpected cordova error on server manager groot pages
- Updated kamehouse-shell web interface through groot
- Added splashscreen and role validation to pages in the ui
- Moved groot login to php mysql and added role validation to groot pages and apis
- Added banners to about and contact us pages
- Added loading data from backend message
- Updated info log entry color
- Moved groot module to groot session and improved server manager load page to load before the groot session is complete
- Updated mobile settings tab styles
- Updated kamehouse modal styles and text
- Updated footer layout and default text
- Updated crud manager to show a message when no data is received from backend
- Made all backgrounds and styles darker
- Removed newsletter from most pages
- Loaded sticky back to top on all pages
- Updated debug mode layout
- Reordered kamehouse main menu
- Updated groot server manager tabs to use full witdh
- Updated groot menu to use more width for icons on desktop
- Added margin to module status table
- Added colors to debug mode console
- Updated debugger console output styles
- Moved toggle debugger to header
- Loaded kamehouse modal on all pages
- Added config object to http client framework to allow configuring timeouts and sending basic auth on mobile
- Made all servers read only in settings tab except custom server
- Removed booking requests view
- Split banners css into multiple files
- Added bash colors to html in kamehouse.js to fix output in server management page
- Fixed crud filter bar buttons layout in mobile
- Reduced mobile config file size
- Updated groot banner text with session details
- Fixed showing test websocket connected when connection fails
- Loaded kamehouse debugger on all pages
- Moved text away from banners to main body
- Removed long texts from banners
- Moved kamehouse-shell.js to execute through debugger
- Updated initial banners
- Hid one off booking response until the booking executes
- Changed tail log button
- Updated ui readme with icon colors
- Improved waitForModule calls to wait only for required modules on page load
- Allowed crud manager to replace default banner in the config
- Updated default banners for several pages
- Added colors to debug mode log entries
- Added world cup 2022 (ARGENTINA CAMPEON!!) banners
- Updated homepage banner animation
- Delayed preloading banner images to improve initial page load speed
- Updated awk tail log and js debug console to log errors with red
- Added page to preview all banners in test module
- Deleted deprecated wake on lan page
- Added timeout to session calls on mobile
- Fixed error message on timeout on mobile
- Reduced logging of waitForModules to log every 3 seconds
- Added timeout to backend connectivity test
- Added option to reverse the received data order to crud manager
- Improved initial load time of booking responses by fixing initial sorting of data
- Added sorting data modal while data is being sorted
- Improved performance of data rendering function on crud manager
- Updated mobile settings page layout
- Updated collapsible divs unicode expand/collapse icons
- Masked sensitive data from js logs and debugger
- Refactored deploy to dev tomcat script
- Added git commit hash to mobile app version
- Refactored logic to keep vlc loops synchronized
- Updated vlc playlist browser dropdowns styles
- Added support to filter rows on read all on crud with max rows, a sort column and sort order
- Updated mobile config file to store credentials per backend server in the dropdown
- Added js encryption support with CryptoJS to encrypt the mobile app config file
- Fixed previous responses in debugger to log the response body in one line
- Updated crud manager to allow customizing building the entity, form fields and list display for arrays. And applied it to kamehouse roles to make it easier to edit them
- Added cookie to store/restore js log level
- Updated debugger log level dropdown to change level on click
- Restructured css code
- Added image/info ui component
- Added custom ui lists
- Added new boxes components for info and links
- Revamped kamehouse homepage
- Added custom icons support on crud manager ui
- Added info image component on crud manager ui
- Updated downloads page layout and styles
- Revamped about page
- Revamped tennisworld pages
- Updated groot server manager to work with dev tomcat too
- Changed module/status api to permitAll to remove dependency on being logged in to kamehouse on groot
- Revamped groot admin pages
- Revamped all ui pages with new components
- Updated icons styles in all pages
- Updated banner text styles
- Updated footer icons
- Translated most banners to Japanese
- Updated apk version page generator script
- Fixed bugs

#### v8.12

- Fixed issues with logic to restart the vlc player sync loops in the mobile app after returning from background
- Added sticky back to top to vlc player
- Rebuilt GRoot menu 

#### v8.11

- Fixed vlc player concurrency issues that were resetting the player view randomly the first seconds of loading the page on the mobile app
- Added handlers for pause and resume event to restart the sync loops to avoid the callbacks piling up on the background
- Updated scrollbar styles
- Updated mobile app settings styles
- Updated debug mode console styles
- Updated groot menu styles and layout
- Removed .m2 repository kamehouse entries after deployment
- Updated build and deploy mobile scripts to upload the apk to local synced google drive
- Refactored build and deploy scripts to use common functions

#### v8.10

- Added mobile build version in the settings page
- Added attempt to run the mobile http request even when ssl nocheck fails
- Refactored mobile functions
- Fixed bugs

#### v8.09

- Extended debug mode console log entries
- Added support for all groot pages on the native mobile app
- Fixed broken external links on the mobile app
- Fixed old angular app in test module to work in the mobile app

#### v8.08

- Updated mobile app settings page
- Removed the unnecessary configs and tabs deprecated since adding kamehouse ui and groot to the mobile app natively
- Fixed initial websockets sync error in vlc player on mobile app
- Fixed refresh cordova plugins in build and deploy script
- Added button to test backend connectivity and credentials on the mobile settings page
- Added exposing response headers to the http client and debugger callbacks

#### v8.07

- Refactored completely js code to use a single kameHouse object for the entire kamehouse js framework and extend it with plugins and extensions on each page rather than having multiple root level global variables all over the place
- Moved js code in kamehouse-mobile and kamehouse-groot to extensions of kamehouse
- Refactored http client and debugger to a common interface
- Split header and footer to be loaded independently and with data- attributes to skip them
- Updated build and deploy scripts for kamehouse mobile
- Added tomcat dev scripts
- Updated footer layout
- Removed all window.onload overrides
- Fixed bugs

#### v8.06

- Changed mobile page startup land page to kamehouse ui home
- Updated login/logout status on mobile
- Updated mobile index page to start in the config tab

#### v8.05

- Rebuilt native mobile app completely to use most kamehouse ui and groot pages natively instead of creating a custom mobile app
- Most kamehouse-ui functionality works out of the box in the native mobile app. Most groot functionality is disabled as there's no native php support in cordova
- Added configuration to mobile app to select backend server to connect to and store credentials
- Updated log level manager api to change back to info more easily
- Updated dev, build and deployment scripts for mobile app
- Renamed global js object to kameHouse
- Fixed bugs

#### v8.04

- Fixed shaky unit tests and build

#### v8.03

- Added sorted execution of scheduled booking requests
- Updated logging level of scheduled booking executions
- Added switch back to log level INFO on log level updater view
- Fixed check on already successful bookings for scheduled bookings

#### v8.02

- Updated UI to handle new court number field in booking requests/responses and scheduled configs
- Added support to book specific court number in tennis world PerfectGym integration
- Updated booking requests/responses archiving script

#### v8.01

- Added sample kamehouse chrome extension
- Deprecated old ActiveCarrot tennis world integration
- Added new PerfectGym tennis world integration to support booking classes, comps and courts (without sending payment details, only for members with enough credits to book)

#### v8.00

- Restructured application docs
- Updated audio playlists scripts
- Updated server backup scripts
- Added world cup 2022 scripts and apps to get my tickets. ARGENTINA CAMPEON DEL MUNDO!!! :)
- Fixed bugs

#### v7.08

* Updated docker image base to Ubuntu 22.04
* Updated error pages styles. Added background image
* Added restart httpd script
* Updated groot home page links
* Refactored rc-local scripts
* Fixed deploy to all servers script
* Updated mobile module styles
* Added more scripts to tail log groot
* Added generic functionality to parse required command line arguments in shell
* Added option to skip logging command line arguments in shell
* Added option to filter lines with untagged log level on tail log shell and groot
* Added error logging to php apis
* Fixed server management page mobile layout bugs
* Fixed dragonball servlet error responses. Added abstract servlet to handle errors
* Fixed jsp and angular 1 demo crud pages error handling
* Fixed some exception mappings to status codes
* Fixed bugs

#### v7.07

* Added copying git repo to /root on docker init script to update kamehouse-shell for root more easily
* Added some shell scripts to sync video playlists/subtitles
* Added sample basic logging node module
* Refactored shell scripts to simplify parsing arguments and printing help menu and reduce duplication
* Split suspend times dropdowns in admin view into hours and minutes
* Added groot and ui static files to deployment script and removed dependency on symlinks to git repo
* Updated deployment script to deploy build version files for cmd, shell and groot
* Updated deployment script to skip tomcat steps for non tomcat modules
* Added support to filter by log level to groot tail log
* Fixed git commit functions
* Fixed bugs

#### v7.06

* Updated setup guides
* Removed dependency on root user in most of kamehouse-shell and groot functionality
* Removed sudo from all scripts where it can be avoided
* Removed my.scripts private repo from kamehouse-shell install script
* Moved setting up root paths and shell on linux from install kamehouse to a separate optional script
* Added a lot of debugging logs to the kamehouse-shell scripts
* Updated suspend times dropdown in admin view
* Removed most passwordless sudo permissions from kamehouse user
* Moved reboot, shutdown, suspend and httpd control commands to execute through kamehouse-shell on linux
* Added script to get the status of the database to kamehouse-shell
* Split docker volumes per profile on docker run script, so each profile uses separate volumes

#### v7.05

* Updated installation and dev environment setup docs
* Added more setup scripts to automate the setup process
* Fixed build, deployment and integration tests scripts
* Updated docker init script to load services before build
* Setup a dev docker container to develop on the host and remote debug on the container
* Added powershell scripts to switch hyper-v settings between docker and virtualbox
* Refactored most scripts to parse parameters
* Updated kamehouse-shell prompt
* Improved kamehouse-shell startup time moving PATH generation to install script
* Added option to install kamehouse-shell completely or only the scripts to the install script
* Fixed vlc start not working when controlling the host from docker on a windows host
* Fixed bugs

#### v7.04

* Updated kh mobile icons
* Added more suspend time options in the server admin page
* Removed deprecated scripts
* Added script to install kamehouse-shell scripts only
* Updated the installation and environment setup guides and other MDs
* Added install script to automate most of kamehouse installation process
* Added uninstall script to uninstall kamehouse and purge the config files

#### v7.03

* Renamed project from java.web.kamehouse to kamehouse
* Moved away from my private my.scripts repo and made kamehouse-shell installable and the main source of my scripts
* Added more retries for tennisworld scheduled bookings
* Added build version to the footer of kamehouse mobile
* Fixed sorting of data in the UI in crud manager
* Added url parameter to override default js console log level for debugging
* Added git commit hash to build version
* Updated layout and styles of kamehouse-mobile
* Added demo profile to docker scripts
* Added confirmation modal to reset config button
* Fixed dateUtils tests that failed on some linux servers
* Created wake on lan page to link to it from mobile app instead of linking to server-management
* Added option to hide debug mode button but still make it clickable
* Fixed link buttons in tennisworld
* Updated security config of tennisworld api
* Updated audio track cycle icon
* Moved remote playlists paths to use generic media-server-ip name instead of actual media server's hostname
* Moved away from lan-share playlists to use the same http- for all playlists on all os
* Added proxy to media server from docker
* Added robots.txt to apache httpd on docker
* Fixed demo vlc playing inside docker container removing the video
* Updated build version format on footer and added it to kamehouse-mobile
* Moved audio and video playlists to separate repos
* Updated docker image users
* Removed hardcoded paths with user nbrest on all modules
* Fixed banners on widths between full desktop and mobile

#### v7.02

* Added kamehouse-mobile module to build native apps for android and ios using apache cordova
* Added native android app built with cordova in js, html and css
* Added downloads page with a link and qr code to download the apk to install on android phones
* Added script to archive tenniswold booking requests and responses
* Replaced the stop and start tomcat buttons with a restart one in groot
* Added instructions to build the apk for android on the readme.md of kamehouse-mobile
* Added kamehouse-mobile to build and deploy scripts
* Updated set git remotes script
* Updated do release script to include updating version in mobile app
* Updated layout of the footer

#### v7.01

* Added support for all the remaining commands to execute on the host of the container through ssh
* Refactored system command generation
* Fixed commands executed through kamehouse-cmd process hanging on windows (unlock screen for example)
* Added support to control the host from kamehouse groot as well
* Added different profiles to docker scripts for ci, dev, prod
* Moved jenkins to run integration tests in a ci docker container instead of a native tomcat and added retries to the run
* Added option to start tomcat in debug mode from groot
* Fixed bug that didn't allow to disable booking schedule configs from the ui
* Fixed excessive logging in kamehouse-cmd startup
* Added keep alive scripts for docker containers
* Added docker folders to server backup script
* Fixed restart tomcat when port 9005 remains locked after tomcat exits
* Removed logging of some passwords
* Added script to export rsa keys to pkcs8 to connect through ssh from kamehouse apps
* Fixed coloring and formatting of some log entries
* Added parameterized tests on DateUtils
* Refactored a lot the docker scripts to allow more parameters to modify the default behaviour
* Fixed unit tests
* Fixed other bugs

#### v7.00

* Added core functionality to execute commands in the host running the docker container through ssh (allowing both to execute the commands inside the container or on the host)
* Added support for remote debugging of tomcat in the container
* Updated tomcat dev deployment script to support deploying to docker container
* Initially added functionality to start and stop vlc player outside the container through ssh
* Added functionality to run the container executing commands in the host to docker run script
* Updated docker startup script to pass environment variables to the container to configure startup mode (self contained, interacting with the host and other options)

#### v6.11

* Refactored docker file to reduce considerably the size of the kamehouse docker image
* Added support for persistance of data in volumes to the docker container setup
* Added docker scripts to backup mysql docker data to the host
* Added docker scripts to re-init docker container data from the host's filesystem and db
* Fixed ssh between host and container to enable kamehouse to run in the container interacting with the host
* Allowed external access to mysqldb from outside the container
* Updated docker run script to add options to execute the container with or without persistance
* Set timezone in the container

#### v6.10

* Forked [jvncsender](https://github.com/nbrest/jvncsender) and added it to the local maven repo of kamehouse
* Added [jvncsender](https://github.com/nbrest/jvncsender) to kamehouse-cmd and replaced most calls to vncdo with kamehouse-cmd. For mouse clicks, vncdo tool is still needed
* Updated the demo docker image generation to include the build of kamehouse, init the database and directories setup while building the image instead of in the container startup script
* Added parameters to skip pulling the latest version of kamehouse and skip deployment during container init script
* Allowed access to tomcat manager outside docker

#### v6.09

* Added docker to the project
* Added a Dockerfile to the root of the project and scripts to create a self contained image to run kamehouse and kamehouse groot in a container
* Added scripts to build kamehouse image and container
* Added scripts to start and stop the container

#### v6.08

* Added integration to sonarcloud straight from git
* Fixed issues reported by sonarcloud

#### v6.07

* Fixed broken integration tests on linux
* Added integration tests to websockets
* Added ci maven profile for continuous integration and setup jenkins build to run all integration tests as well as unit tests

#### v6.06

* Updated jquery to it's latest version
* Fixed websockets for kame.com and going through vm-ubuntu.com
* Added fallback synchronization of vlc player through http calls when the websockets are disconnected
* Added a @Masked annotation to mask hidden fields on toString() calls
* Added integration tests to all controllers replace my current postman tests
* Added integration tests to kamehouse-cmd 
* Refactored unit tests for cruds in controller, service and dao layers, moving most functionality to the abstract classes
* Refactored crud controllers, services and daos moving more functionality to the abstract classes
* Moved vlc player crud to a separate controller

#### v6.05

* **Upgraded project to java 11 and tomcat 9 and latest maven version**
* Upgraded to latest spring, hibernate and junit versions
* Upgraded to latest versions of all other dependencies
* Replaced findbugs with spotbugs

#### v6.04

* Added ui to execute one off bookings to tennisworld
* Fix bug api call errors with html response messing up the view
* Improved logging in the ui
* Added auto hide groot submenu on desktop
* Added search bar to crud manager
* Added dropdown to limit the number of displayed rows to the crud manager
* Added sorting of table by any column to the crud manager
* Added read only functionality to crud manager
* Added ui to trigger scheduled booking configurations execution to tennisworld
* Added persistence for booking requests and responses
* Added ui to view all booking requests and responses
* Added numeric sorting support to table sorting function
* Disabled tabs on crud manager for read only mode
* Added initial sorting support to crud manager
* Added refresh data to list view in crud manager
* Added custom filters to booking response ui
* Added wol media server to debug mode on vlc player
* Fixed bugs

#### v6.03

* Added tennisworld user entity storing the password encrypted
* Added tennisworld booking schedule config entity
* Moved getting the tennisworld user and password from the db instead of encrypted files in the filesystem
* Changed thread names to the tenniswold bookings
* Moved cardio booking schedule from hardcoded in the code to the database
* Added ui to manage scheduled bookings
* Added functionality to configure scheduled bookings for any kind of session, not just cardio, for one off and recurring bookings and with flexible time to book ahead
* Added endpoint to trigger scheduled bookings
* Cleaned up a bit more the js code using more const and from the issues reported in codacy and codefactor
* Added generic crud for all entities in the frontend
* Added unlock screen button in vlc view instead of link to server management
* Updated default table styles
* Fixed bugs

#### v6.02

* Added custom form styles and applied it to all current forms
* Added custom table styles and applied it to all current tables
* Updated page layout for test module websockets and db users forms
* Fixed showing blank pages on some 404 not found when served from tomcat
* Simplified web.xml for ui module
* Fixed showing the correct 404 status code when the page is not found in include-static-html
* Moved tabs in groot to generic kamehouse tabs to reuse them in other pages (currently used in groot server manager)
* Updated the styles and layout of the about page and the home page
* Created domUtils and moved all dom manipulation to go through there
* Added fetchUtils to pull html snippets and js from there
* Huge refactor of the frontend code with all the changes done above. Also removed all `self.` references and updated function definitions to make it more readable. Moved let to const when possible
* Moved logger and httpClient into kamehouse.js and removed them as loadable modules
* Updated layout for groot home page

#### v6.01

* Added initial load of the status of vlc player and the playlist through API calls as soon as the page loads and when loading a new file or playlist from the playlist browser. This avoids the delay of waiting until the websockets synchronize to get the initial/updated view
* Updated theme of vlc player a lot
* Updated themes in most pages. Made most themes darker
* Updated groot login styles
* Replaced remaining bootstrap buttons with own styles and image icons
* Swiched opening php-proxy and other external links in new tabs from groot
* Added security to php-proxy
* Updated angular validations styles
* Added config to make php sessions last longer
* Updated system state buttons layout in server management page
* Updated angular and jsp tables styles
* Added generic styles for tables and image buttons and reused them in all pages
* Added debug mode to jsp pages
* Fixed bugs

#### v6.00

* Added my.scripts as a new maven module (shell) to export them as a deployable zip file
* Updated login page styles
* Replaced login and logout button
* Updated groot index page links styles
* Updated styles in about and contact us pages and newsletter snippet
* Replaced .htaccess authentication in groot with a custom authentication endpoint built with php and a shell script, still relying on .htpasswd file to authenticate users
* Created a custom login page for groot
* Added a sub header in groot with the currently logged in user
* Updated the authentication of all pages and APIs in groot to use the new auth endpoints
* Imported more shell scripts

#### v5.00

* Added a java built command line tool to kamehouse
* Added encrypt and decrypt operations using kamehouse keys to kamehouse-cmd to encrypt/decrypt files
* Added verbose mode to kamehouse-cmd enabled through -v
* Renamed tab from tomcat to deployment in groot server manager
* Added module status and deployment of non tomcat modules: cmd, groot and my.scripts

#### v4.00

* Added many of my shell/sql scripts to the kamehouse repo
* Added scroll to top and bottom in both tail logs and command output in groot server manager
* Added sticky back to top button as a loadable js snippet and into the server manager pages
* Removed deprecated tail-log page and frequent scripts from groot/my-scripts
* Moved a lot of the configuration away from web.xml into java config in KameHouseWebAppInitializer
* Added request logger filter to log all incoming requests
* Added endpoints and UI to configure the request logger to enable/disable logging the payload, headers, client info and query string
* Removed all the manual logging of requests from the controllers
* Updated styles for the groot server manager page
* Updated styles for landing pages with links
* Added id to the tennisworld booking requests
* Added thread utils to edit the current thread name
* Updated thread name of the tennisworld booking for easier tracing of the request flow

#### v3.00

* Added KameHouse GRoot module to the main repository
* Added backend functionality to book cardio tennis sessions on tennisworld
* Added scheduled job to do automatic bookings of cardio sessions to tenniswold
* Updated vlc player styles and main player layout
* Added dropdown with number of lines to tail in tail logs tab
* Added encryption utils and moved base64 encoded files to encrypted files
* Added cookies handler and loading the current tab from cookies on vlc player
* Added loading tab from cookies for all pages with tabs

#### v2.01

* Added collapsible console log div to kamehouse debugger to check the console logs from mobile
* Added log level selector to debug mode
* Moved debug mode to a reusable snippet
* Added debug mode to ehcache page
* Added collapsible list of previous commands to debug mode
* Refactored api call table and other large js generated html into loadable snippets
* Cleaned up html generation in js code
* Merged api call table into debug mode
* Added scripts to deploy a single module both locally and to all servers and to undeploy modules (my.scripts private repo)
* Added an admin view of all kamehouse tomcat modules and it's running versions
* Changed Root menu to GRoot
* Created a full blown tomcat manager replacement page in the kh.webserver private repo (to be merged into this repo in next release). This GRoot application also includes functionality to pull from all my git repos, update my media server playlists, tail logs and restart the server
* Added system state commands to the server manager page
* Added command to reboot the server
* Added a command to restart apache httpd from the admin's server manager page
* Added try/catch to catch previously uncaught exceptions on vlc player page
* Added playlists to music videos and anime best + conan
* Fixed bugs

#### v2.00

* **Restructured the whole project into separate modules. The project structure is completely
 different from the previous versions**
* Created modules admin, commons, commons-core, commons-test, media, tennisworld, testmodule, ui
,vlcrc
* Added spring session to store sessions in the db to share the session between all modules
* Refactored jsps and servlets for test module
* Moved a lot of the spring configuration beans from xml to annotations. Simplified a lot the xml
 configurations
* Added build version and date to footer
* Renamed ApplicationUser to KameHouseUser and renamed roles
* Renamed AdminCommand to a more generic KameHouseSystemCommand reused in other modules outside
 admin
* Moved session status API to UI module
* Moved vlc open and close system commands from admin to vlcrc module
* Fixed the UI for log level, ehcache and scheduler to manage all the backend modules
* Fixed bugs

#### v1.07

* Reorganized the project into modules, with a parent pom and initial kamehouse-webapp module. To
 eventually start splitting the code into multiple modules/services
* Removed logging of input data in the controllers
* Fixed playlist category dropdown grouping anime and cartoons
* Added some try/catch to kamehouse websocket to handle errors better

#### v1.06

* Updated styles for menu pages with links to other pages
* Improved Ehcache UI layout and styles
* Added several new banners
* Added UI to control log levels in the backend
* Added preloading of banners, so transitions between them are smoother on slower connections
* Allowed to skip logging respones in abstract crud controller
* Improved server management UI layout and styles
* Added status of scheduled shutdown and suspend commands in the server management ui
* Improved styles for api call table in debug mode
* Removed deprecated /admin/test-apis page
* Renamed some index js and css files
* Moved shutdown command to the scheduler framework
* Added a scheduler for hibernate command
* Added script to deploy in all servers (my.scripts repo)
* Added more scripts to kh.webserver repo to do deployment and admin tasks
* Fixed time and volume sliders on VLC player. Disabled updating of sliders while they are being selected/moved
* Fixed playlist and playlist browser search not being triggered after switching tabs or playlist updates
* Added scheduler framework using quartz to schedule jobs
* Added a sample job to schedule in the backend and an UI in test-module to schedule it, cancel the schedule and view the schedule status
* Added an UI to view all jobs in the application with their schedules, and the functionality to cancel the scheduled jobs through the UI
* Added generic response body to exception handler in the controllers
* Fixed bugs

#### v1.05

* Added backend functionality to wake on lan other servers
* Added frontend to WOL media server (from kame-house running on another server in the same network)
* Fixed bugs
* Setup continuous integration with jenkins. https://jenkins.nicobrest.com
* Fixed `skill -9 vlc` not working in raspberry pi
* Update VLC main layout buttons
* Added subtitle sync buttons
* Refactored more js code on server-management page
* Added server name to footer
* Added root (/) functionality to do admin commands that don't rely on tomcat (kh.webserver js/php repo)
* Changed modals so they can only be closed clicking the x button
* Updated login page with custom icons and style
* Moved login page to static html. Only jsps now are in /test-module/jsp
* Updated banners in all pages
* Added banner randomizer utils
* Added animations to transition between banner images

#### v1.04

* Added backend to execute bookings (of facility overlay type) to tennis world australia with a single API call to kame-house. The current manual process using tennis world's mobile UI is 11 API calls to tennis world
* Improved playlist comparison algorithm to handle better larger playlists in VLC UI
* Improved expand/collapse playlist filenames performance for larger playlists in VLC UI
* Improved logic to highlight currently playing element in the playlist in VLC UI
* Updated vlc player tab manager to look more responsive with large playlists by loading playlist content asynchronously when switching tabs
* Deprecated isEmpty() in the frontend and improved overall performance with isNullOrUndefined()
* Set a media server that contains all the media files and allowed to stream files and playlists from the media server in the computer running kame-house
* Replaced cobertura with jacoco for code coverage checks
* Added more words in Japanese to kame-house frontend :)
* Added backend functionality for dynamically changing the logging level for each package. Updated log level definitions for each package. Disabled extreme logging of vlc player status by default
* Moved remote playlists from smb:// protocol to lan share (\\server) on windows so it can load subtitles, and http:// on linux as linux doesn't support either smb:// or lan share. Still can't load subtitles from http:// streams on though
* Fixed issue of playlist resyncing when it should be the same
* Added spinning wheel when going to admin view

#### v1.03

* Updated icons in vlc player
* Added spinning wheel when executing commands that take several seconds
* Loading a playlist opens the playlist tab, loading a file opens the now playing tab
* Updated icons for admin controls in vlc player
* Added tooltips on hover
* Added personalized message on loading wheel modal
* Updated styles for main player
* Added more space between buttons on mobile to avoid accidental clicks
* Added aspect ratio dropdown
* Added expand/collapse filename functionality for playlist and playlist browser
* Updated playlist to use filename instead of display name
* Removed bootstrap styles for playlist buttons and added custom styles
* Added separate styles for playlist browser list
* Improved regex for filtering the playlist
* Added close functionality to the loading wheel modal
* Added an error modal when the file I try to load doesn't exist
* Updated vlc tabs to use icons
* Unified kamehouse modals and grouped common functionality
* Added cursor spinner for main vlc player commands
* Added a custom spinner on mobile for main vlc player commands
* Removed brightness reset on hover for vlc buttons
* Fixed bug of randomly not executing vlc commands on tap on mobile
* Fixed bug of randomly not loading the playlist I select
* Fixed bug of playlist resyncing several times until it stabilizes
* Fixed not serializable exception from ehcache
* Added more automated tests in postman
* Fixed ehcache api breaking with videoPlaylists
* Fixed charset encoding when loading jsps (to utf-8)
* Updated server management buttons with icons instead of text
* Fixed x close position and styles on functionality not implemented modal
* Updated deployment script to default to local environment (my.scripts repo)
* Updated footer message
* Added auto closeable modal
* Huge refactor of all js code. Started using more lambda expressions where possible
* Reorganized global functions into several utils prototypes
* Simplified significantly js assigning functions to variables instead of redefining functions
* Updated prod apache http config to pull static resources from git repo instead of going to tomcat

#### v1.02

* Added more unit tests
* Automated API tests with postman
* Automted backup of server configs (my.scripts repo)
* Moved table names to lowercase to export data between windows and linux
* Fixed database export/restore scripts (my.scripts repo)
* Moved playlist synchronization to a websocket and improved synchronization stability
* Split vlc player UI into tabs. Initially for main player, playlist and playlist browser
* Added functionality to filter playlist elements, scroll to currently playing and back to top
* Added functionality to load my video playlist contents, browse them and play an individual file from the playlist browser
* Restructured readme.md into separate files
* Made api call output and ehcache tables more responsive
* Fixed bug of playlist not loading randomly
* Fixed bugs

#### v1.01

* Updated logging strategy in the backend. Added a lot more logging in all layers.
* Pretty much complete refactor of the frontend js code. Started splitting into prototypes (Logger, HttpClient, TimeUtils, etc) and moving away from global functions and variables
* Updated logging in the frontend. Created a Logger prototype with standard log methods and methods to trace/debug function calls.
* Reduced a lot of code duplication in the frontend with the refactor
* Complete refactor of vlc player js code too. Improved synchronization with backend
* Removed deprecated csrf meta tags and headers
* Updated api-call-table with request time and response code
* Updated tail-log.sh to add colors and filtering based on log level (my.scripts repo)
* Fixed remote deployment script (my.scripts repo)
* Fixed bugs

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
* Updated Spring version to 4.3.25 and Spring security to 4.2.13 and other dependencies

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
