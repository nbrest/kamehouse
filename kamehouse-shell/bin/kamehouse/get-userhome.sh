#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/common-functions.sh
if [ "$?" != "0" ]; then
	echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
	exit 149
fi
# Import kamehouse functions
source ${HOME}/programs/kamehouse-shell/bin/common/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing kamehouse-functions.sh\033[0;39m"
  exit 149
fi

USERHOME_WIN="${HOME}"
# USERHOME_LIN gets set during install kamehouse-shell
USERHOME_LIN="/home/${DEFAULT_KAMEHOUSE_USERNAME}"

main() {
  # WIN_USER_HOME=`cmd.exe '/c echo %USERPROFILE%'`
  # WIN_USER_HOME=${WIN_USER_HOME::-1}

  if ${IS_LINUX_HOST}; then
    echo "${USERHOME_LIN}"
  else
    echo "${USERHOME_WIN}"
  fi   
}

main "$@"
