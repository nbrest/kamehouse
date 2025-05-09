| [Home](/README.md) | [Docs](/docs/README.md) |
---------------------------------------------------------------

*********************

# Shell Module:

This module contains most of my shell scripts to automate some tasks setting up, building, deploying and debugging kamehouse.

## Install

- The script `./scripts/install-kamehouse.sh` also installs kamehouse-shell
- Run with `-s` to install kamehouse-shell only
- Run with `-o` to install kamehouse-shell scripts only, without modifying the shell

- Update the `kamehouse-shell` values in `${HOME}/.kamehouse/config/kamehouse.cfg` to match your local network setup for the shell scripts

## Linux:

- Add user running kamehouse to adm group to be able to tail apache2 logs
```sh
sudo usermod -a -G adm username
```

## Keep alive scripts:

### Windows:

- Run `copy-b-bat.sh` to copy `b.bat` to the home directory. Then from cmd.exe just type b enter to start git-bash

- Use windows task scheduler to schedule every X minutes the keep-alive-\*.bat that calls the keep-alive-\*.sh 

----------------------------------

## Setup kamehouse-secrets:

- KameHouse secrets are stored in the encrypted file `${HOME}/.kamehouse/config/keys/.kamehouse-secrets.cfg.enc`

- A template can be found in [.kamehouse-secrets.cfg](/docker/keys/.kamehouse-secrets.cfg). Copy the template to `${HOME}/.kamehouse/config/keys/`. When kamehouse is installed using the installation script mentioned [here](/docs/installation/installation.md), the template and sample keys will be copied automatically to the required folder and then the values can be edited with kamehouse-shell scripts.

- Use `encrypt-kamehouse-secrets.sh` to encrypt `.kamehouse/config/keys/.kamehouse-secrets.cfg` secrets file

- Use `decrypt-kamehouse-secrets.sh` to decrypt `.kamehouse/config/keys/.kamehouse-secrets.cfg.enc` secrets file

--------------------------------------------------

## Edit .kamehouse-secrets.cfg

- Use `edit-kamehouse-secrets.sh` to edit the secrets. This script does automatic decryption before editing and encryption after editing, so there's no need to run the decrypt and encrypt scripts

### Set the values for the kamehouse secrets:

- Set `VNC_SERVER_PASS` to execute vnc commands 
- Set `UNLOCK_SCREEN_PASS` to unlock the kamehouse user's screen
- Set `INTEGRATION_TESTS_CRED` to run integration tests
- Set `MARIADB_PASS_KAMEHOUSE` with the mariadb password used by kamehouse


- See `kamehouse.cfg` and the sample `.kamehouse-secrets.cfg` for the description of the other secrets used by kamehouse

--------------------------------------------------

## Generate keys to replace the sample keys from the installation:

- The installation script will put sample keys to encrypt the kamehouse secrets but it is recommended to generate new ones for each installation and encrypt the secrets with those

### generate symmetric key kamehouse-secrets.key to encrypt the kamehouse secrets
```sh
openssl rand 214 > ${HOME}/.kamehouse/config/keys/kamehouse-secrets.key
```

### generate private/public keys to encrypt/decrypt the symmetric key:
```sh
openssl genpkey -algorithm RSA -out ${HOME}/.kamehouse/config/keys/kamehouse.key -pkeyopt rsa_keygen_bits:2048
openssl rsa -pubout -in ${HOME}/.kamehouse/config/keys/kamehouse.key -out ${HOME}/.kamehouse/config/keys/kamehouse.pub
```

--------------------------------------------------

## Encrypt .kamehouse-secrets.cfg

- The manual encryption process would be:

### encrypt .kamehouse-secrets.cfg with symmetric key kamehouse-secrets.key
```sh
openssl enc -in ${HOME}/.kamehouse/config/keys/.kamehouse-secrets.cfg -out ${HOME}/.kamehouse/config/keys/.kamehouse-secrets.cfg.enc -pbkdf2 -aes256 -kfile ${HOME}/.kamehouse/config/keys/kamehouse-secrets.key
```

### encrypt symmetric key kamehouse-secrets.key with public key kamehouse.pub:
```sh
openssl pkeyutl -encrypt -inkey ${HOME}/.kamehouse/config/keys/kamehouse.pub -pubin -in ${HOME}/.kamehouse/config/keys/kamehouse-secrets.key -out ${HOME}/.kamehouse/config/keys/kamehouse-secrets.key.enc
```

### delete decrypted kamehouse-secrets.key and .kamehouse-secrets.cfg
```sh
rm ${HOME}/.kamehouse/config/keys/kamehouse-secrets.key
rm ${HOME}/.kamehouse/config/keys/.kamehouse-secrets.cfg
```

--------------------------------------------------

## Decrypt and load .kamehouse-secrets.cfg

- Use `edit-kamehouse-secrets.sh` to temporarily decrypt the secrets for editing

- The manual decryption process would be:

### decrypt symetric key with private key
```sh
SUFFIX=$RANDOM
openssl pkeyutl -decrypt -inkey ${HOME}/.kamehouse/config/keys/kamehouse.key -in ${HOME}/.kamehouse/config/keys/kamehouse-secrets.key.enc -out ${HOME}/.kamehouse/config/keys/kamehouse-secrets.key.${SUFFIX}
```

### decrypt .kamehouse-secrets.cfg.enc with symetric key kamehouse-secrets.key
```sh
openssl enc -d -in ${HOME}/.kamehouse/config/keys/.kamehouse-secrets.cfg.enc -out ${HOME}/.kamehouse/config/keys/.kamehouse-secrets.cfg.${SUFFIX} -pbkdf2 -aes256 -kfile ${HOME}/.kamehouse/config/keys/kamehouse-secrets.key.${SUFFIX}
```

### load .kamehouse-secrets.cfg 
```sh
source ${HOME}/.kamehouse/config/keys/.kamehouse-secrets.cfg.${SUFFIX}
```

### delete .kamehouse-secrets.cfg and symmetric key kamehouse-secrets.key 
```sh
rm ${HOME}/.kamehouse/config/keys/kamehouse-secrets.key.${SUFFIX} 
rm ${HOME}/.kamehouse/config/keys/.kamehouse-secrets.cfg.${SUFFIX} 
```
