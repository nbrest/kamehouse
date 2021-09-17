#!/bin/bash

echo "Building docker image java.web.kamehouse"
docker build -t java.web.kamehouse .

echo "Recreating docker container java.web.kamehouse"
docker container rm java.web.kamehouse
docker create --name java.web.kamehouse -p 10080:80 -p 10443:443 -p 13306:3306 -p 18080:8080 -p 19090:9090 java.web.kamehouse
