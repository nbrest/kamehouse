| [Home](/README.md) | [Docs](/docs/README.md) |
---------------------------------------------------------------

*********************

# Apache httpd dev environment setup:

- Access kamehouse frontend through the port defined in `local-setup/apache/[lin|win]/conf/kamehouse/vhost/*.conf`

## Setup directories

### Linux
```sh
sudo mkdir -p ${HOME}/.kamehouse/var/www/html/kamehouse-kintoun
sudo chown ${USER}:users -R ${HOME}/.kamehouse/var/www/html/kamehouse-kintoun
```

### Windows
```sh
mkdir -p "%USERPROFILE%\.kamehouse\var\www\html\kamehouse-kintoun"
```
