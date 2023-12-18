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

trap cleanupAfterRun INT

mainProcess() {
  cdToRootDirFromMobile
  log.info "Run this script from kamehouse-mobile or kamehouse root directory"
  ${HOME}/programs/kamehouse-shell/bin/kamehouse/kamehouse-mobile-resync-static-files.sh -c
  cdToKameHouseMobile
  cordova run browser
  cdToRootDirFromMobile
}

cleanupAfterRun() {
  ${HOME}/programs/kamehouse-shell/bin/kamehouse/kamehouse-mobile-clean.sh
  ctrlC
}

main "$@"
