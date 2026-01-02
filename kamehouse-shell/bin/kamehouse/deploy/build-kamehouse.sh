#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

importKamehouse common/functions/kamehouse/build-functions.sh

initKameHouseShellEnv() {
  LOAD_KAMEHOUSE_SECRETS=true
}

initScriptEnv() {
  # Run the build on this script always from the current directory
  USE_CURRENT_DIR=true
}

mainProcess() {
  setKameHouseRootProjectDir
  setKameHouseBuildVersion
  generateBuildInfo
  buildKameHouseStatic
  buildKameHouseBackend
  buildKameHouseMobile
  deleteGitRepoBuildInfoFiles
  cleanUpMavenRepository
}

parseArguments() {
  parseKameHouseModule "$@"
  parseMavenProfile "$@"

  local OPTIONS=("$@")
  for i in "${!OPTIONS[@]}"; do
    local CURRENT_OPTION="${OPTIONS[i]}"
    if [ "${CURRENT_OPTION:0:1}" != "-" ]; then
      continue
    fi
    local CURRENT_OPTION_ARG="${OPTIONS[i+1]}"
    case "${CURRENT_OPTION}" in
      -m|-p)
        # parsed in a previous parse options function 
        ;;
      -c)
        CONTINUE_INTEGRATION_TESTS_ON_ERRORS=true
        ;;
      -f)
        FAST_BUILD=true
        ;;
      --ci-build)
        CI_BUILD=true
        ;;
      -i)
        INTEGRATION_TESTS=true
        ;;
      -r)
        RESUME_BUILD=true
        ;;
      -?|-??*)
        parseInvalidArgument "${CURRENT_OPTION}"
        ;;        
    esac
  done    
}

setEnvFromArguments() {
  setEnvForKameHouseModule
  setEnvForMavenProfile
}

printHelpOptions() {
  addHelpOption "-c" "continue even with errors when running integration tests. ${COL_YELLOW}Use with -i"
  addHelpOption "-f" "fast build. Skip checkstyle, findbugs and tests"
  addHelpOption "-i" "integration tests: run integration tests only"
  printKameHouseModuleOption "build"
  printMavenProfileOption
  addHelpOption "-r" "resume build. Continue where it failed in the last build. ${COL_YELLOW}Use with -m"
}

main "$@"
