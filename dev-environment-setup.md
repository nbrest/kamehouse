# Dev Environment Setup:

*********************

# Eclipse:

- Follow [dev-environment-setup-eclipse.md](dev-environment-setup-eclipse.md) 

# IntelliJ:

- Follow [dev-environment-setup-intellij.md](dev-environment-setup-intellij.md) 

# KameHouse shell:

Install kamehouse-shell to use all the scripts mentioned below. From the root of your working copy:
```sh
./scripts/install-kamehouse.sh -o
```
Running with -o will install kamehouse shell scripts only. 
Running with -s will install kamehouse shell completely but not the other modules. 
Running Without parameters it installs all kamehouse modules and the full changes to the shell.
All the scripts are available on the directory `${HOME}/programs/kamehouse-shell/bin`

# Tomcat Dev:

* Download tomcat from apache's website and extract it to *$HOME/programs/apache-tomcat-dev*
* Use the sample configuration in the folder `local-setup/tomcat-dev` to update the tomcat port and manager users
* Start your local dev tomcat with `tomcat-startup-dev.sh`
* Deploy your local working copy to dev tomcat with `deploy-kamehouse-dev-tomcat.sh`
* Tail dev tomcat logs with `tail-log.sh -f (eclipse|intellij)`
* Stop your dev tomcat with `tomcat-stop.sh -p [dev-tomcat-port]`

# Apache Httpd:

- Follow [installation-apache.md](installation-apache.md) guide to install apache 
- Follow [dev-environment-setup-apache.md](dev-environment-setup-apache.md) to configure apache for intellij or eclipse dev
- Start apache httpd with `httpd-startup.sh`
- Tail apache httpd logs with `tail-log.sh -f apache`
- Stop apache httdd with `httpd-stop.sh`

# Docker Dev environment:

- Instead of setting up a local dev tomcat and apache httpd on the host, you can run a dev docker container and deploy all your changes from your eclipse or intellij working copy to the docker container and do remote debugging as well on the container

- Install kamehouse-shell on the host to control the dev docker container as mentioned above

- Start a docker container in dev mode with the script `${HOME}/programs/kamehouse-shell/bin/kamehouse/docker/docker-run-kamehouse.sh -p dev -i (intelli|eclipse)`. The default value for -i is instellij

- Execute `${HOME}/programs/kamehouse-shell/bin/kamehouse/docker/docker-reinit-container-data-from-host.sh -s -p dev` to sync the ssh keys of the host to the container using default password `gohan`

- Connnect through ssh to the container using the script `${HOME}/programs/kamehouse-shell/bin/kamehouse/docker/docker-ssh-kamehouse.sh -p dev`. After syncing the keys it should login to the container without asking a password. Inside the container's console:
  - Deploy your changes using `deploy-kamehouse.sh -f`
  - Tail tomcat and apache httpd logs using the `tail-log.sh` script
  - Test kamehouse-shell and kamehouse-cmd inside the container (or deploy them on the host and test on the host too)

- Docker with dev profile will run with the directory `${HOME}/git/kamehouse` inside the container binded to the directory `${HOME}/workspace-(intellij|eclipse)/kamehouse` on the host. So all changes done in the host will be deployed on the container with the deployment script

- To remote debug tomcat running in the dev container from your ide, follow the above eclipse and intellij guides to setup remote debugging

- Changes made to the ui in kamehouse-ui and kamehouse-groot should be rendered automatically as well. Some changes like in /kame-house/admin pages served from tomcat need a kamehouse-ui redeployment with `deploy-kamehouse.sh -f -m ui` from the container's console

- Stop the dev docker container with the script `${HOME}/programs/kamehouse-shell/bin/kamehouse/docker/docker-stop-kamehouse.sh -p dev`

- By default docker dev runs standalone without controlling the host. So it won't control vlc running on the host or all the other commands that would normally be executed on the host. To enable kamehouse running in the container to control the host, add -c to the `docker-run-kamehouse.sh` script

- By default the dev container doesn't persist database or configuration files or ssh keys. To persist those in volumes between container restarts, run `docker-run-kamehouse.sh` also with -v

# VS Code:

* Create a vs code workspace and add either intellij or eclipse kamehouse folder
* To debug the frontend in vscode, use the chrome debugger launch configurations in .vscode/lauch.json
* There's 2 debugger launch configurations there, one for /kame-house-groot app and the other for /kame-house to debug the frontend in vscode and the backend in intellij: Run > Start Debugging or open the debugger tab to select which debugger to launch
* Create a symlink in kamehouse-ui/src/main: `mklink /D "kame-house" "webapp"` so that the vscode debugger picks up the files for /kame-house
* When setting the breakpoints to debug /kame-house, open the js files by browsing through kamehouse/kamehouse-ui/src/main/kame-house (through the symlink). Not by browsing through kamehouse/kamehouse-ui/src/main/webapp or they won't be bound
* When setting the breakpoints to debug /kame-house-groot, open the js files by browsing through kamehouse/kamehouse-groot/public/kame-house-groot
