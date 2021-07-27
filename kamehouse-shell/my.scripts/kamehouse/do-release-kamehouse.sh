#!/bin/bash

# Import common functions
source ${HOME}/my.scripts/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 1
fi

# Import kamehouse functions
source ${HOME}/my.scripts/common/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing kamehouse-functions.sh\033[0;39m"
  exit 1
fi

declare -a VALID_WORKING_DIRS=("${HOME}/workspace-eclipse/${PROJECT}" "${HOME}/workspace-intellij/${PROJECT}")
CURRENT_BRANCH=""
RELEASE_VERSION=""
RELEASE_VERSION_RX=^[0-9]\.[0-9]{2}$
RELEASE_BRANCH="dev"
REQUEST_CONFIRMATION_RX=^yes\|y$

mainProcess() {
  validateCurrentWorkingDirectory
  stashChangesInReleaseBranch
  displayGitStatus
  getPreviousReleaseVersion
  requestConfirmation
  checkCurrentReleaseBranch
  gitMergeMasterToReleaseBranch
  checkUncommitedChangesInPomXml
  updateReleaseVersionInPomXml
  gitCommitPomUpdate 
  gitCheckoutMaster
  gitMergeReleaseBranchToMaster
  pushMasterBranch
  tagRelease
  pushTag
  gitCheckoutReleaseBranch
  pushReleaseBranch
  reapplyStashedChangesInReleaseBranch
}

validateCurrentWorkingDirectory() {
  CURREND_DIR=`pwd`
  IS_CURRENT_DIR_VALID=false
  for VALID_WORKING_DIR in ${VALID_WORKING_DIRS[@]}; do
    if [ "${CURREND_DIR}" == "${VALID_WORKING_DIR}" ]; then
      IS_CURRENT_DIR_VALID=true
    fi
  done
  if ${IS_CURRENT_DIR_VALID}; then
    : # Do nothing
  else
    log.error "${CURREND_DIR} is not a valid working directory for ${PROJECT}"
    exitProcess 1
  fi
}

stashChangesInReleaseBranch() {
  log.info "Stashing changes in ${RELEASE_BRANCH}"
  git stash
}

displayGitStatus() {
  log.info "Displaying git status"
  git status
}

getPreviousReleaseVersion() {
  PREVIOUS_RELEASE_VERSION=`grep -e "<version>.*1-KAMEHOUSE-SNAPSHOT</version>" pom.xml | awk '{print $1}'`
  PREVIOUS_RELEASE_VERSION=`echo ${PREVIOUS_RELEASE_VERSION:9:4}`
}

requestConfirmation() {
  log.info "Current directory: ${COL_PURPLE}`pwd`"
  log.info "Previous release version: ${COL_PURPLE}${PREVIOUS_RELEASE_VERSION}"
  log.info "Release version ${COL_PURPLE}${RELEASE_VERSION}"
  log.info "Do you want to proceed with the release? (${COL_BLUE}Yes${COL_DEFAULT_LOG}/${COL_RED}No${COL_DEFAULT_LOG}): "
  read SHOULD_PROCEED
  SHOULD_PROCEED=`echo "${SHOULD_PROCEED}" | tr '[:upper:]' '[:lower:]'`
  if [[ "${SHOULD_PROCEED}" =~ ${REQUEST_CONFIRMATION_RX} ]]; then
    log.info "Proceeding with the release"
  else
    log.warn "${COL_PURPLE}${SCRIPT_NAME}${COL_DEFAULT_LOG} cancelled by the user"
    exitProcess 2
  fi
}

checkCurrentReleaseBranch() {
  local CURRENT_BRANCH=`git branch | grep "*" | awk '{print $2}'`
  if [ "${CURRENT_BRANCH}" != "${RELEASE_BRANCH}" ]; then
    log.error "Current branch ${CURRENT_BRANCH} is differrent to release branch ${RELEASE_BRANCH}"
    exitProcess 1
  fi
}

gitMergeMasterToReleaseBranch() {
  log.info "Merging master branch to current branch ${CURRENT_BRANCH}"
  git merge master
  checkCommandStatus "$?"
}

checkUncommitedChangesInPomXml() {
  log.info "Checking pom.xml for uncommited changes"
  git status | grep "pom.xml" &>/dev/null
  local POM_MODIFIED_STATUS=$?
  if [ "${POM_MODIFIED_STATUS}" == "0" ]; then
    log.error "pom.xml has uncommited changes. Check them with 'git diff'. Commit or revert the changes before doing the release"
    exitProcess 1
  fi
}

updateReleaseVersionInPomXml() {
  local PREVIOUS_VERSION_IN_POM="<version>${PREVIOUS_RELEASE_VERSION}.1-KAMEHOUSE-SNAPSHOT</version>"
  local RELEASE_VERSION_IN_POM="<version>${RELEASE_VERSION}.1-KAMEHOUSE-SNAPSHOT</version>"

  local PREVIOUS_KAMEHOUSE_VERSION_PROPERTY="<kamehouse.version>${PREVIOUS_RELEASE_VERSION}.1-KAMEHOUSE-SNAPSHOT</kamehouse.version>"
  local RELEASE_KAMEHOUSE_VERSION_PROPERTY="<kamehouse.version>${RELEASE_VERSION}.1-KAMEHOUSE-SNAPSHOT</kamehouse.version>"

  log.info "Updating release version in pom.xml"
  sed -i "s+${PREVIOUS_VERSION_IN_POM}+${RELEASE_VERSION_IN_POM}+g" pom.xml
  sed -i "s+${PREVIOUS_KAMEHOUSE_VERSION_PROPERTY}+${RELEASE_KAMEHOUSE_VERSION_PROPERTY}+g" pom.xml

  log.info "Updating release version in modules"
  local KAMEHOUSE_MODULES=`ls -1 | grep kamehouse-`
  echo -e "${KAMEHOUSE_MODULES}" | while read KAMEHOUSE_MODULE; do
    sed -i "s+${PREVIOUS_VERSION_IN_POM}+${RELEASE_VERSION_IN_POM}+g" ${KAMEHOUSE_MODULE}/pom.xml      
  done
}

gitCommitPomUpdate() {
  log.info "Commiting changes to pom.xml in the root and modules"
  
  git add pom.xml
  checkCommandStatus "$?"

  local KAMEHOUSE_MODULES=`ls -1 | grep kamehouse-`
  echo -e "${KAMEHOUSE_MODULES}" | while read KAMEHOUSE_MODULE; do
    git add ${KAMEHOUSE_MODULE}/pom.xml
    checkCommandStatus "$?"
  done

  git commit -m "Updated pom.xml for release v${RELEASE_VERSION}"
  #checkCommandStatus "$?"
}

gitCheckoutMaster() {
  log.info "Switching to master branch"
  git checkout master
  checkCommandStatus "$?"
  git pull all master
  checkCommandStatus "$?"  
}

gitMergeReleaseBranchToMaster() {
  log.info "Merging ${RELEASE_BRANCH} branch to master"
  git merge ${RELEASE_BRANCH}
  checkCommandStatus "$?"  
}

pushMasterBranch() {
  log.info "Pushing master branch to all remotes"
  git push all master
  checkCommandStatus "$?"
}

tagRelease() {
  log.info "Creating tag for release v${RELEASE_VERSION}"
  git tag -a v${RELEASE_VERSION} -m "Release v${RELEASE_VERSION}"
  checkCommandStatus "$?" 
}

pushTag() {
  log.info "Pushing tag v${RELEASE_VERSION} to all remotes"
  git push all v${RELEASE_VERSION}
  checkCommandStatus "$?"
}

gitCheckoutReleaseBranch() {
  log.info "Switching back to ${RELEASE_BRANCH}"
  git checkout ${RELEASE_BRANCH}
  checkCommandStatus "$?"
}

pushReleaseBranch() {
  log.info "Pushing ${RELEASE_BRANCH} branch to all remotes"
  git push all ${RELEASE_BRANCH}
  checkCommandStatus "$?"
}

reapplyStashedChangesInReleaseBranch() {
  log.info "Reapplying stashed changes in ${RELEASE_BRANCH}"
  git stash pop
}

parseArguments() {
  while getopts ":hv:" OPT; do
    case $OPT in
    ("h")
      parseHelp
      ;;
    ("v")
      local RELEASE_VERSION_ARG=$OPTARG 
      if [[ "${RELEASE_VERSION_ARG}" =~ ${RELEASE_VERSION_RX} ]]; then
        : # Valid release version
      else
        log.error "Option -v has an invalid value of ${RELEASE_VERSION_ARG}"
        printHelp
        exitProcess 1
      fi
      RELEASE_VERSION=${RELEASE_VERSION_ARG}
      ;;
    (\?)
      parseInvalidArgument "$OPTARG"
      ;;
    esac
  done
  
  if [ -z "${RELEASE_VERSION}" ]
  then
    log.error "Option -v version needs to be set"
    printHelp
    exitProcess 1
  fi
}

printHelp() {
  echo -e ""
  echo -e "Usage: ${COL_PURPLE}${SCRIPT_NAME}${COL_NORMAL} [options]"
  echo -e ""
  echo -e "  Options:"
  echo -e "     ${COL_BLUE}-h${COL_NORMAL} display help" 
  echo -e "     ${COL_BLUE}-v (9.99)${COL_NORMAL} Release version [${COL_RED}required${COL_NORMAL}]"
}

main "$@"
