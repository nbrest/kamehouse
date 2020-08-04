# ChangeLog:

#### v1.04

* Added backend to execute bookings (of facility overlay type) to tennis world australia with a single API call. The current manual process using tennis world's mobile UI is 11 API calls.e
* Improved playlist comparison algorithm to handle better larger playlists
* Improved expand/collapse playlist filenames performance for larger playlists
* Improved logic to highlight currently playing element in the playlist
* Improved overall vlc player performance by removing isEmpty() usage
* Set a media server that contains all the media files and allowed to stream files and playlists from the media server in the computer running kame-house
* Replaced cobertura with jacoco for code coverage checks
* Added more words in Japanese to kame-house frontend :)
* Added backend functionality for dynamically changing the logging level for each package. Updated log level definitions for each package

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
