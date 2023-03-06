# KameHouse Execution:

- These are the kamehouse-shell scripts used to control the execution of kamehouse. These can be used both when kamehouse is run natively or inside a docker container. All the scripts are available on the directory `${HOME}/programs/kamehouse-shell/bin`
- A few of these scripts require sudo permissions. Check the [sudoers](/docker/etc/sudoers) file to see which ones need to be setup to run passwordless with sudo to be able to execute them from the kamehouse webapps

## Apache Httpd:

Use the following kamehouse-shell scripts to control apache httpd execution
```sh
httpd-startup.sh 
httpd-stop.sh 
httpd-status.sh
```

## Tomcat:

Use the following kamehouse-shell scripts to control the execution of tomcat
```sh
tomcat-startup.sh 
tomcat-startup-dev.sh
tomcat-stop.sh 
tomcat-status.sh 
tomcat-restart.sh 
```

## CMD:

Use the kamehouse-shell script `kamehouse-cmd.sh` to run kamehouse CMD module

## Tail logs:

- Use the `tail-log.sh` script to tail the kamehouse, apache httpd and tomcat logs

## Deploy:

- Use the `deploy-kamehouse.sh` script to deploy all kamehouse modules
- Use the `deploy-kamehouse-dev-tomcat.sh` script to deploy webapp kamehouse modules to dev tomcat

## Build:

- Use the `build-kamehouse.sh` script to build all kamehouse modules. Run it in the root of a git kamehouse repo

## Mysql:

- Use `mysql-status-kamehouse.sh` to have a quick overview of the status of the database
- Use `mysql-dump-kamehouse.sh` and `mysql-restore-kamehouse.sh` scripts to backup and restore the database
 
## Versions:

Check the versions of the different kamehouse apps installed. These and also the versions of the webapps are available on the ui

- `kamehouse-cmd-version.sh`
- `kamehouse-groot-version.sh`
- `kamehouse-shell-versions.sh`