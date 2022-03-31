# Shell Module:

This module contains most of my shell scripts to automate some tasks setting up, building, deploying and debugging kamehouse.

Install by executing `kamehouse-shell-install.sh` from the root of the kamehouse git project
```sh
chmod a+x ./kamehouse-shell/bin/kamehouse/kamehouse-shell-install.sh
./kamehouse-shell/bin/kamehouse/kamehouse-shell-install.sh
```

- The script `./scripts/install-kamehouse.sh` also installs kamehouse-shell

To install manually copy `bin` folder to `${HOME}/programs/kamehouse-shell/bin` and source `${HOME}/programs/kamehouse-shell/bin/(win|lin)/bashrc/bashrc.sh` from `${HOME}/.bashrc` and then most scripts should work out of the box

Building the project with `build-kamehouse.sh -m shell` creates a zip file with everything in `kamehouse-shell/target/kamehouse-shell-bundle.zip`. 
This zip can be extracted to the users home directory.

## Windows

- In windows besides the install script, add `%USERPROFILE%\programs\kamehouse-shell\bin\win\bat` to the PATH environment variable