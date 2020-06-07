# Troubleshoot issues:

## Setup/Troubleshoot linux commands:

* Make sure the user running tomcat has **sudo** set for the commands that require it in `VncDoSystemCommand.java` 
* I tried setting those without **sudo** and they don't work (tested on **Ubuntu 16**)

## Setup/Troubleshoot VLC start and stop commands:

### Make sure vlc executable is in the user's PATH 
* In **linux** it's added by default when vlc is installed
* In **windows** I need to manually add the path to the executable to my user's `PATH` environment variable
* To test that it works, open a command prompt and type vlc to see if it finds the executable or if it throws an error that it can't find it

### Run tomcat through a startup script, not as a system service
* The commands to start and stop vlc (and possibly other system commands) don't work if tomcat is run as a service in **windows**. Check [Installation](installation.md) to run tomcat from a startup script.

## Setup/Troubleshoot lock, unlock and wake-up screen commands:

* Setup a **vnc server** (I use **tightvnc** on **windows** and the **native desktop sharing tool in ubuntu**) running in the same server as the application. *Unlock screen and wake-up screen are done through vncdotool*.
* Install **vncdotool** (follow https://vncdotool.readthedocs.io/en/latest/install.html) in the same server that runs the application. Test it to make sure you can execute commands through it using the command line.
* Encode user password with base64 for the user and store it in a file specified by the property *unlock.screen.pwd.file*. This file should be readable only by the user, hidden from anyone else. The application will decode and type this password to unlock the screen. (I have a task to encrypt this and store it in a db) 
* If the vnc server is configured with a password (it should!), also set the file pointed by *vnc.server.pwd.file* with the vnc server password encoded. This password will be used by vncdo to execute the commands through vnc. Again, this file contains an encoded password so it should be only readable by the user owning this process. (Have a task to encrypt this password and store it in the db)
* Make sure vncdo in installed to */usr/local/bin/vncdo* in **linux** or update `VncDoSystemCommand.java` to point to where it is installed. Using just vncdo without the absolute path got me command not found. *It needs the absolute path* or some other fix.
* Using a vnc server and vncdotool is the only way I found to unlock the screen remotely on **windows** (also works on **ubuntu**). If you are reading this and have a better solution, please contact me at brest.nico@gmail.com
* *Lock screen command on linux relies on gnome-screensaver-command* to do the lock. 
* Install **gnome-screensaver** with `sudo apt-get install gnome-screensaver`. The `SystemCommand` to lock the screen though kamehouse could easily be changed to use vncdo and hotkeys to lock the screen for other **linux** versions (tested on **ubuntu 16**)

## Deploy to my local tomcat using my deploy script is successful but kame-house doesn't run even if it shows as running in the tomcat manager

* Go to the tomcat manager and *undeploy kame-house*, or stop tomcat and delete the war and kame-house folder from the /webapps directory. 
* Then start tomcat and run the deploy script again (in my.scripts repo)
