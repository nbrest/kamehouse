# KameHouse Execution:

- These are the scripts used to control the execution of kamehouse. These can be used both when kamehouse is run natively or inside a docker container

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
tomcat-stop.sh 
tomcat-status.sh 
tomcat-restart.sh 
tomcat-start-dev.sh
```

## CMD:

Use the kamehouse-shell script `kamehouse-cmd.sh` to run kamehouse CMD module

## Tail logs:

- Use the `tail-log.sh` script to tail the kamehouse logs

## Deploy:

- Use the `deploy-kamehouse.sh` script to deploy all kamehouse modules

## Build:

- Use the `build-kamehouse.sh` script to build all kamehouse modules
