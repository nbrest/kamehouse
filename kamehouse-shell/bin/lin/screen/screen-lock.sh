#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/functions/common-functions.sh
if [ "$?" != "0" ]; then
	echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
	exit 99
fi

mainProcess() {
  if [ -z "${DISPLAY}" ]; then
    export DISPLAY=:0.0
  fi
  if [ -z "${XDG_RUNTIME_DIR}" ]; then
    export XDG_RUNTIME_DIR=/run/user/$(id -u)
  fi
  gnome-screensaver-command -l
}

main "$@"
