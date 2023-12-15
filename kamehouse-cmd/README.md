| [Home](/README.md) | [Docs](/docs/README.md) |
---------------------------------------------------------------

*********************

# Cmd Module:

* kamehouse-cmd is a command line tool written in java to do admin tasks executable through the
 command line rather than being triggered from a web app.

* Uses [jvncsender](https://github.com/nbrest/jvncsender) to send text and mouse clikcs to a vnc server

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
```sh
export RELEASE_VERSION=X.XX
./scripts/update-jvncsender-version.sh -v ${RELEASE_VERSION}
```
- Update jvncsender version in kamehouse parent pom.xml

- Then commit the changes
