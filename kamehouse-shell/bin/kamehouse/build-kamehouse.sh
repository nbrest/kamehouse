#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 1
fi

# Import kamehouse functions
source ${HOME}/programs/kamehouse-shell/bin/common/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing kamehouse-functions.sh\033[0;39m"
  exit 1
fi

LOG_PROCESS_TO_FILE=true
FAST_BUILD=false
INTEGRATION_TESTS=false
MAVEN_COMMAND=
RESUME=false
SKIP_TESTS=false
CONTINUE_ON_ERRORS=false
BUILD_ALL_EXTRA_MODULES=false
DELETE_ALL_MOBILE_OUTPUTS=false

mainProcess() {
  buildProject
  cleanLogsInGitRepoFolder
}

buildProject() {
  log.info "Building ${COL_PURPLE}kamehouse${COL_DEFAULT_LOG} with profile ${COL_PURPLE}${MAVEN_PROFILE}${COL_DEFAULT_LOG}"
  
  exportGitCommitHash
  buildMavenCommand
  executeMavenCommand

  if [[ "${BUILD_ALL_EXTRA_MODULES}" == "true" || "${MODULE}" == "kamehouse-mobile" ]]; then
    buildMobile
  fi
}

buildMavenCommand() {
  MAVEN_COMMAND="mvn clean install -P ${MAVEN_PROFILE}"

  if ${SKIP_TESTS}; then
    log.info "Executing build skipping tests"
    MAVEN_COMMAND="${MAVEN_COMMAND} -Dmaven.test.skip=true"
  fi

  if ${FAST_BUILD}; then
    log.info "Executing fast build. Skipping checkstyle, findbugs and tests"
    MAVEN_COMMAND="${MAVEN_COMMAND} -Dmaven.test.skip=true -Dcheckstyle.skip=true -Dspotbugs.skip=true"
  fi

  if ${INTEGRATION_TESTS}; then
    if ${CONTINUE_ON_ERRORS}; then
      MAVEN_COMMAND="mvn test-compile failsafe:integration-test -P ${MAVEN_PROFILE}"
    else
      MAVEN_COMMAND="mvn test-compile failsafe:integration-test failsafe:verify -P ${MAVEN_PROFILE}"
    fi
  fi

  if [ -n "${MODULE}" ]; then
    log.info "Building module ${COL_PURPLE}${MODULE}"
    if ${RESUME}; then
      log.info "Resuming from last build"
      MAVEN_COMMAND="${MAVEN_COMMAND} -rf :${MODULE}"
    else
      MAVEN_COMMAND="${MAVEN_COMMAND} -pl :${MODULE} -am"
    fi
  else
    log.info "Building all modules"
  fi
}

executeMavenCommand() {
  log.info "Executing command: '${MAVEN_COMMAND}'"
  ${MAVEN_COMMAND}
  checkCommandStatus "$?" "An error occurred building kamehouse"
}

buildMobile() {
  log.info "Building kamehouse-mobile android app"
  cd kamehouse-mobile
  if ${DELETE_ALL_MOBILE_OUTPUTS}; then
    log.debug "cordova clean ; cordova platform remove android ; cordova platform add android"
    cordova clean
    cordova platform remove android
    cordova platform add android
  fi
  ${HOME}/programs/kamehouse-shell/bin/kamehouse/kamehouse-mobile-resync-kh-files.sh
  cp -v -f pom.xml www/
  echo "${GIT_COMMIT_HASH}" > www/git-commit-hash.txt
  date +%Y-%m-%d' '%H:%M:%S > www/build-date.txt
  log.debug "cordova build android"
  cordova build android
  checkCommandStatus "$?" "An error occurred building kamehouse-mobile"
  ${HOME}/programs/kamehouse-shell/bin/kamehouse/kamehouse-mobile-resync-kh-files.sh -d
}

parseArguments() {
  parseKameHouseModule "$@"
  parseMavenProfile "$@"

  while getopts ":acdfim:p:rs" OPT; do
    case $OPT in
    ("a")
      BUILD_ALL_EXTRA_MODULES=true
      ;;  
    ("c")
      CONTINUE_ON_ERRORS=true
      ;;    
    ("d")
      DELETE_ALL_MOBILE_OUTPUTS=true
      ;;    
    ("f")
      FAST_BUILD=true
      ;;
    ("i")
      INTEGRATION_TESTS=true
      ;;
    ("r")
      RESUME=true
      ;;
    ("s")
      SKIP_TESTS=true
      ;;
    (\?)
      parseInvalidArgument "$OPTARG"
      ;;
    esac
  done
}

setEnvFromArguments() {
  setEnvForKameHouseModule
  setEnvForMavenProfile
}

printHelpOptions() {
  addHelpOption "-a" "build all modules, including mobile app (by default it builds all without the mobile app)"
  addHelpOption "-c" "continue even with errors when running integration tests"
  addHelpOption "-d" "delete all output folders on kamehouse-mobile to do a full rebuild. This option is only considered when used with -a or -m mobile"
  addHelpOption "-f" "fast build. Skip checkstyle, findbugs and tests"
  addHelpOption "-i" "run integration tests only"
  printKameHouseModuleOption "build"
  printMavenProfileOption
  addHelpOption "-r" "resume. Continue where it failed in the last build"
  addHelpOption "-s" "skip tests. Use it to find any checkstyle/findbugs issues on all modules regardless of test coverage"
}

main "$@"
