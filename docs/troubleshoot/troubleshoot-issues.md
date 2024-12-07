| [Home](/README.md) | [Docs](/docs/README.md) |
---------------------------------------------------------------

*********************

# Troubleshoot issues:

*********************

## Kame-house doesn't run even if it shows as running in the tomcat manager:

* This happened rarely after a succesful deployment to my local tomcat using my deploy script
* Go to the tomcat manager and *undeploy kame-house*, or stop tomcat and delete the war and kame-house folder from the /webapps directory. 
* Then start tomcat and run the deploy script again

*********************

## Linux commands:

## Lock, unlock and wake-up screen commands:

* Setup a **vnc server** (I use **tightvnc** on **windows** and the **native desktop sharing tool in ubuntu**) running in the same server as the application. *Unlock screen and wake-up screen are done through jvncsender*.
* Encrypt the user password with kamehouse keys and store it in a file specified by the property *unlock.screen.pwd.file*. This file should be readable only by the user, hidden from anyone else. The application will decrypt and type this password to unlock the screen. Check below how to create the encrypted file.
* If the vnc server is configured with a password (it should!), also set the file pointed by *vnc.server.pwd.file* with the vnc server password encrypted. This password will be used by jvncsender to execute the commands through vnc. Again, this file contains an encrypted password so it should be only readable by the user owning this process. Check below how to create the encrypted file.
* *Lock screen command on linux relies on gnome-screensaver-command* to do the lock. Install **gnome-screensaver** with `sudo apt-get install gnome-screensaver`. The `SystemCommand` to lock the screen though kamehouse could easily be changed to use jvncsender to lock the screen for other **linux** versions (tested on **ubuntu 16**)

## Lock screen on raspbian:

* To lock the screen in raspbian create a script `gnome-screensaver-command` that uses `dm-tool lock` to lock the screen
```sh
sudo su
cd /usr/bin
vim gnome-screensaver-command
```

- Set the content to:
```sh
#!/bin/bash
XDG_SEAT_PATH=/org/freedesktop/DisplayManager/Seat0 dm-tool lock
```

- Then set the permissions
```sh
chmod a+x gnome-screensaver-command
exit
```

## sudo commands in raspbian:

### su:
```sh
sudo su
cd /usr/bin
vim su
```

- Set the content to:
```sh
/bin/su "$@"
```

- Then set the permissions
```sh
chmod a+x su
exit
```

### netstat:
```sh
sudo su
cd /usr/bin
vim netstat
```

- Set the content to:
```sh
/bin/netstat "$@"
```

- Then set the permissions
```sh
chmod a+x netstat
exit
```

### systemctl:
```sh
sudo su
cd /usr/bin
vim systemctl
```

- Set the content to:
```sh
/bin/systemctl "$@"
```

- Then set the permissions
```sh
chmod a+x systemctl
exit
```

### reboot:
```sh
sudo su
cd /usr/sbin
vim reboot
```

- Set the content to:
```sh
/sbin/reboot "$@"
```

- Then set the permissions
```sh
chmod a+x reboot
exit
```

### shutdown:
```sh
sudo su
cd /usr/sbin
vim shutdown
```

- Set the content to:
```sh
/sbin/shutdown "$@"
```

- Then set the permissions
```sh
chmod a+x shutdown
exit
```

*********************

## VLC

### Make sure vlc executable is in the user's PATH 
* In **linux** it's added by default when vlc is installed
* In **windows** I need to manually add the path to the executable to my user's `PATH` environment variable
* To test that it works, open a command prompt and type vlc to see if it finds the executable or if it throws an error that it can't find it

### Run tomcat through a startup script, not as a system service
* The commands to start and stop vlc (and possibly other system commands) don't work if tomcat is run as a service in **windows**. Check [Installation](/docs/installation/installation.md) to run tomcat from a startup script.

### VLC fails to load audio on videos
- This happened on raspberrypi bookworm
- Vlc shows this popup sometimes on top of the playing video
```sh
audio output failed: the audio device "default" could not be used: unknown error 524
```
- solution: Prefix vlc start command with `XDG_RUNTIME_DIR=/run/user/$(id -u) ` 

### VLC open file from docker on windows also opens a file explorer window
- Using docker to control a windows host has the side effect of sometimes a file explorer window gets opened minimized after vlc player.
- To attemp to close it automatically with the `vlc-start.sh` script, configure windows to launch folders in separate process. In File Explorer:
    - view > options > view > Check: launch folders in separate process

### VLC Websockets keep reconnecting infinitely, sending data but not receiving:

#### This happened several times on dev environment. 
- Redeploying webapp and restarting tomcat several times didn't fix it
- Restarting apache httpd didn't fix it
- Accessing directly to tomcat without going through httpd didn't fix it (not httpd related)
- Closing chrome and reopening everything didn't fix it
- Restarting intellij didn't fix it
- Using firefox, I see the same issue. Not chrome related
- Only thing that worked was shutting down (not hibernate) computer

#### It happened also in my server niko-nba
- Here I had to stop tomcat and restart it and it started working again

*********************

## Windows

### Any permission errors when loading tomcat/httpd
KameHouse should run fine without admin permissions, but you can configure windows startup shortcuts to run as administrator: 
- properties > shortcut > run > minimized 
- properties > shortcut > advanced > run as administrator 

- If needed, also configure windows terminal app to run `Git Bash`, `PowerShell` and `Console` profiles as administrator

### PSExec errors:

#### Couldn't install PSEXESVC service: Access is denied.

- Run from an administrator command line 
```sh 
.\programs\pstools\psexec -i -s -d explorer.exe
```

- Or from git bash:
```sh
./programs/pstools/psexec -i -s -d explorer.exe
```
- Try running the command over ssh as well

- Configure windows terminal app to run the profiles as admin, as mentioned above

*********************

## Create a certificate, private key and keystore:

- to encrypt and decrypt files required by kamehouse

### Steps to create private key, certificate and keystore in a linux server:
```sh
openssl genrsa -out kamehouse.key 2048
openssl req -new -key kamehouse.key -out kamehouse.csr
openssl x509 -req -in kamehouse.csr -signkey kamehouse.key -out kamehouse.crt

cat kamehouse.key > kamehouse.pem
cat kamehouse.crt >> kamehouse.pem 

openssl pkcs12 -export -in kamehouse.pem -out kamehouse.pkcs12
keytool -list -keystore kamehouse.pkcs12
```
Then put `kamehouse.crt` and `kamehouse.pkcs12` in the directories pointed to by the properties with the same name in `commons.properties`

To create an encrypted file with the content kamehouse needs encrypted, use kamehouse-cmd with the operation encrypt.

*********************

## Add key to existing jks keystore:

- This step is not required for kamehouse to run
- From an rsa public/private key pair generated in the above steps:

```sh
openssl pkcs12 -export -in kamehouse.pem -out kamehouse.pkcs12 -name kamehouse
keytool -list -keystore kamehouse.pkcs12

# Add key to another keystore
keytool -importkeystore -srckeystore kamehouse.pkcs12 -destkeystore kamehouse.jks -srcalias kamehouse -destalias kamehouse
keytool -list -keystore kamehouse.jks
```

- On windows define `JAVA_HOME` environment variable add `%JAVA_HOME%\bin` to `PATH` to execute `keytool`

*********************

## Create rsa private/public key pair:

- readable by kamehouse to connect to the host through ssh from docker

### Steps to create the key files:
```sh
openssl genpkey -algorithm RSA -out private.pem -pkeyopt rsa_keygen_bits:2048
openssl rsa -pubout -in private.pem -out public_key.pem
mv private.pem id_rsa.pkcs8
mv public_key.pem id_rsa.pub.pkcs8
```

Then put `id_rsa.pkcs8` and `id_rsa.pub.pkcs8` in the directories pointed to by the properties `ssh.private.key` and `ssh.public.key` (usually `${HOME}/.ssh/`) both in the host and in the docker container (they can be exported to the container with the docker re-init container data script)

*********************

## Apache httpd VRUNTIME140.dll missing error on startup (windows):

- If I get a `VRUNTIME140.dll missing` error when trying to load httpd.exe, I need to install Some microsoft Visual C++ runtime. Google it. For PHP 7.4+ I need version 2019 of the runtime. version 2015 still throws some errors
- Remember to run apache with a startup script and not as a service, as described in [installation-apache.md](/docs/installation/installation-apache.md)

*********************

## Add more subdomains to certbot SSL certificate:

```sh
certbot -d www.nicobrest.com,kame.nicobrest.com,docker-demo.nicobrest.com --expand
```

- After the update remove `Include /etc/letsencrypt/options-ssl-apache.conf` `SSLCertificateFile` and `SSLCertificateKeyFile` from `/etc/apache2/conf/kamehouse/vhost/kamehouse-https-vhosts.conf` and `/etc/apache2/conf/kamehouse/vhost/kamehouse-https-subdomains-vhosts.conf`
- Then add them back to `https-config.conf`

*********************

## Enable CORS on media server:

- To be able to test it's connectivity from js in kamehouse-mobile:
- Add the following line to conf/kamehouse/http.conf: `Header add Access-Control-Allow-Origin *`
- Restart httpd
- Don't do this if the server is exposed to internet
- It's not strictly necessary. If the connectivity test fails with CORS error, it will try to load the media server page in the inappbrowser anyway, and that should still work. In a way, receiving a CORS error is a successful connectivity test

*********************

## Commands on docker host don't work:

- When running on docker, commands executed on the host are done through ssh
- Make sure the ssh host key of the docker host is accepted in the container. To do it, ssh to the container and from the container ssh to the host once. The first time it will always ask to accept the key. If the ssh commands from kamehouse or groot don't work, this might be the issue

## KameHouse shell scripts that execute ssh command don't work:

- Here also make sure that the client executing the ssh already accepted the host key of the ssh server it connects to. I need to manually ssh from the client to the server at least once to manually accept the server key and store it in the known hosts file. Then the automated processes running through ssh with public/private keys will work.

*********************

## Remove known host keys when the server keys changed:

```sh
ssh-keygen -f "${HOME}/.ssh/known_hosts" -R "pi"
ssh-keygen -f "${HOME}/.ssh/known_hosts" -R "192.168.0.129"
```

## Can't tail apache httpd log on linux

- Add user running kamehouse to adm group as described in [kamehouse-shell](/kamehouse-shell/README.md)

*********************

## Add jar to local maven repository (deprecated)

- From the root of the project
```sh
mvn deploy:deploy-file -DgroupId=com.oracle -DartifactId=ojdbc6 -Dversion=11.2.0.4 -Durl=file:./local-maven-repo/ -DrepositoryId=local-maven-repo -DupdateReleaseInfo=true -Dfile=./lib/ojdbc6.jar
```

*********************

## Deploy kamehouse-cmd doesn't work from groot on windows

- Sometimes it fails not finding unzip. It's not a command on windows by default. In the apache error logs:
```log
deploy-kamehouse.sh: line 137: unzip: command not found
```
- Download unzip.exe and put it in a directory in your PATH

*********************

## Upgraded SSH Server stops accepting ssh keys

When this happens the best thing to do is to update your ssh keys to a more secure type accepted by the server. To work around this with the current keys, update `sshd_config` with the key types you need to support. For example:
```sh
HostKeyAlgorithms ssh-ed25519,ssh-ed25519-cert-v01@openssh.com,sk-ssh-ed25519@openssh.com,sk-ssh-ed25519-cert-v01@openssh.com,ecdsa-sha2-nistp256,ecdsa-sha2-nistp256-cert-v01@openssh.com,ecdsa-sha2-nistp384,ecdsa-sha2-nistp384-cert-v01@openssh.com,ecdsa-sha2-nistp521,ecdsa-sha2-nistp521-cert-v01@openssh.com,sk-ecdsa-sha2-nistp256@openssh.com,sk-ecdsa-sha2-nistp256-cert-v01@openssh.com,ssh-rsa,ssh-rsa-cert-v01@openssh.com,ssh-dss,ssh-dss-cert-v01@openssh.com,rsa-sha2-256,rsa-sha2-512
PubkeyAcceptedAlgorithms ssh-ed25519,ssh-ed25519-cert-v01@openssh.com,sk-ssh-ed25519@openssh.com,sk-ssh-ed25519-cert-v01@openssh.com,ecdsa-sha2-nistp256,ecdsa-sha2-nistp256-cert-v01@openssh.com,ecdsa-sha2-nistp384,ecdsa-sha2-nistp384-cert-v01@openssh.com,ecdsa-sha2-nistp521,ecdsa-sha2-nistp521-cert-v01@openssh.com,sk-ecdsa-sha2-nistp256@openssh.com,sk-ecdsa-sha2-nistp256-cert-v01@openssh.com,ssh-rsa,ssh-rsa-cert-v01@openssh.com,ssh-dss,ssh-dss-cert-v01@openssh.com,rsa-sha2-256,rsa-sha2-512
PubkeyAcceptedKeyTypes ssh-ed25519,ssh-ed25519-cert-v01@openssh.com,sk-ssh-ed25519@openssh.com,sk-ssh-ed25519-cert-v01@openssh.com,ecdsa-sha2-nistp256,ecdsa-sha2-nistp256-cert-v01@openssh.com,ecdsa-sha2-nistp384,ecdsa-sha2-nistp384-cert-v01@openssh.com,ecdsa-sha2-nistp521,ecdsa-sha2-nistp521-cert-v01@openssh.com,sk-ecdsa-sha2-nistp256@openssh.com,sk-ecdsa-sha2-nistp256-cert-v01@openssh.com,ssh-rsa,ssh-rsa-cert-v01@openssh.com,ssh-dss,ssh-dss-cert-v01@openssh.com,rsa-sha2-256,rsa-sha2-512
```

- Make sure your keys are still in `${HOME}/.ssh/authorized_keys` in the server
