#!/bin/bash

# Config in ci to run as: /home/nbrest/my.scripts/kamehouse/ci/ci-post-build.sh (no sudo)

echo "Running ci-post-build.sh script"

/home/nbrest/my.scripts/kamehouse/build-java-web-kamehouse.sh -i -p ci

