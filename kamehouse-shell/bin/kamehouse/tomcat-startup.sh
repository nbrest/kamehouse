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

DEBUG_MODE=false
TOMCAT_DIR=""
TOMCAT_LOG=""

mainProcess() {
  source ${HOME}/programs/kamehouse-shell/bin/kamehouse/set-java-home.sh false true
  export HOME=`${HOME}/programs/kamehouse-shell/bin/kamehouse/get-userhome.sh`
  TOMCAT_DIR="${HOME}/programs/apache-tomcat"
  TOMCAT_LOG=${TOMCAT_DIR}/logs/catalina.out
  cd ${TOMCAT_DIR}
  if ${IS_LINUX_HOST}; then
    startTomcatLinux
  else
    startTomcatWindows
  fi
}

startTomcatLinux() {
  if [ -z "${TERM}" ]; then
    export TERM=xterm
  fi

  if [ -z "${DISPLAY}" ]; then
    export DISPLAY=:0.0
  fi

  if [ -z "${DBUS_SESSION_BUS_ADDRESS}" ]; then
    USER_UID=`cat /etc/passwd | grep "/home/${USER}:" | cut -d ':' -f3`
    export DBUS_SESSION_BUS_ADDRESS=unix:path=/run/user/${USER_UID}/bus
  fi

  log.debug "TERM=${TERM}"
  log.debug "DISPLAY=${DISPLAY}"
  log.debug "DBUS_SESSION_BUS_ADDRESS=${DBUS_SESSION_BUS_ADDRESS}"

  if ${DEBUG_MODE}; then
    log.info "Starting tomcat ${TOMCAT_DIR} in debug mode"
    log.debug "${TOMCAT_DIR}/bin/catalina.sh jpda start | tee ${TOMCAT_LOG}"
    cd ${TOMCAT_DIR}
    ${TOMCAT_DIR}/bin/catalina.sh jpda start | tee ${TOMCAT_LOG}
  else
    log.info "Starting tomcat ${TOMCAT_DIR}"
    log.debug "${TOMCAT_DIR}/bin/startup.sh"
    cd ${TOMCAT_DIR} 
    ${TOMCAT_DIR}/bin/startup.sh
  fi
}

startTomcatWindows() {
  if ${DEBUG_MODE}; then
    log.info "Starting tomcat ${TOMCAT_DIR} in debug mode"
    log.debug "powershell.exe -c \"Start-Process ./bin/catalina.bat -ArgumentList \\\"jpda start\\\"\" &"
    powershell.exe -c "Start-Process ./bin/catalina.bat -ArgumentList \"jpda start\"" &
  else
    log.info "Starting tomcat ${TOMCAT_DIR}"
    log.debug "powershell.exe -c \"Start-Process ./bin/startup.bat\" &"
    powershell.exe -c "Start-Process ./bin/startup.bat" &
  fi
}

parseArguments() {
  while getopts ":d" OPT; do
    case $OPT in
    ("d")
      DEBUG_MODE=true
      ;;
    (\?)
      parseInvalidArgument "$OPTARG"
      ;;
    esac
  done 
}

printHelpOptions() {
  addHelpOption "-d" "debug. start tomcat in debug mode"
}

main "$@"
