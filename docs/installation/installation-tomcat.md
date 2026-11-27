| [Home](/README.md) | [Docs](/docs/README.md) |
---------------------------------------------------------------

*********************

## Install Tomcat:

- Download tomcat from apache website [(versions)](/docs/versions/versions.md)
- **Run tomcat through a startup script, not as a system service**. Some commands like ztv start and stop won't work otherwise
- Add `TOMCAT_TEXT_USER` and `TOMCAT_TEXT_PASS` env variables to `${HOME}/.kamehouse/secrets/.kamehouse-secrets.cfg` for groot to access tomcat manager's api. See [docker .kamehouse-secrets.cfg](/docker/setup-kamehouse/secrets/.kamehouse-secrets.cfg) as an example.

*********************

### Windows:

* If I currently have tomcat running as a service, uninstall the service. 
* Download tomcat from apache's website and extract it to `$HOME/.kamehouse/opt/kamehouse-tomcat`
* Use the sample configuration in the folder [/local-setup/tomcat](/local-setup/tomcat) to update the tomcat port and manager users
* Add a shortcut to the `$HOME/.kamehouse/opt/kamehouse-tomcat/bin/startup.bat` script in the *windows startup folder* (Currently in **windows 10/11** it's `$HOME\AppData\Roaming\Microsoft\Windows\Start Menu\Programs\Startup`) so tomcat runs when I logon
* Edit the **windows** shortcut and in the field *Start in* change from `$HOME/.kamehouse/opt/kamehouse-tomcat/bin` to `$HOME/.kamehouse/opt/kamehouse-tomcat` otherwise it will create the application logs in `$HOME/.kamehouse/opt/kamehouse-tomcat/bin/logs` instead of `$HOME/.kamehouse/opt/kamehouse-tomcat/logs`
* To make the command prompt start minimized, update **catalina.bat** and in the line where it says `set _EXECJAVA=start "%TITLE%" %_RUNJAVA%` add `/min` after the start: `set _EXECJAVA=start /min "%TITLE%" %_RUNJAVA%`

*********************

### Linux:

* If I have any tomcat installed as a service, run `sudo apt-get remove tomcatX`, `sudo apt-get purge tomcatX`. Remove everything related to tomcat. 
* Download the zip or tar.gz from the tomcat website
* Unpack it in `$HOME/.kamehouse/opt/kamehouse-tomcat` and start it with `$HOME/.kamehouse/opt/kamehouse-tomcat/bin/startup.sh` 
* Use the sample configuration in the folder [/local-setup/tomcat](/local-setup/tomcat) to update the tomcat port and manager users
* Tomcat should start with my current user
* To run on startup, with my current user, create a script where I cd to `$HOME/.kamehouse/opt/kamehouse-tomcat` (TOMCAT_HOME) and then run `./bin/startup.sh` in that script (`tomcat/tomcat-startup.sh` in kamehouse-shell). I need to do it this way because I need to be on `$HOME/.kamehouse/opt/kamehouse-tomcat` when I run `startup.sh`. If I run the script from `$HOME/.kamehouse/opt/kamehouse-tomcat/bin`, it will create the application logs in `$HOME/.kamehouse/opt/kamehouse-tomcat/bin/logs` instead of `$HOME/.kamehouse/opt/kamehouse-tomcat/logs`. 
* Update the script `$HOME/.kamehouse/opt/kamehouse-tomcat/bin/startup.sh` and as the second line add `export DISPLAY=:0` otherwise *ztv start* will fail because *DISPLAY* env variable won't be set at reboot time when tomcat is being started. I don't need to set it if I run `$HOME/.kamehouse/opt/kamehouse-tomcat/bin/startup.sh` from my desktop but if I schedule it at boot, `$HOME/.kamehouse/opt/kamehouse-tomcat/bin/startup.sh` needs to be updated with that export.
* To run on startup in **ubuntu**: Use startup scripts. Check `kamehouse-startup-service.sh`, `kamehouse-startup.service`, `kamehouse-startup-service-deploy.sh` in startup in kamehouse-shell to automate tomcat startup
