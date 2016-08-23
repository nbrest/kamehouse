#!/bin/bash
####################################################
### REPOSITORY NAME: java.web.mobile.inspections ###
####################################################

### Create the remote named all with fetch from the github repo
git remote remove all
git remote add all https://github.com/nbrest/java.web.mobile.inspections.git

### Add the nicobrest repo to the remote all push
git remote set-url --add --push all https://www.nicobrest.com.ar/git/java.web.mobile.inspections.git

### Add the github repo to the remote all push
git remote set-url --add --push all https://github.com/nbrest/java.web.mobile.inspections.git

### Create a remote named github to be able to push only to github
git remote remove github
git remote add github https://github.com/nbrest/java.web.mobile.inspections.git

### Create a remote named nicobrest to be able to push only to my private repo
git remote remove nicobrest
git remote add nicobrest https://www.nicobrest.com.ar/git/java.web.mobile.inspections.git

### Create a remote named githubssh to push using ssh to github
git remote remove githubssh
git remote add githubssh git@github.com:nbrest/java.web.mobile.inspections.git

### Create a remote to push using ssh inside my local network without internet access
git remote remove localssh
git remote add localssh ssh://nbrest@niko-w/git/java.web.mobile.inspections.git

### list all the remotes
git remote -v 

