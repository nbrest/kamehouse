# Docker:

The project is hosted on [docker hub](https://hub.docker.com/repository/docker/nbrest/java.web.kamehouse).

The docker image loads kamehouse through tomcat and apache httpd and most of the functionality works out of the box.

*********************

## Pull the image from docker hub (optional)

You can skip this step and directly run the image. If it doesn't find it locally, it will download it automatically from docker hub. If you do have the image locally already, this will pull the latest changes to the image from docker hub

Execute the script `docker/scripts/docker-pull-java-web-kamehouse.sh`

```
docker pull nbrest/java.web.kamehouse:latest
```

*********************

## Run the image

Execute the script `docker/scripts/docker-run-java-web-kamehouse.sh`

```
docker run --rm -p 6022:22 -p 6080:80 -p 6443:443 -p 6090:9090 nbrest/java.web.kamehouse:latest
```

With the parameter `--rm` the container will be removed automatically after it exits. Without it, it will remain in your system.

Passing `--env FAST_DOCKER_INIT=true` to `docker run` skips pulling and deploying the latest version of kamehouse during the container startup. By default it does both. If skipped, the container will start with the version of kamehouse that was used when the image was built. You can always update to the latest version once the container is started with the deployment script mentioned below.

After that, once the init script finishes deploying kamehouse to tomcat in the container, you can access kamehouse at [https://localhost:6443/kame-house/](https://localhost:6443/kame-house/) or [http://localhost:6080/kame-house/](http://localhost:6080/kame-house/) and you can login with the following user:password to test different functionality
- admin:admin
- user:user
- guest:guest

You can also access kamehouse groot at [https://localhost:6443/kame-house-groot/](https://localhost:6443/kame-house-groot/) or [http://localhost:6080/kame-house-groot/](http://localhost:6080/kame-house-groot/) and login with admin:admin to groot

You can also access the container through ssh at `ssh -p 6022 nbrest@localhost` with the default password `nbrest`

In the container console, you can run the following scripts:

- `tail-log.sh -f [kamehouse|tomcat|apache]` : tail the logs of the application
- `build-java-web-kamehouse.sh` : Execute it on `/home/nbrest/git/java.web.kamehouse` to build the project and run all the unit tests
- `build-java-web-kamehouse.sh -i -p docker` : Execute it on `/home/nbrest/git/java.web.kamehouse` to run all the integration tests
- `deploy-java-web-kamehouse.sh -f -p docker` : Pull the latest changes from git dev branch and deploy them (Executed automatically during container startup)
- `docker-my-scripts-update.sh` : Updates the scripts in `/home/nbrest/my.scripts` with the version currently pulled from `/home/nbrest/git/java.web.kamehouse`
- `kamehouse-cmd.sh` : Test the functionality of kamehouse-cmd

*********************

## Stopping the container

Check the running containers with the command: 

```
docker container list
```

Stop the kamehouse container with 

```
docker stop container-id-hash
```

*********************

## Build the image manually

If for any reason you can't pull the image from docker hub, you can build it manually. At the root of the project there's a Dockerfile that can be used to build the image to run kamehouse in a container

At the root of the project execute the script `docker/scripts/docker-build-java-web-kamehouse.sh`

```
docker build -t nbrest/java.web.kamehouse:latest .
```

You can then run the image as mentioned above either with temporary or permanent container.