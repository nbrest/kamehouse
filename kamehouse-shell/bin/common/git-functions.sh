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
  local GIT_COMMIT_OUT=""
  local GIT_COMMIT_RESULT=""
  local NO_CHANGES_TO_COMMIT="nothing to commit, working tree clean"
  log.info "Displaying git status before adding changes"
  git status
  
  log.info "Adding changes to git"	
  git add *
  checkCommandStatus "$?" "An error occurred adding changes to git"

  log.info "Displaying git status after adding changes"
  git status
  
  log.info "Committing changes to git with message: ${MESSAGE}"
  GIT_COMMIT_OUT=`git commit -m "${MESSAGE}"`
  GIT_COMMIT_RESULT=$?
  echo "${GIT_COMMIT_OUT}"
  if [[ "${GIT_COMMIT_OUT}" =~ ${NO_CHANGES_TO_COMMIT} ]]; then
    log.warn "No changes to commit on git"
  else
    checkCommandStatus "${GIT_COMMIT_RESULT}" "An error occurred commiting changes"
  fi
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

# Display build version and date of the specified repo
displayBuildVersionAndDate() {
  local PROJECT_DIR=$1
  cd ${PROJECT_DIR}

  local KAMEHOUSE_RELEASE_VERSION=`grep -e "<version>.*1-KAMEHOUSE-SNAPSHOT</version>" pom.xml | awk '{print $1}'`
  KAMEHOUSE_RELEASE_VERSION=`echo ${KAMEHOUSE_RELEASE_VERSION:9:6}`

  local BUILD_VERSION=`git log | head -n 3 | grep commit | cut -c 8-`
  BUILD_VERSION=${BUILD_VERSION:0:8}
  if [ -n "${KAMEHOUSE_RELEASE_VERSION}" ]; then
    BUILD_VERSION=${KAMEHOUSE_RELEASE_VERSION}"-"${BUILD_VERSION}
  fi
  echo "buildVersion=${BUILD_VERSION}"

  local BUILD_DATE=`git log | head -n 3 | grep Date | cut -c 9-`
  BUILD_DATE=${BUILD_DATE:0:25}
  BUILD_DATE=`date -d"$BUILD_DATE" +%Y-%m-%d' '%H:%M:%S`
  echo "buildDate=${BUILD_DATE}"
}
