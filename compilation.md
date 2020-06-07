# Compilation:

* Compile using `mvn clean install [compilation option]` .

| Option | Usage | Description | 
| ------------------ | ----- | ----------- |
| P | -P:prod | Uses mysql db. **Default**  |
| P | -P:qa | Uses oracle |
| P | -P:dev | Uses hsql in memory db |

## View code coverage reports in my local:

* Compile using `mvn cobertura:cobertura` and open *target/site/cobertura/index.html*