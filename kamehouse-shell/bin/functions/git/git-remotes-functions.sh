
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
    exitProcess ${EXIT_VAR_NOT_SET}
  fi
}

removeRemotes() {
  log.info "Removing remotes"
  git remote remove all
  git remote remove origin
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
    git remote add origin git@github.com:nbrest/${REPOSITORY_NAME}.git
  else
    git remote add origin git@bitbucket.org:nbrest/${REPOSITORY_NAME}.git
  fi
}

listRemotes() {
  log.info "Listing remotes"
  git remote -v 
}
