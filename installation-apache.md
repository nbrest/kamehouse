
# Installation Apache Httpd:

- Currently using apache 2.4

## Configuration files: 

- The folder `local-setup/apache` in the root of this repo contains sample apache configuration files needed to setup kamehouse locally for both windows and linux with the vhosts setup for both production and eclipse and intellij environments

## Prod environment:

*********************

### Windows:

- Download a precompiled version of apache httpd (Currently using https://www.apachehaus.com/) and install it to `${HOME}/programs/apache-httpd`
- Install php to `${HOME}/programs/php`
- Update the configuration files with the ones in `local-setup/apache` 
  - Replace all the files in `${HOME}/programs/apache-httpd/conf` with the ones from `local-setup/apache/win/conf` 
  - All the apache modules that need to be loaded should already be uncommented in the sample `httpd.conf`
  - Edit `httpd.conf` and check that it points correctly to the php installation. Replace `nbrest` with your username
  ```sh
  LoadModule php7_module "C:/Users/nbrest/programs/php/php7apache2_4.dll"
  PHPiniDir "C:/Users/nbrest/programs/php"
  ```
  - Update `${HOME}/programs/apache-httpd/conf/httpd.conf`. Replace `nbrest` with your username in SRVROOT
  - Update `${HOME}/programs/apache-httpd/conf/kamehouse/doc-root-permissions.conf`. Replace `nbrest` with your username
  - Update `${HOME}/programs/apache-httpd/conf/kamehouse/vhost/http/cordova.conf`. Replace `nbrest` with your username
  - Update `${HOME}/programs/apache-httpd/conf/kamehouse/vhost/https/cordova.conf`. Replace `nbrest` with your username

#### Create symlinks

- Create a symlink in a windows cmd console to serve static files from my ${HOME}/git/kamehouse repo:
  - Execute the script [setup-apache-httpd-dirs.bat](scripts/setup-apache-httpd-dirs.bat)

#### For media server only:
- Create link for streaming media-drive files through http
```sh
mklink /D "%USERPROFILE%\programs\apache-httpd\www\kamehouse-webserver\kame-house-streaming\media-server\media-drive" "N:\"
```
- Then on the other servers in httpd config proxy /kame-house-streaming/media-server to media-server

#### Install .httpasswd file

- This is needed for [Groot](kamehouse-groot/README.md) and [Shell](kamehouse-shell/README.md)
- Create a new one or copy the sample one from `docker/apache2` folder
```sh
cp ${HOME}/git/kamehouse/docker/apache2/.htpasswd ${HOME}/programs/apache-httpd/www/kamehouse-webserver/
```

#### Run httpd through a startup script, not as a system service

- Otherwise the command to start tomcat from kamehouse groot will start tomcat in the background and when I start vlc it will also start in the background. To avoid that, don't run httpd as a service. Start it up creating a shortcut in the startup directory

#### Create a shortcut in Startup:

* Add a shortcut to `$HOME/programs/apache-httpd/bin/httpd.exe` in the *windows startup folder* (Currently in **windows 10** it's *$HOME\AppData\Roaming\Microsoft\Windows\Start Menu\Programs\Startup*) so httpd runs when I logon
* Edit the **windows** shortcut and in the field *Start in* change from *$HOME/programs/apache-httpd/bin* to *$HOME/programs/apache-httpd* otherwise it will create the application logs in *$HOME/programs/apache-httpd/bin/logs* instead of *$HOME/programs/apache-httpd/logs*
* Make the command prompt start minimized: Edit the windows shortcut -> Run -> Minimized

*********************

### Linux:

- Install apache httpd from the package manager
- Install php from the package manager
- Update the configuration files with the ones in `local-setup/apache` 
  ```sh
  sudo cp -v -f -r local-setup/apache/lin/conf to /var/apache2/conf
  sudo cp -v -f -r local-setup/apache/lin/sites-available to /var/apache2/sites-available
  sudo a2ensite default-ssl
  sudo a2enmod headers proxy proxy_http proxy_wstunnel ssl rewrite 
  sudo usermod -a -G adm [username-running-kamehouse]
  ```
  - Update `/var/apache2/conf/kamehouse/vhost/http/cordova.conf`. Replace `nbrest` with your username
  - Update `/var/apache2/conf/kamehouse/vhost/https/cordova.conf`. Replace `nbrest` with your username

#### Create symlinks

- Create a symlink from `/var/www/kamehouse-webserver/kame-house` to `${HOME}/git/kamehouse/kamehouse-ui/src/main/webapp`
  - Execute the script [setup-apache-httpd-dirs.sh](scripts/setup-apache-httpd-dirs.sh)

#### Install .httpasswd file

- This is needed for [Groot](kamehouse-groot/README.md) and [Shell](kamehouse-shell/README.md)
- Create a new one or copy the sample one from `docker/apache2` folder
```sh
cp ${HOME}/git/kamehouse/docker/apache2/.htpasswd /var/www/kamehouse-webserver/
```