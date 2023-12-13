| [Home](/README.md) | [Docs](/docs/README.md) |
---------------------------------------------------------------

*********************

# Cmd Module:

* kamehouse-cmd is a command line tool written in java to do admin tasks executable through the
 command line rather than being triggered from a web app.

* Uses [jvncsender](https://github.com/nbrest/jvncsender) to send text to a vnc server replacing the external vncdo tool. For sending mouse clicks, vncdo is still needed

* For a list of all operations to execute with kamehouse cmd, run `kamehouse-cmd.sh -h`

| folder | description |
| ---- | --------|
| bin | Contains the script file to execute the application |
| lib | Contains all the jars, including the main jar of the application |

### Installation:

- Build from the working directory and install using the script `build-kamehouse.sh -m cmd`. This command builds kamehouse-cmd module and installs it to `${HOME}/programs/kamehouse-cmd`. Then to execute the command line tool, run `${HOME}/programs/kamehouse-cmd/bin/kamehouse-cmd.sh`

- The script `deploy-kamehouse.sh -m cmd` builds the module from `${HOME}/git/kamehouse` and also installs it to `${HOME}/programs/kamehouse-cmd`

- In either case the `kamehouse-cmd-bundle.zip` remains in the `kamehouse-cmd/target` directory and can be extracted to any path where you want to install the command line tool
 
### Upgrade jvncsender version:

- After installing the latest jvncsender version in my local `${HOME}/.m2` repository (check jvncsender readme to install), in the root of kamehouse project run:
- Delete the older version from the kamehouse local-maven-repo
```sh
export RELEASE_VERSION=X.XX
mvn deploy:deploy-file -DgroupId=be.jedi -DartifactId=jvncsender -Dversion=${RELEASE_VERSION} -Durl=file:./local-maven-repo/ -DrepositoryId=local-maven-repo -DupdateReleaseInfo=true -Dfile=${HOME}/.m2/repository/be/jedi/jvncsender/${RELEASE_VERSION}-SNAPSHOT/jvncsender-${RELEASE_VERSION}-SNAPSHOT-jar-with-dependencies.jar
```
- Update jvncsender version in kamehouse parent pom.xml
- Update local-maven-repo/be/jedi/jvncsender/${RELEASE_VERSION}/jvncsender-${RELEASE_VERSION}.pom to only have the content:
```xml
<?xml version="1.0"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>

  <groupId>be.jedi</groupId>
  <artifactId>jvncsender</artifactId>
  <version>${RELEASE_VERSION}</version>
  <name>jvncsender</name>
</project>
```
- Then commit the changes
