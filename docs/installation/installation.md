| [Home](/README.md) | [Docs](/docs/README.md) |
---------------------------------------------------------------

*********************

# Installation:

* The recommended and easiest way is to install is through docker as explained in [docker-setup.md](/docs/docker/docker-setup.md)

## To install natively without docker:

* Install java JDK [(versions)](/docs/versions/versions.md)

* Install maven [(versions)](/docs/versions/versions.md)

* Install git (and git bash on windows)
  - On windows make sure it's installed to `C:\Program Files\Git\bin\bash.exe`

* Install msys2 (for tmux use on windows)
  - Make sure it's installed to `C:\msys64`
```sh
# In a msys64 terminal
pacman --noconfirm -S git
pacman --noconfirm -S gzip
pacman --noconfirm -S tmux
pacman --noconfirm -S rsync
pacman --noconfirm -S unzip
pacman --noconfirm -S vim
pacman --noconfirm -S wget
pacman --noconfirm -S zip
mkdir -p /home/$USER/.ssh
cp /c/Users/$USER/.ssh/* /home/$USER/.ssh
```

  - Configure msys2 environment
```sh
# In a msys64 terminal add to the BEGINNING of /etc/profile to inherit PATH
MSYS2_PATH_TYPE=inherit

# In a msys64 terminal
echo "" >> /etc/profile
echo 'export HOME="/c/Users/$USER"' >> /etc/profile
echo 'cd ${HOME}' >> /etc/profile
```

  - Configure a new windows terminal profile `KameHouse Shell`:
      - Command: `C:\msys64\usr\bin\bash.exe -i -l`
      - Starting dir: `%USERPROFILE%`
      - Run as admin: check
      - Set icon as favicon.ico from kamehouse-ui

* Install node [(versions)](/docs/versions/versions.md) 
```sh
# Update dockerfile when updating node version here
cd ~
curl -sL https://deb.nodesource.com/setup_20.x | sudo bash -
sudo apt-get -y install nodejs
sudo npm install typescript -g
```

* Insall python [(versions)](/docs/versions/versions.md) 
### Install python on windows:

  - Windows:
    - Install python to `%USERPROFILE%\programs\python`
    - Add to path on windows `%USERPROFILE%\programs\python`, `%USERPROFILE%\programs\python\Scripts` (Can be set during install)
```sh
    pip install PyQt5
    pip install loguru
    pip install requests
    pip install websocket
    pip install websocket-client
    pip install stomper
```
  - Linux:
```sh
    PYTHON_VERSION=   # set value from versions.md
    sudo apt-get install -y picom
    sudo apt-get install -y xcompmgr
    sudo apt-get install -y python${PYTHON_VERSION}
    sudo apt-get install -y python3-pyqt5
    sudo apt-get install -y python3-loguru
    sudo apt-get install -y python3-requests
    sudo apt-get install -y python3-websocket
    sudo apt-get install -y python3-websocket-client
    sudo apt-get install -y python3-stomper
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
      - The file to update depends on the mariadb version you are using. It could be 50-server.conf in a subfolder of /etc/mysql or mariadb.conf 
      ```
      default-time-zone='+10:00'
      secure-file-priv=""
      ```

* Install tomcat following [installation-tomcat.md](/docs/installation/installation-tomcat.md)

* Install apache following [installation-apache.md](/docs/installation/installation-apache.md)

* Once the above setup is complete, download and run the script [install-kamehouse.sh](/scripts/install-kamehouse.sh) from this git repo, which will pull kamehouse from git into `${HOME}/git/kamehouse` and run the deployment script that will build and deploy all kamehouse modules
```sh
wget https://raw.githubusercontent.com/nbrest/kamehouse/refs/heads/dev/scripts/install-kamehouse.sh
chmod a+x ./install-kamehouse.sh ; ./install-kamehouse.sh

# Run with -s to install shell only
chmod a+x ./install-kamehouse.sh ; ./install-kamehouse.sh -s
```

* Update the values in `${HOME}/.kamehouse/config/kamehouse.cfg` to match your local network setup then rebuild kamehouse with `deploy-kamehouse.sh`

* Configure kamehouse secrets. See [kamehouse-shell](/kamehouse-shell/README.md) to configure kamehouse secrets on your local system to run kamehouse

* Configure certificate and keystore used for encryption/decryption in kamehouse as mentioned in the section below `Create certificate and keystore:`

* In **Windows**: Update root password in `MARIADB_PASS_ROOT_WIN` in `${HOME}/.kamehouse/config/keys/.kamehouse-secrets.cfg`. See below section how to configure kamehouse secrets 

* [optional] Update kamehouse mariadb password `MARIADB_PASS_KAMEHOUSE` in `${HOME}/.kamehouse/config/keys/.kamehouse-secrets.cfg`. See below section how to configure kamehouse secrets 

* Open a new terminal where `KameHouse Shell` should be in the `PATH` already and run `${HOME}/programs/kamehouse-shell/bin/kamehouse/mariadb/mariadb-setup-kamehouse.sh -s -d` to configure and init mariadb database for kamehouse

* Then start both tomcat and apache to access kamehouse at http://localhost/kame-house or https://localhost/kame-house

* From a new bash terminal access KameHouse CMD module through `kamehouse-cmd.sh` and all other kamehouse-shell scripts

* All kamehouse-shell scripts should be in the path if the install script correctly updated `${HOME}/.barhrc` file to source `${HOME}/programs/kamehouse-shell/bin/common/bashrc/bashrc.sh`

**WARNING**
The script [install-kamehouse.sh](/scripts/install-kamehouse.sh) will update your bash terminal settings. To revert your terminal unsource `${HOME}/programs/kamehouse-shell/bin/common/bashrc/bashrc.sh` from `${HOME}/.barhrc` and access the kamehouse-shell scripts from their full path or run the [uninstall-kamehouse.sh](/scripts/uninstall-kamehouse.sh) script

* Follow the [Execution](/docs/execution/execution.md) guide to run kamehouse

### Linux:

* Run `set-kamehouse-sudoers-permissions.sh` to setup permissions in linux to execute all commands that need sudo

* **optionally** run `${HOME}/programs/kamehouse-shell/bin/kamehouse/shell/install-kamehouse-shell-root.sh` to setup root user with the kamehouse-shell prompt as well. This doesn't really add any functionality. It's just to have a cooler prompt with root :)

### Create certificate and keystore:

- These certificate and keystore are used in kamehouse to encrypt/decrypt some content in kamehouse, for example passwords for users in tennisworld

```sh
openssl genrsa -out kamehouse-private.key 2048
openssl req -new -key kamehouse-private.key -out kamehouse.csr
openssl x509 -req -in kamehouse.csr -signkey kamehouse-private.key -out kamehouse.crt

cat kamehouse-private.key > kamehouse.pem
cat kamehouse.crt >> kamehouse.pem 

openssl pkcs12 -export -in kamehouse.pem -out kamehouse.pkcs12
keytool -list -keystore kamehouse.pkcs12

mkdir -p ${HOME}/.kamehouse/config/keys/
mv kamehouse.crt ${HOME}/.kamehouse/config/keys/
mv kamehouse.pkcs12 ${HOME}/.kamehouse/config/keys/

rm kamehouse-private.key 
rm kamehouse.pem 
rm kamehouse.csr
```

Put `kamehouse.crt` and `kamehouse.pkcs12` in the directories pointed to by the properties with the same name in `commons.properties`. Default path is `${HOME}/.kamehouse/config/keys`

- After following these steps and the steps mentioned in [kamehouse-shell](/kamehouse-shell/README.md) to setup kamehouse secrets, the directory `${HOME}/.kamehouse/config/keys` should contain the following files:
    - `kamehouse.crt`
    - `kamehouse.pkcs12`
    - `kamehouse-secrets.key.enc`
    - `.kamehouse-secrets.cfg.enc`
    - `kamehouse.key`
    - `kamehouse.pub`

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
