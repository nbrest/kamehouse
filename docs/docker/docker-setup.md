| [Home](/README.md) | [Docs](/docs/README.md) |
---------------------------------------------------------------

*********************

# Docker Setup:

The project is hosted on [docker hub](https://hub.docker.com/repository/docker/nbrest/kamehouse).

The docker image loads kamehouse through tomcat and apache httpd and most of the functionality works out of the box.

The kamehouse docker container can be used stand alone or it can be used to control a remote host. When controlling a remote host:
- The remote host needs to have [kamehouse-shell](/kamehouse-shell/README.md) and [kamehouse-cmd](/kamehouse-cmd/README.md) installed
- The remote host doesn't need to have httpd or tomcat installed or any other kamehouse module other than shell and cmd
- The remote host needs to be reachable by `ssh` and `vnc` from the docker container 
- Most of the commands are sent from the container to the remote host via `ssh`
- For `vnc` commands on the remote host, the docker container first tries to send the request directly through `vnc` and fallsback to `ssh` if the vnc command fails
- If the remote host is a windows host, it needs to have pstools installed as described [here](/README.md) for certain commands to work
- Set the properties in `${HOME}/.kamehouse/kamehouse.cfg` to control a remote host and execute the docker run script with `-c`

## Install kamehouse shell scripts to control the kamehouse docker container

- Download the script [install-kamehouse.sh](/scripts/install-kamehouse.sh) from this git repo, then execute with -o parameter to copy only kamehouse-shell scripts, without affecting the current user's shell at all
```sh
chmod a+x install-kamehouse.sh ; ./install-kamehouse.sh -o
```
- Running the script with `-s` will install the full kamehouse-shell

- After installing kamehouse shell, follow the guide to install [kamehouse-cmd](/kamehouse-cmd/README.md) and create the encrypted password files as well, when using the docker container to control a remote host
- If you are using the container stand alone, kamehouse-cmd is not needed on the host. Only the kamehouse-shell scripts

*********************

## Pull the image from docker hub (optional)

You can skip this step and directly run the image. If it doesn't find it locally, it will download it automatically from docker hub. If you do have the image locally already, this will pull the latest changes to the image from docker hub

Execute the script `${HOME}/programs/kamehouse-shell/bin/kamehouse/docker/docker-pull-kamehouse.sh`

*********************

## Initial sample test users

- Login with the following `user:password` to test different functionality

```sh
seiya:ikki (admin)
ryoma:fuji (user)
vegeta:trunks (guest)
```

*********************

## Run the image

Execute the script `${HOME}/programs/kamehouse-shell/bin/kamehouse/docker/docker-run-kamehouse.sh -p demo` 

- Execute the script with `-h` to see all the profiles and options

*********************

## Login to the container

After that, once the init script finishes deploying kamehouse to tomcat in the container, you can access kamehouse at [https://localhost:12443/kame-house/](https://localhost:12443/kame-house/) or [http://localhost:12080/kame-house/](http://localhost:12080/kame-house/) and you can login with the users mentioned above

You can also access kamehouse groot at [https://localhost:12443/kame-house-groot/](https://localhost:12443/kame-house-groot/) or [http://localhost:12080/kame-house-groot/](http://localhost:12080/kame-house-groot/) and login to groot with the admin user mentioned above

You can also access the container through ssh using the script `${HOME}/programs/kamehouse-shell/bin/kamehouse/docker/docker-ssh-kamehouse.sh -p demo` default password `gohan` 

In the container console, you can run the following scripts:

- `tail-log.sh -f [kamehouse|tomcat|apache]` : tail the logs of the application
- `build-kamehouse.sh` : Execute it on `/home/goku/git/kamehouse` to build the project and run all the unit tests
- `build-kamehouse.sh -i -p docker` : Execute it on `/home/goku/git/kamehouse` to run all the integration tests
- `deploy-kamehouse.sh -p docker` : Pull the latest changes from git dev branch and deploy all the modules (Executed automatically during container startup)
- `deploy-kamehouse.sh -m shell` : Installs the latest version of kamehouse-shell
- `kamehouse-cmd.sh` : Test the functionality of kamehouse-cmd

### Deploy workspace changes on docker dev:

- In docker `dev` profile, `${HOME}/git/kamehouse` directory inside the docker container is mapped to the host's `${HOME}/workspace/kamehouse`
- Log in to docker `dev` through ssh, then run:
```sh
cd ${HOME}/git/kamehouse
deploy-kamehouse.sh -c
```

## Check the status

- Check the status of the docker containers, images and volumes in the system with the command `${HOME}/programs/kamehouse-shell/bin/kamehouse/docker/docker-status-kamehouse.sh`

```sh
docker container list
docker images
docker volume ls
```

*********************

## Stopping the container

Use the script `${HOME}/programs/kamehouse-shell/bin/kamehouse/docker/docker-stop-kamehouse.sh -p demo`

*********************

## Build the image manually

If for any reason you can't pull the image from docker hub, you can build it manually. At the root of the project there's a Dockerfile that can be used to build the image to run kamehouse in a container

At the root of the project execute the script `./kamehouse-shell/bin/kamehouse/docker/docker-build-kamehouse.sh`

You can then run the image as mentioned above 

*********************

## Sync ssh keys and configuration between host and container

- When running the script `docker-run-kamehouse.sh` with `-c`, the container is setup to execute certain commands in the host specified in `${HOME}/.kamehouse/kamehouse.cfg` 
- Commands such as starting and stopping vlc player, shutdown, reboot, suspend will run on the host
- By default in most profiles, the container is setup to execute the commands within the container only
- The commands executed on the host are done through `ssh` from the docker container to the host
- For those commands to be executed successfully, the ssh keys need to be synchronized between the host and the container. To do that, execute the script `docker-reinit-container.sh` **from the host** to resync the ssh keys and configuration files to the docker container. The container needs to be up and running to execute the reinit script from the host
- When running the reinit script, the console will show some sftp debugging messages and wait for the input of the docker container user password. Type `gohan` as the password to continue with the sftp sync
- After resyninc the ssh keys and configuration files, ssh to the docker container and re deploy kamehouse with `deploy-kamehouse.sh`. Then the docker container will be able to control the remote host

*********************

## Other useful docker scripts

- `docker-upgrade-containers.sh` pulls the latest version of kamehouse, stops the existing running containers and cleans up the old images
- `docker-cleanup-kamehouse.sh` cleans up old untagged kamehouse images
- `docker-server-key-remove.sh` removes an outdated key from the known hosts file so the ssh command doesn't fail next time
- `docker-ci-integration-tests-trigger.sh` starts a ci docker container and runs all integration tests inside it
- `docker-reinit-container.sh` re initializes the container data from the host or resets to inital docker setup. Also synchronizes ssh keys between host and container which is necessary to execute passwordless ssh commands on the host from the webapps in the container when using the docker container to control a remote host
