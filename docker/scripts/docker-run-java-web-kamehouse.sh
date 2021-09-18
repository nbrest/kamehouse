#!/bin/bash

echo "Running image nbrest/java.web.kamehouse:latest. The container will be removed when it exits"

docker run --rm -p 6022:22 -p 6080:80 -p 6443:443 -p 6090:9090 nbrest/java.web.kamehouse:latest
