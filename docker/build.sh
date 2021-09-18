#!/bin/bash

echo "Building docker image java.web.kamehouse"
docker build -t java.web.kamehouse .

echo "Recreating docker container java.web.kamehouse"
docker container rm java.web.kamehouse
docker create --name java.web.kamehouse -p 6022:22 -p 6080:80 -p 6443:443 -p 6090:9090 java.web.kamehouse
