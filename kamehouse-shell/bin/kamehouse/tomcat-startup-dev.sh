#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/common-functions.sh
if [ "$?" != "0" ]; then
	echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
	exit 1
fi

# dev environment: eclipse or intellij
DEV_ENVIRONMENT=intellij
LOG_PROCESS_TO_FILE=false
TOMCAT_DIR=${HOME}/programs/apache-tomcat-dev
TOMCAT_LOG=${TOMCAT_DIR}/logs/catalina.out

mainProcess() {
  setGlobalVariables

  echo "********************************************************************************************"
  echo " Redirecting logs to ${TOMCAT_LOG}"
  echo ""
  echo "               Tail the logs using the command 'tail-log.sh -f ${DEV_ENVIRONMENT}'"
  echo "********************************************************************************************"
  cd ${TOMCAT_DIR}
  # Start with jpda start to be able to remote debug on port 8000 (default port)
  ${TOMCAT_DIR}/bin/catalina.sh jpda start | tee ${TOMCAT_LOG} 
}

setGlobalVariables() {
  TOMCAT_DIR=${HOME}/programs/apache-tomcat-dev
  TOMCAT_LOG=${TOMCAT_DIR}/logs/catalina.out
  if ${IS_LINUX_HOST}; then
    source ${HOME}/programs/kamehouse-shell/bin/lin/bashrc/java-home.sh
  fi
}

parseArguments() {
  while getopts ":hi:" OPT; do
    case $OPT in
    ("h")
      parseHelp
      ;;
    ("i")
      DEV_ENVIRONMENT=$OPTARG
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
  echo -e "     ${COL_BLUE}-h${COL_NORMAL} display help" 
  echo -e "     ${COL_BLUE}-i (eclipse|intellij)${COL_NORMAL} IDE's tomcat to deploy to" 
}

main "$@"
