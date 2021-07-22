#!/bin/bash

# Import common functions
source ${HOME}/my.scripts/common/common-functions.sh
if [ "$?" != "0" ]; then
	echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
	exit 1
fi

# dev environment: eclipse or intellij
DEV_ENVIRONMENT=
LOG_PROCESS_TO_FILE=false
TOMCAT_VERSION=8.5
TOMCAT_DIR=${HOME}/workspace-intellij/apache-tomcat-${TOMCAT_VERSION}
TOMCAT_LOG=${TOMCAT_DIR}/logs/catalina.out

mainProcess() {
  DEV_ENVIRONMENT=$1
  if [ -z "${DEV_ENVIRONMENT}" ]; then
    log.error "Need to pass eclipse or intellij as parameter"
    exit 1
  fi

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
  TOMCAT_DIR=${HOME}/workspace-${DEV_ENVIRONMENT}/apache-tomcat-${TOMCAT_VERSION}
  TOMCAT_LOG=${TOMCAT_DIR}/logs/catalina.out
}

main "$@"
