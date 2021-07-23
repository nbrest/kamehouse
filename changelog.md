# ChangeLog:

#### v4.00

* Added some of my bash scripts to the kamehouse repo
* Added scroll to top and bottom in both tail logs and command output in groot server manager
* Added sticky back to top button as a loadable js snippet and into the server manager pages
* Removed deprecated tail-log page and frequent scripts from groot/my-scripts
* Moved a lot of the configuration away from web.xml into java config in KameHouseWebAppInitializer
* Added request logger filter
* Added endpoints and UI to configure the request logger to enable/disable logging the payload, headers, client info and query string
* Removed all the manual logging of requests from the controllers

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
