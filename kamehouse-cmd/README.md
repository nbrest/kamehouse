# Cmd Module:

* kamehouse-cmd is a command line tool written in java to do admin tasks executable through the
 command line rather than being triggered from a web app.

| folder | description |
| ---- | --------|
| bin | Contains the script file to execute the application |
| lib | Contains all the jars, including the main jar of the application |

### Installation:

- Build from the working directory and install using the script `build-java-web-kamehouse.sh -m cmd
`. This command builds
 kamehouse-cmd module and installs it to `${HOME}/programs/kamehouse-cmd`. Then to execute the
  command line tool, run `${HOME}/programs/kamehouse-cmd/bin/kamehouse-cmd.sh`

- The script `deploy-java-web-kamehouse.sh -m cmd` builds the module from `${HOME}/git/java.web.kamehouse` and also installs it to `${HOME}/programs/kamehouse-cmd`

- In either case the `kamehouse-cmd-bundle.zip` remains in the `kamehouse-cmd/target` directory and
 can be extracted to any path where you want to install the command line tool
 