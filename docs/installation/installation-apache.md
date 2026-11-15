| [Home](/README.md) | [Docs](/docs/README.md) |
---------------------------------------------------------------

*********************

# Installation Apache Httpd:

- Download and install apache httpd [(versions)](/docs/versions/versions.md)

## Configuration files: 

- The folder [/local-setup/apache](/local-setup/apache) in the root of this repo contains sample apache configuration files needed to setup kamehouse locally for both windows and linux with the vhosts setup for both production and dev environments

## Prod environment:

*********************

### Windows:

- Download a precompiled version of apache httpd (Currently using https://www.apachehaus.com/) and install it to `${HOME}/.kamehouse/opt/apache-httpd`
- Update the configuration files with the ones in [/local-setup/apache](/local-setup/apache)
  - Replace all the files in `${HOME}/.kamehouse/opt/apache-httpd/conf` with the ones from [/local-setup/apache/win/conf/](/local-setup/apache/win/conf/)
  - All the apache modules that need to be loaded should already be uncommented in the sample `httpd.conf`
  - Update `${HOME}/.kamehouse/opt/apache-httpd/conf/httpd.conf`. Replace `nbrest` with your username in HOME_KAMEHOUSE
  - Update `${HOME}/.kamehouse/opt/apache-httpd/conf/kamehouse/doc-root-permissions.conf`. Replace `nbrest` with your username

- In an admin cmd.exe run these commands to create the httpd logs symlink to tail the logs:
```sh
rm "%USERPROFILE%\.kamehouse\var\log\apache2"
mklink /D "%USERPROFILE%\.kamehouse\var\log\apache2" "%USERPROFILE%\.kamehouse\opt\apache-httpd\logs"
```

#### Run httpd through a startup script, not as a system service

- Otherwise the command to start tomcat from kamehouse groot will start tomcat in the background and when I start vlc it will also start in the background. To avoid that, don't run httpd as a service. Start it up creating a shortcut in the startup directory

#### Create a shortcut in Startup:

* Add a shortcut to `$HOME/.kamehouse/opt/apache-httpd/bin/httpd.exe` in the *windows startup folder* (Currently in **windows 10** it's *$HOME\AppData\Roaming\Microsoft\Windows\Start Menu\Programs\Startup*) so httpd runs when I logon
* Edit the **windows** shortcut and in the field *Start in* change from *$HOME/.kamehouse/opt/apache-httpd/bin* to *$HOME/.kamehouse/opt/apache-httpd* otherwise it will create the application logs in *$HOME/.kamehouse/opt/apache-httpd/bin/logs* instead of *$HOME/.kamehouse/opt/apache-httpd/logs*
* Make the command prompt start minimized: Edit the windows shortcut -> Run -> Minimized

*********************

### Linux:

- Install apache httpd from the package manager
- Update the configuration files with the ones in [/local-setup/apache](/local-setup/apache)
```sh
# config apache
sudo cp -v -f -r local-setup/apache/lin/conf to /var/apache2/conf
sudo cp -v -f -r local-setup/apache/lin/sites-available to /var/apache2/sites-available
sudo a2ensite default-ssl
sudo a2enmod headers proxy proxy_http proxy_wstunnel ssl rewrite 
```

- Make static content root dir for kamehouse:
```sh
mkdir -p ${HOME}/.kamehouse/var/www/html/kamehouse-kintoun
sudo mkdir -p /var/www/
sudo rm -rf /var/www/html
sudo ln -s ${HOME}/.kamehouse/var/www/html/kamehouse-kintoun /var/www/html
```

- Allow kamehouse user to read apache logs:
```sh
sudo chmod a+rx /var/log/apache2
```

- Setup tail log script for apache logs
```sh
mkdir -p ${HOME}/.kamehouse/var/log/
ln -s /var/log/apache2 ${HOME}/.kamehouse/var/log/apache2
```
