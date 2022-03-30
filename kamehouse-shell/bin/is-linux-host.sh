#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/common-functions.sh
if [ "$?" != "0" ]; then
	echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
	exit 1
fi

# Returns true if it's a linux host, false if it isn't.
# It can also be infered by the return value. 
# Returns 0 for true, 1 for false.
main() {
	echo ${IS_LINUX_HOST}
  if ${IS_LINUX_HOST}; then
    exitProcess 0
  else
    exitProcess 1
  fi
}

main "$@"
