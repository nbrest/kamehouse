#!/bin/bash

echo "This command is deprecated. Look at the comments in the source to install new dependencies."
exit

# Use the following mvn deploy command to add required jars to /local-maven-repo. ojdbc6 is already there 
# so I don't need it, but if I ever need a new dependency that it's not in the official maven repos, add 
# it like this to a local repo in the project, so it can be used in CI servers like travis

# mvn deploy:deploy-file -DgroupId=com.oracle -DartifactId=ojdbc6 -Dversion=11.2.0.4 -Durl=file:./local-maven-repo/ -DrepositoryId=local-maven-repo -DupdateReleaseInfo=true -Dfile=./lib/ojdbc6.jar

#**************************************************************************************************#
# DEPRECATED as it doesn't work when trying to build the project in continuous integration servers #
#**************************************************************************************************#

# Script to install the oracle jdbc driver into the local repository.
# Run from the root of the project. This file should be on ./scripts and
# the oracle driver should be on ./lib

# mvn install:install-file -DgroupId=com.oracle -DartifactId=ojdbc6 \
#     -Dversion=11.2.0.4 -Dpackaging=jar -Dfile=lib/ojdbc6.jar -DgeneratePom=true
