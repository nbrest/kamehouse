#!/bin/bash

# Config in ci to run as:  sudo -u nbrest /home/nbrest/my.scripts/kamehouse/ci/ci-pre-build.sh

echo "Running ci-pre-build.sh script"

/home/nbrest/my.scripts/lin/kamehouse/tomcat-stop.sh
/home/nbrest/my.scripts/kamehouse/deploy-java-web-kamehouse.sh -f -p ci
/home/nbrest/my.scripts/kamehouse/tomcat-startup.sh

