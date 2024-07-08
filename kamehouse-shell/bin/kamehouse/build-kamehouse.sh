#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/functions/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 99
fi

# Import kamehouse functions
source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing kamehouse-functions.sh\033[0;39m"
  exit 99
fi

source ${HOME}/.kamehouse/.shell/.cred

# Run the build on this script always from the current directory
USE_CURRENT_DIR=true
STATIC_ONLY=false

mainProcess() {
  setKameHouseRootProjectDir
  buildKameHouseUiStatic
  buildKameHouseGroot
  if ${STATIC_ONLY}; then
    log.info "Finished building static code only"
    exitSuccessfully
  fi
  buildKameHouseProject
  cleanUpMavenRepository
}

parseArguments() {
  parseKameHouseModule "$@"
  parseMavenProfile "$@"

  while getopts ":abcfim:p:rs" OPT; do
    case $OPT in 
    ("a")
      CLEAN_CORDOVA_BEFORE_BUILD=true
      ;;   
    ("b")
      REFRESH_CORDOVA_PLUGINS=true
      ;;  
    ("c")
      CONTINUE_INTEGRATION_TESTS_ON_ERRORS=true
      ;;    
    ("f")
      FAST_BUILD=true
      ;;
    ("i")
      INTEGRATION_TESTS=true
      ;;
    ("r")
      RESUME_BUILD=true
      ;;   
    ("s")
      STATIC_ONLY=true
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
  addHelpOption "-a" "mobile: reset platforms on project. ${COL_YELLOW}USE WHEN VERY SURE"
  addHelpOption "-b" "mobile: refresh cordova plugins ${COL_YELLOW}USE WHEN VERY SURE"
  addHelpOption "-c" "integration tests: continue even with errors when running integration tests"
  addHelpOption "-f" "fast build. Skip checkstyle, findbugs and tests"
  addHelpOption "-i" "integration tests: run integration tests only"
  printKameHouseModuleOption "build"
  printMavenProfileOption
  addHelpOption "-r" "resume build. Continue where it failed in the last build. ${COL_YELLOW}Use with -m"
  addHelpOption "-s" "build static ui code only"
}

main "$@"
