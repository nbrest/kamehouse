#!/bin/bash

echo "Building docker image java.web.kamehouse"
docker build -t nbrest/java.web.kamehouse .

echo "Recreating docker container java.web.kamehouse"
docker container rm kamehouse-docker
docker create --name kamehouse-docker -p 6022:22 -p 6080:80 -p 6443:443 -p 6090:9090 nbrest/java.web.kamehouse
