
# Installation Apache Httpd:

- Currently using apache 2.4

## Configuration files: 

- The folder `local-setup/apache` in the root of this repo contains sample apache configuration files needed to setup kamehouse locally for both windows and linux with the vhosts setup for both production and eclipse and intellij environments

- Replace nbrest with your own username in all the following commands

## Prod environment:

*********************

### Windows:

- Download a precompiled version of apache httpd (Currently using https://www.apachehaus.com/) and install it to `${HOME}/programs/apache-httpd`
- Install php to `${HOME}/programs/php`
- Update the configuration files with the ones in `local-setup/apache` 
- Make sure `httpd.conf` points correctly to the php installation

#### Create symlinks

- Create a symlink in a windows cmd console with admin permissions to serve static files from my ${HOME}/git/kamehouse repo:
```sh
mkdir "C:\Users\nbrest\programs\apache-httpd\www\kamehouse-webserver"

rmdir "C:\Users\nbrest\programs\apache-httpd\www\kamehouse-webserver\kame-house"
mklink /D "C:\Users\nbrest\programs\apache-httpd\www\kamehouse-webserver\kame-house" "C:\Users\nbrest\git\kamehouse\kamehouse-ui\src\main\webapp"

rmdir "C:\Users\nbrest\programs\apache-httpd\www\kamehouse-webserver\kame-house-groot"
mklink /D "C:\Users\nbrest\programs\apache-httpd\www\kamehouse-webserver\kame-house-groot" "C:\Users\nbrest\git\kamehouse\kamehouse-groot\public\kame-house-groot"
```

- Create link for streaming media-drive files through http (only on media-server)
```sh
mklink /D "C:\Users\nbrest\programs\apache-httpd\www\kamehouse-webserver\kame-house-streaming\media-server\media-drive" "N:\"
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

#### Create symlinks

- Create a symlink from /var/www/kamehouse-webserver/kame-house to ${HOME}/git/kamehouse/kamehouse-ui/src/main/webapp
```sh
sudo mkdir -p /var/www/kamehouse-webserver

sudo chown nbrest:users -R /var/www/kamehouse-webserver
cd /var/www/kamehouse-webserver
rm kame-house
ln -s ${HOME}/git/kamehouse/kamehouse-ui/src/main/webapp kame-house

rm kame-house-groot
ln -s ${HOME}/git/kamehouse/kamehouse-groot/public/kame-house-groot kame-house-groot
```

#### Install .httpasswd file

- This is needed for [Groot](kamehouse-groot/README.md) and [Shell](kamehouse-shell/README.md)
- Create a new one or copy the sample one from `docker/apache2` folder
```sh
cp ${HOME}/git/kamehouse/docker/apache2/.htpasswd /var/www/kamehouse-webserver/
```