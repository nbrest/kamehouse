# Shell Module:

This module contains most of my shell scripts to automate some tasks setting up, building, deploying and debugging kamehouse.

## Install

- The script `./scripts/install-kamehouse.sh` also installs kamehouse-shell
- Run with `-s` to install kamehouse-shell only
- Run with `-o` to install kamehouse-shell scripts only, without modifying the shell

## Linux:

- Add user running kamehouse to adm group to be able to tail apache2 logs
```sh
usermod -a -G adm username
```