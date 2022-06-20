# Eclipse:

- Follow [dev-environment-setup-eclipse.md](dev-environment-setup-eclipse.md) 

# IntelliJ:

- Follow [dev-environment-setup-intellij.md](dev-environment-setup-intellij.md) 

# Tomcat Dev:

* Download tomcat from apache's website and extract it to *$HOME/programs/apache-tomcat-dev*
* Use the sample configuration in the folder `local-setup/tomcat-dev` to update the tomcat port and manager users

# Docker Dev:

- Start a docker container in dev mode with the script `${HOME}/programs/kamehouse-shell/bin/kamehouse/docker/docker-run-kamehouse.sh -p dev` to debug a tomcat server running inside the container

- Execute `${HOME}/programs/kamehouse-shell/bin/kamehouse/docker/docker-reinit-container-data-from-host.sh -p dev -i (intelli|eclipse)` to sync the ssh keys of the host to the container and reinit container data using default password `gohan`

- Connnect through ssh to the container using the script `${HOME}/programs/kamehouse-shell/bin/kamehouse/docker/docker-ssh-kamehouse.sh -p dev`. After syncing the keys it should login to the container without asking a password to deploy your changes and tail the application logs
  - Deploy your changes using `deploy-kamehouse.sh -f`
  - Tail tomcat and apache httpd logs using the `tail-log.sh` script
  - Test kamehouse-shell and kamehouse-cmd inside the container

- Docker with dev profile will run with the directory `${HOME}/git/kamehouse` inside the container binded to the directory `${HOME}/workspace-(intellij|eclipse)/kamehouse` on the host. So all changes done in the host will be deployed on the container with the deployment script

# Apache Httpd:

- Follow [installation-apache.md](installation-apache.md) guide to install apache 
- Follow [dev-environment-setup-apache.md](dev-environment-setup-apache.md) to configure apache for intellij or eclipse dev

# VS Code:

* Create a vs code workspace and add either intellij or eclipse kamehouse folder
* To debug the frontend in vscode, use the chrome debugger launch configurations in .vscode/lauch.json
* There's 2 debugger launch configurations there, one for /kame-house-groot app and the other for /kame-house to debug the frontend in vscode and the backend in intellij: Run > Start Debugging or open the debugger tab to select which debugger to launch
* Create a symlink in kamehouse-ui/src/main: `mklink /D "kame-house" "webapp"` so that the vscode debugger picks up the files for /kame-house
* When setting the breakpoints to debug /kame-house, open the js files by browsing through kamehouse/kamehouse-ui/src/main/kame-house (through the symlink). Not by browsing through kamehouse/kamehouse-ui/src/main/webapp or they won't be bound
* When setting the breakpoints to debug /kame-house-groot, open the js files by browsing through kamehouse/kamehouse-groot/public/kame-house-groot
