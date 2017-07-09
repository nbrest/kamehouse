******
# Notes:

This is a base project to use for **java WEB Applications** that run inside an application server or as a stand-alone spring boot application.

The application consists of a **REST** web service with test endpoints and a frontend to manipulate the data using the latest **javascript** frameworks and the same functionality implemented using **JSPs**. The data is stored in a relational database. The database engine depends on the **maven profile** selected. **Hsqldb** for dev, **Oracle** for qa and **Mysql** for prod to test the application using different database engines. 

The project uses **Maven** as a **SCM**. It is configured to validate the test coverage with **cobertura**, validate code with **findbugs** and the style with **checkstyle**.

##### Java frameworks/libraries:
* Spring
* Hibernate
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
