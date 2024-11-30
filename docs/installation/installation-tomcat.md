| [Home](/README.md) | [Docs](/docs/README.md) |
---------------------------------------------------------------

*********************

## Install Tomcat:

- Download tomcat from apache website [(versions)](/docs/versions/versions.md)
- **Run tomcat through a startup script, not as a system service**. Some commands like vlc start and stop won't work otherwise
- Add `TOMCAT_TEXT_USER` and `TOMCAT_TEXT_PASS` env variables to `${HOME}/.kamehouse/.shell/shell.pwd` for groot to access tomcat manager's api. See [docker shell.pwd](/docker/keys/shell.pwd) as an example.

*********************

### Windows:

* If I currently have tomcat running as a service, uninstall the service. 
* Download tomcat from apache's website and extract it to `$HOME/programs/apache-tomcat`
* Use the sample configuration in the folder [/local-setup/tomcat](/local-setup/tomcat) to update the tomcat port and manager users
* Add a shortcut to the `$HOME/programs/apache-tomcat/bin/startup.bat` script in the *windows startup folder* (Currently in **windows 10/11** it's `$HOME\AppData\Roaming\Microsoft\Windows\Start Menu\Programs\Startup`) so tomcat runs when I logon
* Edit the **windows** shortcut and in the field *Start in* change from `$HOME/programs/apache-tomcat/bin` to `$HOME/programs/apache-tomcat` otherwise it will create the application logs in `$HOME/programs/apache-tomcat/bin/logs` instead of `$HOME/programs/apache-tomcat/logs`
* To make the command prompt start minimized, update **catalina.bat** and in the line where it says `set _EXECJAVA=start "%TITLE%" %_RUNJAVA%` add `/min` after the start: `set _EXECJAVA=start /min "%TITLE%" %_RUNJAVA%`

*********************

### Linux:

* If I have any tomcat installed as a service, run `sudo apt-get remove tomcatX`, `sudo apt-get purge tomcatX`. Remove everything related to tomcat. 
* Download the zip or tar.gz from the tomcat website
* Unpack it in `$HOME/programs/apache-tomcat` and start it with `$HOME/programs/apache-tomcat/bin/startup.sh` 
* Use the sample configuration in the folder [/local-setup/tomcat](/local-setup/tomcat) to update the tomcat port and manager users
* Tomcat should start with my current user
* To run on startup, with my current user, create a script where I cd to `$HOME/programs/apache-tomcat` (TOMCAT_HOME) and then run `./bin/startup.sh` in that script (`kamehouse/tomcat-startup.sh` in kamehouse-shell). I need to do it this way because I need to be on `$HOME/programs/apache-tomcat` when I run `startup.sh`. If I run the script from `$HOME/programs/apache-tomcat/bin`, it will create the application logs in `$HOME/programs/apache-tomcat/bin/logs` instead of `$HOME/programs/apache-tomcat/logs`. 
* Update the script `$HOME/programs/apache-tomcat/bin/startup.sh` and as the second line add `export DISPLAY=:0` otherwise *vlc start* will fail because *DISPLAY* env variable won't be set at reboot time when tomcat is being started. I don't need to set it if I run `$HOME/programs/apache-tomcat/bin/startup.sh` from my desktop but if I schedule it at boot, `$HOME/programs/apache-tomcat/bin/startup.sh` needs to be updated with that export.
* To run on startup in **ubuntu 20**: Use startup scripts. Check `kamehouse-startup-service.sh`, `kamehouse-startup.service`, `kamehouse-startup-service-deploy.sh` in lin/startup in kamehouse-shell to automate tomcat startup
