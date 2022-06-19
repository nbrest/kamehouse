# Apache httpd dev environment setup:

- Replace nbrest with your own username in all the following commands

## eclipse:

### Windows:

#### Create symlinks

- Create symlink to pickup static resources from eclipse (from WINDOWS cmd admin console)
```sh
mkdir "C:\Users\nbrest\programs\apache-httpd\www\www-eclipse"

rmdir "C:\Users\nbrest\programs\apache-httpd\www\www-eclipse\kame-house"
mklink /D "C:\Users\nbrest\programs\apache-httpd\www\www-eclipse\kame-house" "C:\Users\nbrest\workspace-eclipse\kamehouse\kamehouse-ui\src\main\webapp"

rmdir "C:\Users\nbrest\programs\apache-httpd\www\www-eclipse\kame-house-groot"
mklink /D "C:\Users\nbrest\programs\apache-httpd\www\www-eclipse\kame-house-groot" "C:\Users\nbrest\workspace-eclipse\kamehouse\kamehouse-groot\public\kame-house-groot"
```

### Linux:

#### Create symlinks

- Create the symlink in linux:
```sh
sudo mkdir -p /var/www/www-eclipse

sudo chown nbrest:users -R /var/www/www-eclipse
cd /var/www/www-eclipse

rm kame-house
ln -s ${HOME}/workspace-eclipse/kamehouse/kamehouse-ui/src/main/webapp kame-house

rm kame-house-groot
ln -s ${HOME}/workspace-eclipse/kamehouse/kamehouse-groot/public/kame-house-groot kame-house-groot
```

- Access kamehouse eclipse through the port defined for workspace-eclipse in ${HOME}/programs/apache-httpd/conf/kamehouse/vhost/*.conf

*********************

## intellij:

### Windows:

#### Create symlinks

- Create symlink to pickup static resources from intellij (from WINDOWS cmd admin console)
```sh
mkdir "C:\Users\nbrest\programs\apache-httpd\www\www-intellij"

rmdir "C:\Users\nbrest\programs\apache-httpd\www\www-intellij\kame-house"
mklink /D "C:\Users\nbrest\programs\apache-httpd\www\www-intellij\kame-house" "C:\Users\nbrest\workspace-intellij\kamehouse\kamehouse-ui\src\main\webapp"

rmdir "C:\Users\nbrest\programs\apache-httpd\www\www-intellij\kame-house-groot"
mklink /D "C:\Users\nbrest\programs\apache-httpd\www\www-intellij\kame-house-groot" "C:\Users\nbrest\workspace-intellij\kamehouse\kamehouse-groot\public\kame-house-groot"
```

### Linux:

#### Create symlinks

- Create the symlink in linux:
```sh
sudo mkdir -p /var/www/www-intellij

sudo chown nbrest:users -R /var/www/www-intellij
cd /var/www/www-intellij

rm kame-house
ln -s ${HOME}/workspace-intellij/kamehouse/kamehouse-ui/src/main/webapp kame-house

rm kame-house-groot
ln -s ${HOME}/workspace-intellij/kamehouse/kamehouse-groot/public/kame-house-groot kame-house-groot
```

- Access kamehouse intellij through the port defined for workspace-intellj in ${HOME}/programs/apache-httpd/conf/kamehouse/vhost/*.conf
