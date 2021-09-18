#!/bin/bash

REPOSITORY_NAME="java.web.kamehouse"

###########
# SSH all #
###########

### Create the remote named all with fetch from the bitbucket repo
git remote remove all
git remote add all git@bitbucket.org:nbrest/${REPOSITORY_NAME}.git

### Add the bitbucket repo to the remote all push
git remote set-url --add --push all git@bitbucket.org:nbrest/${REPOSITORY_NAME}.git

### Add the github repo to the remote all push
git remote set-url --add --push all git@github.com:nbrest/${REPOSITORY_NAME}.git

#######
# SSH #
#######

### Create a remote named bitbucket to push using ssh to github
git remote remove bitbucket-ssh
git remote add bitbucket-ssh git@bitbucket.org:nbrest/${REPOSITORY_NAME}.git

### Create a remote named githubssh to push using ssh to github
git remote remove github-ssh
git remote add github-ssh git@github.com:nbrest/${REPOSITORY_NAME}.git

#########
# HTTPS #
#########

### Create a remote named bitbucket to be able to push only to bitbucket
git remote remove bitbucket-https
git remote add bitbucket-https https://nbrest@bitbucket.org/nbrest/${REPOSITORY_NAME}.git

### Create a remote named github to be able to push only to github
git remote remove github-https
git remote add github-https https://nbrest@github.com/nbrest/${REPOSITORY_NAME}.git

### list all the remotes
git remote -v 

