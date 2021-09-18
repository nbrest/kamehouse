#!/bin/bash

echo "Recreating docker container kamehouse-docker from image nbrest/java.web.kamehouse:latest"
docker container rm kamehouse-docker
docker create --name kamehouse-docker -p 6022:22 -p 6080:80 -p 6443:443 -p 6090:9090 nbrest/java.web.kamehouse
