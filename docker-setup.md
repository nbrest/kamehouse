# Docker:

The project is hosted on [docker hub](https://hub.docker.com/repository/docker/nbrest/java.web.kamehouse).

The docker image loads kamehouse through tomcat and apache httpd and most of the functionality works out of the box.

*********************

## Pull the image from docker hub

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

After that you can access kamehouse at [https://localhost:6443/kame-house/](https://localhost:6443/kame-house/) or [http://localhost:6080/kame-house/](http://localhost:6080/kame-house/) and you can login with the following user:password to test different functionality
- admin:admin
- user:user
- guest:guest

You can also access kamehouse groot at [https://localhost:6443/kame-house-groot/](https://localhost:6443/kame-house-groot/) or [http://localhost:6080/kame-house-groot/](http://localhost:6080/kame-house-groot/) and login with admin:admin to groot

You can also access the container through ssh at `ssh -p 6022 root@localhost` with the default password `change-me`

In the container console, you can run the following scripts:

- `tail-log.sh -f [kamehouse|tomcat|apache]` : tail the logs of the application
- `build-java-web-kamehouse.sh` : Execute it on `/root/git/java.web.kamehouse` to build the project and run all the unit tests
- `build-java-web-kamehouse.sh -i -p docker` : Execute it on `/root/git/java.web.kamehouse` to run all the integration tests
- `deploy-java-web-kamehouse.sh -f -p docker` : Pull the latest changes from git dev branch and deploy them
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
docker build -t nbrest/java.web.kamehouse .
```

You can then run the image as mentioned above either with temporary or permanent container.