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

- Download a precompiled version of apache httpd (Currently using https://www.apachehaus.com/) and install it to `${HOME}/programs/apache-httpd`
- Install php to `${HOME}/programs/php`
- Update the configuration files with the ones in [/local-setup/apache](/local-setup/apache)
  - Replace all the files in `${HOME}/programs/apache-httpd/conf` with the ones from [/local-setup/apache/win/conf/](/local-setup/apache/win/conf/)
  - All the apache modules that need to be loaded should already be uncommented in the sample `httpd.conf`
  - Edit `httpd.conf` and check that it points correctly to the php installation. Replace `nbrest` with your username
  ```sh
  LoadModule php7_module "C:/Users/[USERNAME]/programs/php/php7apache2_4.dll"
  PHPiniDir "C:/Users/[USERNAME]/programs/php"
  ```
  - Update `${HOME}/programs/apache-httpd/conf/httpd.conf`. Replace `nbrest` with your username in SRVROOT
  - Update `${HOME}/programs/apache-httpd/conf/kamehouse/doc-root-permissions.conf`. Replace `nbrest` with your username

- Enable mariadb extension on php. Update `php.ini`:
  - Set with full path `extension_dir = "C:/Users/[USERNAME]/programs/php/ext"`
  - Uncomment: `extension=mysqli`
  - Uncomment: `extension=pdo_mysql`

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
- Install php mariadb: 
```
sudo apt-get install php-mysql libapache2-mod-php
```

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
  sudo mkdir -p /var/www/kamehouse-webserver
  sudo chown ${USER}:users -R /var/www/kamehouse-webserver
```

- Allow kamehouse user to read apache logs:
```sh
sudo chmod a+rx /var/log/apache2
```

- Allow `www-data` user to access `${HOME}/logs` from kamehouse user
```sh
chmod a+rx ${HOME}
chmod a+rx ${HOME}/logs
```
