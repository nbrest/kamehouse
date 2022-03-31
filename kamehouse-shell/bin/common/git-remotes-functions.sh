
# Global variables
REPOSITORY_NAME=""
IS_GITHUB_REPOSITORY=true

GITHUB_HTTPS_USER=nbrest
BITBUCKET_HTTPS_USER=nbrest

mainProcess() {
  checkVariables
  removeRemotes
  addRemotes
  listRemotes
}

checkVariables() {
  if [ -z "${REPOSITORY_NAME}" ]; then
    log.error "REPOSITORY_NAME not set"
    exit 1
  fi
}

removeRemotes() {
  log.info "Removing remotes"
  
  # All/Origin
  git remote remove all
  git remote remove origin

  # Bitbucket
  git remote remove bitbucket-ssh
  git remote remove bitbucket-https
  
  # Github
  git remote remove github-ssh
  git remote remove github-https

  ### Legacy remotes:
  git remote remove bitbucket
  git remote remove bitbucketssh
  git remote remove github
  git remote remove githubssh
}

addRemotes() {
  log.info "Adding remotes"

  # All (fetches from bitbucket, pushes to both bitbucket and github)
  git remote add all git@bitbucket.org:nbrest/${REPOSITORY_NAME}.git
  git remote set-url --add --push all git@bitbucket.org:nbrest/${REPOSITORY_NAME}.git
  if ${IS_GITHUB_REPOSITORY}; then 
    git remote set-url --add --push all git@github.com:nbrest/${REPOSITORY_NAME}.git
  fi

  # Origin
  if ${IS_GITHUB_REPOSITORY}; then 
    git remote add origin https://github.com/nbrest/${REPOSITORY_NAME}.git
  else
    git remote add origin https://bitbucket.org/nbrest/${REPOSITORY_NAME}.git
  fi

  # Bitbucket
  git remote add bitbucket-ssh git@bitbucket.org:nbrest/${REPOSITORY_NAME}.git
  git remote add bitbucket-https https://${BITBUCKET_HTTPS_USER}@bitbucket.org/nbrest/${REPOSITORY_NAME}.git

  # Github
  if ${IS_GITHUB_REPOSITORY}; then 
    git remote add github-ssh git@github.com:nbrest/${REPOSITORY_NAME}.git
    git remote add github-https https://${GITHUB_HTTPS_USER}@github.com/nbrest/${REPOSITORY_NAME}.git
  fi
}

listRemotes() {
  log.info "Listing remotes"
  git remote -v 
}
