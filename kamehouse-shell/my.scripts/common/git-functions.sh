#######################################################################
# This file should be imported through common-functions, not directly #
#######################################################################
# Git functions to call from other scripts

# cd to git project dir and check for errors
cdProjectDir() {
  local PROJECT_DIR=$1
  cd ${PROJECT_DIR}
  checkCommandStatus "$?" "Invalid project directory"
}

gitCheckout() {
  local BRANCH=$1
  log.info "Checking out git branch ${BRANCH}"
  git checkout ${BRANCH}
  checkCommandStatus "$?" "An error occurred checking out ${BRANCH} branch"
}

gitPull() {
  local REMOTE=$1
  local BRANCH=$2  
  git checkout ${BRANCH}
  checkCommandStatus "$?" "An error occurred checking out ${BRANCH} branch"

  log.info "Pulling from git branch ${BRANCH}"
  git pull ${REMOTE} ${BRANCH}
}

gitPullWithRetry() {
  local REMOTE=$1
  local BRANCH=$2
  executeWithRetry "gitPull ${REMOTE} ${BRANCH}"
}

# cd to the project dir and checkout and pull from the remote branch
gitCdCheckoutAndPull() {
  local PROJECT_DIR=$1
  local REMOTE=$2
  local BRANCH=$3
  cdProjectDir ${PROJECT_DIR}
  gitCheckout ${BRANCH}
  gitPullWithRetry ${REMOTE} ${BRANCH}
}

gitPush() {
  local REMOTE=$1
  local BRANCH=$2
  log.info "Pushing changes to ${REMOTE} ${BRANCH}"
  git push ${REMOTE} ${BRANCH}
}

gitPushWithRetry() {
  local REMOTE=$1
  local BRANCH=$2
  executeWithRetry "gitPush ${REMOTE} ${BRANCH}" "gitPull ${REMOTE} ${BRANCH}"
  log.info "Finished pushing changes to git"
}

# Add and commit all changes in the current branch
gitCommitAllChanges() {
  local MESSAGE=$1
  log.info "Displaying git status before adding changes"
  git status
  
  log.info "Adding changes to git"	
  git add *
  checkCommandStatus "$?" "An error occurred adding changes to git"

  log.info "Displaying git status after adding changes"
  git status
  
  log.info "Committing changes to git"
  git commit -m "${MESSAGE}"
  checkCommandStatus "$?" "An error occurred commiting changes"
}

# Add and commit all the changes and push them to the remote
gitCommitAllChangesAndPush() {
  local REMOTE=$1
  local BRANCH=$2
  local MESSAGE=$3
  gitCommitAllChanges "${MESSAGE}"
  gitPushWithRetry ${REMOTE} ${BRANCH}
}

# Add and commit all the changes and push them to the remote
gitCdCommitAllChangesAndPush() {
  local PROJECT_DIR=$1
  local REMOTE=$2
  local BRANCH=$3
  local MESSAGE=$4
  cdProjectDir ${PROJECT_DIR}
  gitCommitAllChanges "${MESSAGE}"
  gitPushWithRetry ${REMOTE} ${BRANCH}
}