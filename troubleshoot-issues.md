# Troubleshoot issues:

## Deploy to my local tomcat using my deploy script is successful but kame-house doesn't run even if it shows as running in the tomcat manager

* Go to the tomcat manager and *undeploy kame-house*, or stop tomcat and delete the war and kame-house folder from the /webapps directory. 
* Then start tomcat and run the deploy script again

## Linux commands:

* Make sure the user running tomcat has **sudo** set for the commands that require it in `VncDoSystemCommand.java` 
* I tried setting those without **sudo** and they don't work (tested on **Ubuntu 16**)

## Lock, unlock and wake-up screen commands:

* Setup a **vnc server** (I use **tightvnc** on **windows** and the **native desktop sharing tool in ubuntu**) running in the same server as the application. *Unlock screen and wake-up screen are done through vncdotool*.
* Install **vncdotool** (follow https://vncdotool.readthedocs.io/en/latest/install.html) in the same server that runs the application. Test it to make sure you can execute commands through it using the command line.
* Encrypt the user password with kamehouse keys and store it in a file specified by the property *unlock.screen.pwd.file*. This file should be readable only by the user, hidden from anyone else. The application will decrypt and type this password to unlock the screen. Check below how to create the encrypted file.
* If the vnc server is configured with a password (it should!), also set the file pointed by *vnc.server.pwd.file* with the vnc server password encrypted. This password will be used by vncdo to execute the commands through vnc. Again, this file contains an encrypted password so it should be only readable by the user owning this process. Check below how to create the encrypted file.
* Make sure vncdo in installed to */usr/local/bin/vncdo* in **linux** or update `VncDoSystemCommand.java` to point to where it is installed. Using just vncdo without the absolute path got me command not found. *It needs the absolute path* or some other fix.
* Using a vnc server and vncdotool is the only way I found to unlock the screen remotely on **windows** (also works on **ubuntu**). If you are reading this and have a better solution, please contact me at brest.nico@gmail.com
* *Lock screen command on linux relies on gnome-screensaver-command* to do the lock. 
* Install **gnome-screensaver** with `sudo apt-get install gnome-screensaver`. The `SystemCommand` to lock the screen though kamehouse could easily be changed to use vncdo and hotkeys to lock the screen for other **linux** versions (tested on **ubuntu 16**)

## VLC start and stop commands:

### Make sure vlc executable is in the user's PATH 
* In **linux** it's added by default when vlc is installed
* In **windows** I need to manually add the path to the executable to my user's `PATH` environment variable
* To test that it works, open a command prompt and type vlc to see if it finds the executable or if it throws an error that it can't find it

### Run tomcat through a startup script, not as a system service
* The commands to start and stop vlc (and possibly other system commands) don't work if tomcat is run as a service in **windows**. Check [Installation](installation.md) to run tomcat from a startup script.

## Websockets keep reconnecting infinitely, sending data but not receiving:

### This happened several times on dev environment. 
- Redeploying webapp and restarting tomcat several times didn't fix it
- Restarting apache httpd didn't fix it
- Accessing directly to tomcat without going through httpd didn't fix it (not httpd related)
- Closing chrome and reopening everything didn't fix it
- Restarting intellij didn't fix it
- Using firefox, I see the same issue. Not chrome related
- Only thing that worked was shutting down (not hibernate) computer

### It happened also in my server niko-nba
- Here I had to stop tomcat and restart it and it started working again

## Create a certificate, private key and keystore to encrypt and decrypt files required by kamehouse:

### Steps to create private key, certificate and keystore in a linux server:
```
openssl genrsa -out kamehouse.key 2048
openssl req -new -key kamehouse.key -out kamehouse.csr
openssl x509 -req -in kamehouse.csr -signkey kamehouse.key -out kamehouse.crt

cat kamehouse.key > kamehouse.pem
cat kamehouse.crt >> kamehouse.pem 

openssl pkcs12 -export -in kamehouse.pem -out kamehouse.pkcs12 
```
Then put `kamehouse.crt` and `kamehouse.pkcs12` in the directories pointed to by the properties with the same name in `commons.properties`

To create an encrypted file with the content kamehouse needs encrypted, use kamehouse-cmd with the operation encrypt.

## Create rsa private/public key pair readable kamehouse to connect to the host through ssh from docker

### Steps to create the key files:
```
openssl genpkey -algorithm RSA -out private.pem -pkeyopt rsa_keygen_bits:2048
openssl rsa -pubout -in private.pem -out public_key.pem
mv private.pem id_rsa.pkcs8
mv public_key.pem id_rsa.pub.pkcs8
```

Then put `id_rsa.pkcs8` and `id_rsa.pub.pkcs8` in the directories pointed to by the properties `ssh.private.key` and `ssh.public.key` (usually `${HOME}/.ssh/`) both in the host and in the docker container (they can be exported to the container with the docker re-init container data script)

## Apache httpd error on startup (windows):

- If I get a VRUNTIME140.dll missing error when trying to load httpd.exe, I need to install Some microsoft Visual C++ runtime. Google it. For PHP 7.4+ I need version 2019 of the runtime. version 2015 still throws some errors
- In services, configure the Apache httpd service to run as user nbrest. By default it runs as SYSTEM, and as system it doesn't run kamehouse-shell scripts correctly (when running with php)
