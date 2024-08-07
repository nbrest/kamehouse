#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/functions/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 99
fi

# LOG_PROCESS_TO_FILE=false
TEST_PARAM=""

mainProcess() {
  log.info "base script: TEST_PARAM=${TEST_PARAM}"
  printHelp
}

parseArguments() {
  while getopts ":t:" OPT; do
    case $OPT in
    ("t")
      TEST_PARAM="$OPTARG"
      ;;
    (\?)
      parseInvalidArgument "$OPTARG"
      ;;
    esac
  done 
}

setEnvFromArguments() {
  checkRequiredOption "-t" "${TEST_PARAM}" 
}

printHelpOptions() {
  addHelpOption "-t testParam" "test param" "r"
}

main "$@"
