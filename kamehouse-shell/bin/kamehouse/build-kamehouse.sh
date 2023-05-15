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

source ${HOME}/.kamehouse/.shell/.cred

LOG_PROCESS_TO_FILE=true

# buildMobile default settings override for build
USE_CURRENT_DIR_FOR_CORDOVA=true

mainProcess() {
  buildKameHouseProject
}

parseArguments() {
  parseKameHouseModule "$@"
  parseMavenProfile "$@"

  while getopts ":bcdfim:p:r" OPT; do
    case $OPT in 
    ("b")
      REFRESH_CORDOVA_PLUGINS=true
      ;;  
    ("c")
      CONTINUE_INTEGRATION_TESTS_ON_ERRORS=true
      ;;    
    ("d")
      CLEAN_CORDOVA_BEFORE_BUILD=true
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
  addHelpOption "-b" "mobile: refresh cordova plugins ${COL_YELLOW}USE WHEN VERY SURE"
  addHelpOption "-c" "integration tests: continue even with errors when running integration tests"
  addHelpOption "-d" "mobile: reset platforms on project. ${COL_YELLOW}USE WHEN VERY SURE"
  addHelpOption "-f" "fast build. Skip checkstyle, findbugs and tests"
  addHelpOption "-i" "integration tests: run integration tests only"
  printKameHouseModuleOption "build"
  printMavenProfileOption
  addHelpOption "-r" "resume build. Continue where it failed in the last build"
}

main "$@"
