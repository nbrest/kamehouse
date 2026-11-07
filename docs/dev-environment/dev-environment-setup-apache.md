| [Home](/README.md) | [Docs](/docs/README.md) |
---------------------------------------------------------------

*********************

# Apache httpd dev environment setup:

- Access kamehouse frontend through the port defined in `${HOME}/programs/apache-httpd/conf/kamehouse/vhost/*.conf`

## Setup directories

### Linux
```sh
  sudo mkdir -p /var/www/kamehouse-webserver
  sudo chown ${USER}:users -R /var/www/kamehouse-webserver
```

### Windows
```sh
mkdir "%USERPROFILE%\programs\apache-httpd\www\kamehouse-webserver"
```
