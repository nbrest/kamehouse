# Compilation:

* Compile using `mvn clean install [compilation option]` .

| Option | Usage | Description | 
| ------------------ | ----- | ----------- |
| P | -P:prod | Uses mysql db. **Default**  |
|   | -P:qa | Uses oracle db |
|   | -P:dev | Uses hsql in memory db |

## View code coverage reports in my local:

* For **kamehouse-webapp** module: open `kamehouse-webapp/target/site/jacoco/index.html` in a
 browser
 after building the module