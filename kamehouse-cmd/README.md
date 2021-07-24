******
# Notes:

This is a base project to use for **stand alone JAVA** applications with **maven profiles**, **spring** and **hibernate** that don't run inside an application server. It generates a zip file with the jars, configuration files and execution scripts that can installed and executed from any location.

*********************
# Execution in eclipse:

- To run from eclipse, create a run configuration and in the arguments tab, update the working directory to projectBaseDir/target instead of the default projectBaseDir. This is because the external properties files under the folder conf/ don't exist in the projectBaseDir, but are copied with the maven build to the target directory.

************
# Compilation:

- Do **NOT** compile using `mvn clean package`. Use the script **scripts/build.sh** to compile. It updates the application name, version and log file configuration from the pom.xml and generates a zip bundle with the jars, external configuration files and execution scripts with the structure:

| folder | description |
| ---- | --------|
| bin | Contains the script file to execute the application |
| conf | Contains the properties files |
| lib | Contains all the jars, including the jar of the main application |
| logs | Logging directory |

- Use compilation scripts:
  - **build.sh** - Sets up the bin script and log file name and builds the zip file that can be installed to any directory
  - **build-install.sh** - Builds the zip file and extracts it into target/
  - **build-install-run.sh** - Builds the zip file with the provided parameters, extracts it into target/ and runs the application without parameters.
  - **build-install-run-params.sh** - Builds the zip file with the provided compiler parameters, extracts it into target/ and runs the application with the provided run parameters.
*************
# Installation:

- Extract the zip file into the desired directory and run the script in the bin/ directory with the required parameters to execute the application.

