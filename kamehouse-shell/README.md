# Shell Module:

This module contains most of my shell scripts to automate some tasks setting up, building, deploying and debugging kamehouse.

To install copy `my.scripts` folder to `${HOME}/my.scripts` and source `(win|lin)/bashrc/bashrc.sh` from `${HOME}/.bashrc` and then most scripts should work out of the box

Building the project with `build-java-web-kamehouse.sh -m shell` creates a zip file with everything in `kamehouse-shell/target/kamehouse-shell-bundle.zip`. 
This zip can be extracted to the users home directory.