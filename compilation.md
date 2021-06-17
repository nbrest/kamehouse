# Compilation:

* Compile using `mvn clean install [compilation option]` in the root to build all the modules.

| Option | Usage | Description | 
| ------------------ | ----- | ----------- |
| P | -P:prod | Uses mysql db. **Default**  |
|   | -P:qa | Uses oracle db |
|   | -P:dev | Uses hsql in memory db |

## View code coverage reports in my local:

* For **kamehouse-admin** module: open `kamehouse-admin/target/site/jacoco/index.html` in a
 browser
 after building the module
 * Same for all the other modules
 