# Installation:

* Deploy as a standard war into the webapps directory of your tomcat installation

## Tomcat:

* **Run tomcat through a startup script, not as a system service**

### Windows:

* If I currently have it running as a service, uninstall the service. 
* Download tomcat from apache's website and extract it to *$HOME/programs/apache-tomcat*
* Add a shortcut to the `$HOME/programs/apache-tomcat/bin/startup.bat` script in the *windows startup folder* (Currently in **windows 10** it's *$HOME\AppData\Roaming\Microsoft\Windows\Start Menu\Programs\Startup*) so tomcat runs when I logon
* Edit the **windows** shortcut and in the field 'Start in' change from *$HOME/programs/apache-tomcat/bin* to *$HOME/programs/apache-tomcat* otherwise it will create the application logs in *$HOME/programs/apache-tomcat/bin/logs* instead of *$HOME/programs/apache-tomcat/logs*
* To make the command prompt start minimized, update **catalina.bat** and in the line where it says `set _EXECJAVA=start "%TITLE%" %_RUNJAVA%` add `/min` after the start: `set _EXECJAVA=start /min "%TITLE%" %_RUNJAVA%`

### Linux:
* If I have any tomcat installed as a service, run `sudo apt-get remove tomcatX`, `sudo apt-get purge tomcatX`. Remove everything related to tomcat. 
* Download the zip or tar.gz from the tomcat website
* Unpack it in *$HOME/programs/apache-tomcat* and start it with `$HOME/programs/apache-tomcat/bin/startup.sh` 
* Tomcat should start with my current user
* To run on startup, with my current user, create a script where I cd to *$HOME/programs/apache-tomcat* (TOMCAT_HOME) and then run `./bin/startup.sh` in that script (`kamehouse/tomcat-startup.sh` in my.scripts repo). I need to do it this way because I need to be on *$HOME/programs/apache-tomcat* when I run `startup.sh`. If I run the script from *$HOME/programs/apache-tomcat/bin*, it will create the application logs in *$HOME/programs/apache-tomcat/bin/logs* instead of *$HOME/programs/apache-tomcat/logs*. 
* Update the script `$HOME/programs/apache-tomcat/bin/startup.sh` and as the second line add `export DISPLAY=:0` otherwise *vlc start* will fail because *DISPLAY* env variable won't be set at reboot time when tomcat is being started. I don't need to set it if I run `$HOME/programs/apache-tomcat/bin/startup.sh` from my desktop but if I schedule it at boot, `$HOME/programs/apache-tomcat/bin/startup.sh` needs to be updated with that export.
* To run on startup in **ubuntu 20**: Use startup scripts. Check `rc-local.sh`, `rc-local.service`, `rc-local-deploy.sh` in lin/startup in my.scripts repo to automate tomcat startup
* {*DEPRECATED*} - This doesn't work in **ubuntu 20**. To run on startup edit my cron jobs with `crontab -e` and add the following entry: `@reboot /bin/bash /PATH-TO-MY-SCRIPT/tomcat-startup.sh`

## Apache Httpd:

* Same as in [Dev Environment Setup](dev-environment-setup.md)
* The config I have in my private repo for httpd has the setup to listen to ports for development and for prod
