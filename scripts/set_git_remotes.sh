#!/bin/bash

REPOSITORY_NAME="java.web.mobile.inspections"

#############
# HTTPS all #
#############

### Create the remote named all with fetch from the bitbucket repo
git remote remove all
git remote add all https://nbrest@bitbucket.org/nbrest/${REPOSITORY_NAME}.git
#git remote add all https://nbrest@github.com/nbrest/${REPOSITORY_NAME}.git

### Add the bitbucket repo to the remote all push
git remote set-url --add --push all https://nbrest@bitbucket.org/nbrest/${REPOSITORY_NAME}.git

### Add the github repo to the remote all push
git remote set-url --add --push all https://nbrest@github.com/nbrest/${REPOSITORY_NAME}.git

### Add the nicobrest repo to the remote all push
#git remote set-url --add --push all https://nbrest@www.nicobrest.com.ar/git/${REPOSITORY_NAME}.git

#########
# HTTPS #
#########

### Create a remote named bitbucket to be able to push only to bitbucket
git remote remove bitbucket
git remote add bitbucket https://nbrest@bitbucket.org/nbrest/${REPOSITORY_NAME}.git

### Create a remote named github to be able to push only to github
git remote remove github
git remote add github https://nbrest@github.com/nbrest/${REPOSITORY_NAME}.git

### Create a remote named nicobrest to be able to push only to my private repo
#git remote remove nicobrest
#git remote add nicobrest https://nbrest@www.nicobrest.com.ar/git/${REPOSITORY_NAME}.git

#######
# SSH #
#######

### Create a remote named bitbucket to push using ssh to github
git remote remove bitbucketssh
git remote add bitbucketssh git@bitbucket.org:nbrest/${REPOSITORY_NAME}.git

### Create a remote named githubssh to push using ssh to github
git remote remove githubssh
git remote add githubssh git@github.com:nbrest/${REPOSITORY_NAME}.git

### Create a remote to push using ssh inside my local network without internet access
#git remote remove localssh
#git remote add localssh ssh://nbrest@niko-w/git/${REPOSITORY_NAME}.git

### list all the remotes
git remote -v 

