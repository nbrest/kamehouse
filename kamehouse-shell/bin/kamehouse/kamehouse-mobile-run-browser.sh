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

trap cleanupAfterRun INT

USE_CURRENT_DIR=true

mainProcess() {
  setKameHouseRootProjectDir
  ${HOME}/programs/kamehouse-shell/bin/kamehouse/kamehouse-mobile-resync-static-files.sh -c
  cdToKameHouseModule "kamehouse-mobile"
  cordova run browser
  cdToRootDirFromModule "kamehouse-mobile"
}

cleanupAfterRun() {
  ${HOME}/programs/kamehouse-shell/bin/kamehouse/kamehouse-mobile-clean.sh
  ctrlC
}

main "$@"
