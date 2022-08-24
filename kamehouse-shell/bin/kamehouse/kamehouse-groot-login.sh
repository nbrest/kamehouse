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

# Global variables
LOG_PROCESS_TO_FILE=true
LOG_CMD_ARGS=false

HTPASSWD_CMD=${HOME}/programs/apache-httpd/bin/htpasswd.exe
HTPASSWD_FILE=${HOME}/programs/apache-httpd/www/kamehouse-webserver/.htpasswd

mainProcess() {
  log.debug "Attempting login for user ${COL_PURPLE}${USERNAME_ARG}"
  
  if ${IS_LINUX_HOST}; then
    HTPASSWD_CMD=htpasswd
    HTPASSWD_FILE=/var/www/kamehouse-webserver/.htpasswd
  fi

  ${HTPASSWD_CMD} -vb ${HTPASSWD_FILE} ${USERNAME_ARG} ${PASSWORD_ARG}
  RESULT=$?
  if [ "${RESULT}" != "0" ]; then
    echo "loginStatus=ERROR"
  else
    echo "loginStatus=SUCCESS"
  fi
}

parseArguments() {
  parsePasswordArg "$@"
  parseUsernameArg "$@"
}

setEnvFromArguments() {
  checkRequiredOption "-p" "${PASSWORD_ARG}"
  checkRequiredOption "-u" "${USERNAME_ARG}"
}

printHelpOptions() {
  printPasswordArgOption
  printUsernameArgOption
}

main "$@"
