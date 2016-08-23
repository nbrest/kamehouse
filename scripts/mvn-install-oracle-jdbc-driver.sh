#!/bin/bash

# Script to install the oracle jdbc driver into the local repository.
# Run from the root of the project. This file should be on ./scripts and
# the oracle driver should be on ./lib

mvn install:install-file -DgroupId=com.oracle -DartifactId=ojdbc6 \
     -Dversion=11.2.0.4 -Dpackaging=jar -Dfile=lib/ojdbc6.jar -DgeneratePom=true
