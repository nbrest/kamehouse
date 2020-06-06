# Troubleshoot issues:

### Setup/Troubleshoot linux commands:

* Make sure the user running tomcat has sudo set for the commands that require it in VncDoSystemCommand.java. I tried setting those without sudo and they don't work (tested on Ubuntu 16).

### Setup/Troubleshoot VLC start and stop commands:

* Make sure vlc executable is in the user's PATH. In linux it's added by default when vlc is installed. In windows I need to manually add the path to the executable to my user's PATH environment variable. To test that it works, open a command prompt and type vlc to see if it finds the executable or if it throws an error that it can't find it.

* The commands to start and stop vlc (and possibly other system commands) don't work if tomcat is run as a service in windows, even if it's configured to run as a service with my user. To fix this, uninstall the service. Download tomcat and extract it to $HOME/programs/apache-tomcat. Add a shortcut to the $HOME/programs/apache-tomcat/bin/startup.bat script in the windows startup folder (Currently in windows 10 it's $HOME\AppData\Roaming\Microsoft\Windows\Start Menu\Programs\Startup) so tomcat runs when I logon. Edit the windows shortcut and in the field 'Start in' change from $HOME/programs/apache-tomcat/bin to $HOME/programs/apache-tomcat otherwise it will create the application logs in $HOME/programs/apache-tomcat/bin/logs instead of $HOME/programs/apache-tomcat/logs

* To make the command windows start minimized, update catalina.bat and in the line where it says 'set _EXECJAVA=start "%TITLE%" %_RUNJAVA%' add /min after the start: 'set _EXECJAVA=start /min "%TITLE%" %_RUNJAVA%'

* For the vlc start and stop (and possibly other system commands) to work in Linux, if I have any tomcat installed as a service, run sudo apt-get remove tomcatX, sudo apt-get purge tomcatX, remove everything. Download the zip or tar.gz from the tomcat website, unpack it in $HOME/programs/apache-tomcat and start it with $HOME/programs/apache-tomcat/bin/startup.sh. Tomcat should start with my current user and the command to start and stop vlc should work.
* To run on startup, with my current user, create a script where I cd to $HOME/programs/apache-tomcat (TOMCAT_HOME) and then run ./bin/startup.sh in that script. I need to do it this way because I need to be on $HOME/programs/apache-tomcat when I run startup.sh. If I run the script from $HOME/programs/apache-tomcat/bin, it will create the application logs in $HOME/programs/apache-tomcat/bin/logs instead of $HOME/programs/apache-tomcat/logs. Then edit my cron jobs with 'crontab -e' and add the following entry: 

'@reboot /bin/bash /PATH-TO-MY-SCRIPT/tomcat-startup.sh'

* Also update the script startup.sh and as the second line add 'export DISPLAY=:0' otherwise vlc start will fail because DISPLAY env variable won't be set at reboot time when tomcat is being started. I don't need to set it if I run startup.sh from my desktop but if I schedule it with cron, startup.sh needs to be updated with that export.

### Setup/Troubleshoot lock, unlock and wake-up screen commands:

* Setup a vnc server (I use tightvnc on windows and the native desktop sharing tool in ubuntu) running in the same server as the application. Unlock screen is done through vncdotool.
* Install vncdotool (follow https://vncdotool.readthedocs.io/en/latest/install.html) in the same server that runs the application. Test it to make sure you can execute commands through it using the command line.
* Encode user password with base64 for the user and store it in a file specified by the property unlock.screen.pwd.file. This file should be readable only by the user, hidden from anyone else. The application will decode and type this password to unlock the screen.
* If the vnc server is configured with a password (it should!), also set the file pointed by vnc.server.pwd.file with the vnc server password encoded. This password will be used by vncdo to execute the commands through vnc. Again, this file contains an encoded password so it should be only readable by the user owning this process.
* Make sure vncdo in installed to /usr/local/bin/vncdo in linux or update VncDoSystemCommand.java to point to where it is installed. Using just vncdo without the absolute path got me command not found. It needs the absolute path or some other fix.
* Using a vnc server and vncdotool is the only way I found to unlock the screen remotely on windows 10 (also works on ubuntu). If you are reading this and have a better solution, please contact me.
* Lock screen command on linux relies on gnome-screensaver-command to do the lock. Install it with sudo apt-get install gnome-screensaver. The command line could easily be changed to use vncdo and hotkeys to lock the screen for other linux versions (tested on ubuntu 16).

### Deploy to my local tomcat using my deploy script is successful but kame-house doesn't run even if it shows as running in the tomcat manager

* Go to the tomcat manager and undeploy kame-house, or stop tomcat and delete the war and kame-house folder from the /webapps directory. 
* Then start tomcat and run the deploy script again
