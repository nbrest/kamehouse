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

LOG_PROCESS_TO_FILE=true
DEBUG_MODE=false

mainProcess() {
  export HOME=`${HOME}/programs/kamehouse-shell/bin/kamehouse/get-userhome.sh`
  TOMCAT_DIR=`${HOME}/programs/kamehouse-shell/bin/kamehouse/get-tomcat-dir.sh`
  TOMCAT_LOG=${TOMCAT_DIR}/logs/catalina.out
  cd ${TOMCAT_DIR}
  if ${IS_LINUX_HOST}; then
    if ${DEBUG_MODE}; then
      log.info "Starting tomcat ${TOMCAT_DIR} in debug mode"
      log.debug "cd ${TOMCAT_DIR} ; DBUS_SESSION_BUS_ADDRESS=unix:path=/run/user/${USER_UID}/bus DISPLAY=:0.0 ${TOMCAT_DIR}/bin/catalina.sh jpda start | tee ${TOMCAT_LOG}"
      cd ${TOMCAT_DIR} ; DBUS_SESSION_BUS_ADDRESS=unix:path=/run/user/${USER_UID}/bus DISPLAY=:0.0 ${TOMCAT_DIR}/bin/catalina.sh jpda start | tee ${TOMCAT_LOG}
    else
      log.info "Starting tomcat ${TOMCAT_DIR}"
      USER_UID=`cat /etc/passwd | grep ${USER} | cut -d ':' -f3`
      log.debug "cd ${TOMCAT_DIR} ; DBUS_SESSION_BUS_ADDRESS=unix:path=/run/user/${USER_UID}/bus DISPLAY=:0.0 ${TOMCAT_DIR}/bin/startup.sh"
      cd ${TOMCAT_DIR} ; DBUS_SESSION_BUS_ADDRESS=unix:path=/run/user/${USER_UID}/bus DISPLAY=:0.0 ${TOMCAT_DIR}/bin/startup.sh
    fi
  else
    if ${DEBUG_MODE}; then
      log.info "Starting tomcat ${TOMCAT_DIR} in debug mode"
      log.debug "powershell.exe -c \"Start-Process ./bin/catalina.bat -ArgumentList \\\"jpda start\\\"\" &"
      powershell.exe -c "Start-Process ./bin/catalina.bat -ArgumentList \"jpda start\"" &
    else
      log.info "Starting tomcat ${TOMCAT_DIR}"
      log.debug "powershell.exe -c \"Start-Process ./bin/startup.bat\" &"
      powershell.exe -c "Start-Process ./bin/startup.bat" &
    fi
  fi
}

parseArguments() {
  while getopts ":dh" OPT; do
    case $OPT in
    ("d")
      DEBUG_MODE=true
      ;;
    ("h")
      parseHelp
      ;;
    (\?)
      parseInvalidArgument "$OPTARG"
      ;;
    esac
  done 
}

printHelp() {
  echo -e ""
  echo -e "Usage: ${COL_PURPLE}${SCRIPT_NAME}${COL_NORMAL} [options]"
  echo -e ""
  echo -e "  Options:"  
  echo -e "     ${COL_BLUE}-d${COL_NORMAL} debug. start tomcat in debug mode"
  echo -e "     ${COL_BLUE}-h${COL_NORMAL} display help"
}

main "$@"
