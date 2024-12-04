| [Home](/README.md) | [Docs](/docs/README.md) |
---------------------------------------------------------------

*********************

# Docker Setup:

The project is hosted on [docker hub](https://hub.docker.com/repository/docker/nbrest/kamehouse).

The docker image loads kamehouse through tomcat and apache httpd and most of the functionality works out of the box.

The kamehouse docker container can be used stand alone or it can be used to control a remote host that has kamehouse shell, groot and cmd installed. 
- The remote host doesn't need to have tomcat up and running and needs to be reachable by `http` and `ssh` from the docker container
- Most of the commands are sent from the container to the remote host via ssh. A few are sent over http

## Install kamehouse shell scripts to control docker (optional)

- Download the script [install-kamehouse.sh](/scripts/install-kamehouse.sh) from this git repo, then execute with -o parameter to install only kamehouse-shell scripts, without affecting the shell
```sh
chmod a+x install-kamehouse.sh ; ./install-kamehouse.sh -o
```
- Running the script with -s will install the full kamehouse-shell

- Follow the guides to install kamehouse cmd and groot as well on the host (optional: if using docker to control a remote host)

*********************

## Pull the image from docker hub (optional)

You can skip this step and directly run the image. If it doesn't find it locally, it will download it automatically from docker hub. If you do have the image locally already, this will pull the latest changes to the image from docker hub

Execute the script `${HOME}/programs/kamehouse-shell/bin/kamehouse/docker/docker-pull-kamehouse.sh`

```
docker pull nbrest/kamehouse:latest
```

*********************

## Initial sample test users

- Login with the following `user:password` to test different functionality

```
seiya:ikki (admin)
ryoma:fuji (user)
vegeta:trunks (guest)
```

*********************

## Run the image

Execute the script `${HOME}/programs/kamehouse-shell/bin/kamehouse/docker/docker-run-kamehouse.sh -p demo` or run manually with the command:
```sh
docker run --rm -h kamehouse-docker-demo \
  --env BUILD_ON_STARTUP=true \
  --env DEBUG_MODE=false \
  --env DOCKER_CONTROL_HOST=false \
  --env DOCKER_PORT_HTTP=12080 \
  --env DOCKER_PORT_HTTPS=12443 \
  --env DOCKER_PORT_TOMCAT_DEBUG=12000 \
  --env DOCKER_PORT_TOMCAT=12090 \
  --env DOCKER_PORT_MARIADB=12306 \
  --env DOCKER_PORT_SSH=12022 \
  --env IS_DOCKER_CONTAINER=false \
  --env PROFILE=demo \
  --env USE_VOLUMES=false \
  -p 12022:22 \
  -p 12080:80 \
  -p 12443:443 \
  -p 12000:8000 \
  -p 12090:9090 \
  -p 12306:3306 \
  nbrest/kamehouse:latest
```

- Execute the script with `-h` to see all the profiles and options

With the parameter `--rm` the container will be removed automatically after it exits. Without it, it will remain in your system.

Passing `--env BUILD_ON_STARTUP=true` to `docker run` pulls and deploys the latest version of kamehouse during the container startup. By default it doesn't do either. If skipped, the container will start with the version of kamehouse that was used when the image was built. You can always update to the latest version once the container is started with the deployment script mentioned below.

*********************

## Login to the container

After that, once the init script finishes deploying kamehouse to tomcat in the container, you can access kamehouse at [https://localhost:12443/kame-house/](https://localhost:12443/kame-house/) or [http://localhost:12080/kame-house/](http://localhost:12080/kame-house/) and you can login with the users mentioned above

You can also access kamehouse groot at [https://localhost:12443/kame-house-groot/](https://localhost:12443/kame-house-groot/) or [http://localhost:12080/kame-house-groot/](http://localhost:12080/kame-house-groot/) and login to groot with the admin user mentioned above

You can also access the container through ssh using the script `${HOME}/programs/kamehouse-shell/bin/kamehouse/docker/docker-ssh-kamehouse.sh -p demo` default password `gohan` or manually with the command

```sh
ssh -p 12022 goku@localhost
``` 

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

Or check the running containers with the command: 

```sh
docker container list
```

Stop the kamehouse container with 

```
docker stop container-id-hash
```

*********************

## Build the image manually

If for any reason you can't pull the image from docker hub, you can build it manually. At the root of the project there's a Dockerfile that can be used to build the image to run kamehouse in a container

At the root of the project execute the script `./kamehouse-shell/bin/kamehouse/docker/docker-build-kamehouse.sh`

```
docker build --build-arg DOCKER_IMAGE_BASE=ubuntu:22.04 -t nbrest/kamehouse:latest .
```

You can then run the image as mentioned above either with temporary or permanent container.


*********************

## Sync ssh keys and configuration between host and container

- When running the script `docker-run-kamehouse.sh` with `-c`, the container is setup to execute certain commands in the host. Such as starting and stopping vlc player, shutdown, reboot, suspend and others. By default in most profiles, the container is setup to execute the commands within the container
- Those commands are executed through ssh from the docker container to the host. For those commands to be executed successfully, the ssh keys need to be synchronized between the host and the container. To do that, execute the script `docker-reinit-container.sh` from the host to resync the ssh keys and configuration files to the docker container

*********************

## Other useful docker scripts

- `docker-upgrade-containers.sh` pulls the latest version of kamehouse, stops the existing running containers and cleans up the old images
- `docker-cleanup-kamehouse.sh` cleans up old untagged kamehouse images
- `docker-server-key-remove.sh` removes an outdated key from the known hosts file so the ssh command doesn't fail next time
- `docker-ci-integration-tests-trigger.sh` starts a ci docker container and runs all integration tests inside it
- `docker-reinit-container.sh` re initializes the container data from the host or resets to inital docker setup. Also synchronizes ssh keys between host and container which is necessary to execute passwordless ssh commands on the host from the webapps in the container when using the docker container to control a remote host
