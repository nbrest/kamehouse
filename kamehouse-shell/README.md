| [Home](/README.md) | [Docs](/docs/README.md) |
---------------------------------------------------------------

*********************

# Shell Module:

This module contains most of my shell scripts to automate some tasks setting up, building, deploying and debugging kamehouse.

## Install

- The script `./scripts/install-kamehouse.sh` also installs kamehouse-shell
- Run with `-s` to install kamehouse-shell only
- Run with `-o` to install kamehouse-shell scripts only, without modifying the shell

- Update the `kamehouse-shell` values in `${HOME}/.kamehouse/kamehouse.cfg` to match your local network setup for the shell scripts

## Linux:

- Add user running kamehouse to adm group to be able to tail apache2 logs
```sh
sudo usermod -a -G adm username
```

## Keep alive scripts:

### Windows:

- Run `copy-b-bat.sh` to copy `b.bat` to the home directory. Then from cmd.exe just type b enter to start git-bash

- Use windows task scheduler to schedule every X minutes the keep-alive-\*.bat that calls the keep-alive-\*.sh 
