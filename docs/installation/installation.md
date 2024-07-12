| [Home](/README.md) | [Docs](/docs/README.md) |
---------------------------------------------------------------

*********************

# Installation:

* The recommended and easiest way is to install is through docker as explained in [docker-setup.md](/docs/docker/docker-setup.md)

## To install natively without docker:

* Install java JDK [(versions)](/docs/versions/versions.md)

* Install maven [(versions)](/docs/versions/versions.md)

* Install git (and git bash on windows)

* Install node [(versions)](/docs/versions/versions.md) 
```sh
# Update dockerfile when updating node version here
cd ~
curl -sL https://deb.nodesource.com/setup_20.x | sudo bash -
sudo apt-get -y install nodejs
sudo npm install typescript -g
# to avoid jquery typescript compiler errors
sudo npm i --save-dev @types/jquery
```

* Install mariadb server [(versions)](/docs/versions/versions.md) and set a password for user root in windows

  - Update server configuration:
    - Windows:
      - Check if they are not already set in my.ini
      ```
      lower_case_table_names=1
      secure-file-priv=""
      ```
    - linux:
      - The config files are in /etc/mysql
      - The file to update depends on the mariadb version you are using. It could be mariadb.conf, 50-server.conf
      ```
      default-time-zone='+10:00'
      secure-file-priv=""
      ```

* Install tomcat following [installation-tomcat.md](/docs/installation/installation-tomcat.md)

* Install apache following [installation-apache.md](/docs/installation/installation-apache.md)

* Once the above setup is complete, download and run the script [install-kamehouse.sh](/scripts/install-kamehouse.sh) from this git repo, which will pull kamehouse from git into `${HOME}/git/kamehouse` and run the deployment script that will build and deploy all kamehouse modules
  - Once downloaded, run the script on bash with the command `chmod a+x install-kamehouse.sh ; ./install-kamehouse.sh`

* In **Windows**: Update root password in `MARIADB_PASS_ROOT_WIN` in `${HOME}/.kamehouse/.shell/.cred` 

* [optional] Update kamehouse mariadb password `MARIADB_PASS_KAMEHOUSE` in `${HOME}/.kamehouse/.shell/.cred` 

* Open a new terminal where `KameHouse Shell` should be in the `PATH` already and run `${HOME}/programs/kamehouse-shell/bin/common/mariadb/mariadb-setup-kamehouse.sh -s -d` to configure and init mariadb database for kamehouse

* Then start both tomcat and apache to access kamehouse at http://localhost/kame-house or https://localhost/kame-house

* From a new bash terminal access KameHouse CMD module through `kamehouse-cmd.sh` and all other kamehouse-shell scripts

* All kamehouse-shell scripts should be in the path if the install script correctly updated `${HOME}/.barhrc` file to source `${HOME}/programs/kamehouse-shell/bin/common/bashrc/bashrc.sh`

**WARNING**
The script [install-kamehouse.sh](/scripts/install-kamehouse.sh) will update your bash terminal settings. To revert your terminal unsource `${HOME}/programs/kamehouse-shell/bin/common/bashrc/bashrc.sh` from `${HOME}/.barhrc` and access the kamehouse-shell scripts from their full path or run the [uninstall-kamehouse.sh](/scripts/uninstall-kamehouse.sh) script

* Follow the [Execution](/docs/execution/execution.md) guide to run kamehouse

### Linux:

* Run `set-kamehouse-sudoers-permissions.sh` to setup permissions in linux to execute all commands that need sudo

* **optionally** run `${HOME}/programs/kamehouse-shell/bin/kamehouse/install-kamehouse-shell-root.sh` to setup root user with the kamehouse-shell prompt as well. This doesn't really add any functionality. It's just to have a cooler prompt with root :)

## Uninstall:

- Download and run the [uninstall-kamehouse.sh](/scripts/uninstall-kamehouse.sh) to uninstall kamehouse from your system
```sh
chmod a+x uninstall-kamehouse.sh ; ./uninstall-kamehouse.sh
```
- Run with -p to remove all configuration files as well
- Run with -s to uninstall only kamehouse-shell
- This script doesn't remove the database contents. To do that, login to mariadb and execute 
```sql
DROP SCHEMA IF EXISTS kameHouse;
```

### Linux:

* To uninstall kamehouse-shell for root, run [uninstall-kamehouse.sh](/scripts/uninstall-kamehouse.sh) as root
