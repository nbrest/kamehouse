#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "`date +%Y-%m-%d' '%H:%M:%S` - [ERROR] - Error importing kamehouse-functions.sh" ; exit 99 ; fi

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/build-functions.sh
if [ "$?" != "0" ]; then echo "`date +%Y-%m-%d' '%H:%M:%S` - [ERROR] - Error importing build-functions.sh" ; exit 99 ; fi

initKameHouseShellEnv() {
  LOAD_KAMEHOUSE_SECRETS=true
}

initScriptEnv() {
  # Run the build on this script always from the current directory
  USE_CURRENT_DIR=true
  STATIC_ONLY=false
  SKIP_STATIC=false
}

mainProcess() {
  setKameHouseRootProjectDir
  setKameHouseBuildVersion
  generateBuildInfo
  if ! ${SKIP_STATIC}; then
    buildKameHouseStatic
  fi
  checkBuildStaticOnly
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
      -i)
        INTEGRATION_TESTS=true
        ;;
      -r)
        RESUME_BUILD=true
        ;;
      -s)
        STATIC_ONLY=true
        ;;
      --skip-static)
        SKIP_STATIC=true
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
  addHelpOption "-s" "build static ui code only"
  addHelpOption "--skip-static" "skip static code build"
}

main "$@"
