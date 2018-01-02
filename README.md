# Description:

This application (still heavily under construction) will contain the following modules: 
* Manage my media files
* Control multiple VLC Players through their web API
* Test endpoints to practice different frontend frameworks
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
* Vue
* React
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
# ChangeLog:
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