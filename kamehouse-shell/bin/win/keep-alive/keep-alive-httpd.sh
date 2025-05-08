#!/bin/bash

# Import kamehouse functions
source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing kamehouse-functions.sh\033[0;39m"
  exit 99
fi

mainProcess() {
  checkKeepAliveScriptsEnabled
  netstat -ano | grep "LISTENING" | grep ":${PORT} " | tail -n 1
  PID=`netstat -ano | grep "LISTENING" | grep ":${PORT} " | tail -n 1 | awk '{print $5}'`
  if [[ -z "${PID}" ]]; then
    log.error "${SERVICE} not running. Starting it now"
    ${SERVICE_STARTUP} &
  else 
    log.info "${SERVICE} is currently running with pid ${COL_PURPLE}${PID}"
  fi
}

initScriptEnv() {
  PORT=443
  SERVICE="httpd"
  SERVICE_STARTUP="${HOME}/programs/kamehouse-shell/bin/kamehouse/httpd-restart.sh"
}

main "$@"
