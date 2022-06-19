# Installation:

* The recommended and easiest way is to install is through docker as explained in [docker-setup.md](docker-setup.md)

## To install natively without docker:

* Install java jdk 11

* Install maven (minimum version 3)

* Install git (and git bash on windows)

* Install mysql server version 8
  - Execute the sql scripts:
    - [setup-kamehouse.sql](kamehouse-shell/bin/kamehouse/sql/mysql/setup-kamehouse.sql)
    - [spring-session.sql](kamehouse-shell/bin/kamehouse/sql/mysql/spring-session.sql)
    - [dump-kamehouse.sql](docker/mysql/dump-kamehouse.sql) (optional to setup initial users mentioned in [docker-setup.md](docker-setup.md))

* Install tomcat following [installation-tomcat.md](installation-tomcat.md)

* Install apache following [installation-apache.md](installation-apache.md)

* Once the above setup is complete, download and run the script [install-kamehouse.sh](scripts/install-kamehouse.sh) from this git repo, which will pull kamehouse from git into `${HOME}/git/kamehouse` and run the deployment script that will build and deploy all kamehouse modules
  - Once downloaded, run the script on bash with the command `chmod a+x install-kamehouse.sh ; ./install-kamehouse.sh`

* Then start both tomcat and apache to access kamehouse at http://localhost/kame-house or https://localhost/kame-house

* From a new bash terminal access KameHouse CMD module through `kamehouse-cmd.sh` and all other kamehouse-shell scripts

* All kamehouse-shell scripts should be in the path if the install script correctly updated `${HOME}/.barhrc` file to source `${HOME}/programs/kamehouse-shell/bin/common/bashrc/bashrc.sh`

**WARNING**
The script [install-kamehouse.sh](scripts/install-kamehouse.sh) will update your bash terminal settings. To revert your terminal unsource `${HOME}/programs/kamehouse-shell/bin/common/bashrc/bashrc.sh` from `${HOME}/.barhrc` and access the kamehouse-shell scripts from their full path or run the [uninstall-kamehouse.sh](scripts/uninstall-kamehouse.sh) script

* Follow the [Execution](execution.md) guide to run kamehouse

## Uninstall

- Download and run the [uninstall-kamehouse.sh](scripts/uninstall-kamehouse.sh) to uninstall kamehouse from your system
```sh
chmod a+x uninstall-kamehouse.sh ; ./uninstall-kamehouse.sh
```
- Run with -p to remove all configuration files as well
