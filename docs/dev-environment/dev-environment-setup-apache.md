| [Home](/README.md) | [Docs](/docs/README.md) |
---------------------------------------------------------------

*********************

# Apache httpd dev environment setup:

- Access kamehouse frontend through the port defined for dev in `${HOME}/programs/apache-httpd/conf/kamehouse/vhost/*.conf`

## Setup directories

### Linux
```sh
  sudo mkdir -p /var/www/kamehouse-webserver-dev
  sudo chown ${USER}:users -R /var/www/kamehouse-webserver-dev
```

### Windows
```sh
mkdir "%USERPROFILE%\programs\apache-httpd\www\kamehouse-webserver-dev"
```
