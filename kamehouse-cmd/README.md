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

### Create encrypted vnc password file

- In order to execute vnc commands, kamehouse uses an encrypted file that contains the password for the vnc user. Generate that file with the following commands:
```sh
# generate encrypted password file
echo -n "your-password" > ${HOME}/.kamehouse/.vnc.server.pwd
kamehouse-cmd.sh -o encrypt -if ${HOME}/.kamehouse/.vnc.server.pwd -of ${HOME}/.kamehouse/keys/.vnc.server.pwd.enc
rm -fv {HOME}/.kamehouse/.vnc.server.pwd
# check the password is stored correctly
kamehouse-cmd.sh -o decrypt -if ${HOME}/.kamehouse/keys/.vnc.server.pwd.enc -of stdout
```

### Create encrypted unlock screen password file

- In order to execute unlock the user's screen, kamehouse decrypts the encrypted user's password from the file `.kamehouse/keys/.unlock.screen.pwd.enc`. To generate the file run the commands with your current windows/linux user password:
```sh
# generate encrypted password file
echo -n "your-password" > ${HOME}/.unlock.screen.pwd
kamehouse-cmd.sh -o encrypt -if ${HOME}/.kamehouse/.unlock.screen.pwd -of ${HOME}/.kamehouse/keys/.unlock.screen.pwd.enc
rm -fv {HOME}/.kamehouse/.unlock.screen.pwd
# check the password is stored correctly
kamehouse-cmd.sh -o decrypt -if ${HOME}/.kamehouse/keys/.unlock.screen.pwd.enc -of stdout
```

### Upgrade jvncsender version:

- After installing the latest jvncsender version in my local `${HOME}/.m2` repository (check jvncsender readme to install), in the root of kamehouse project run:
```sh
export RELEASE_VERSION=X.XX
./scripts/update-jvncsender-version.sh -v ${RELEASE_VERSION}
```
- Update jvncsender version in kamehouse parent pom.xml

- Then commit the changes
