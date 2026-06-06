#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

initScriptEnv() {
  TOMCAT_DIR=${HOME}/programs/apache-tomcat-dev
  TOMCAT_LOG=${TOMCAT_DIR}/logs/catalina.out
}

mainProcess() {
  source ${HOME}/programs/kamehouse-shell/bin/kamehouse/deploy/set-java-home.sh --skip-override --log
  
  echo "********************************************************************************************"
  echo " Redirecting logs to ${TOMCAT_LOG}"
  echo ""
  echo "               Tail the logs using the command '   tail-log.sh -f tomcat-dev -n 2000    '"
  echo "********************************************************************************************"
  cd ${TOMCAT_DIR}
  # Start with jpda start to be able to remote debug on port 8000 (default port)
  log.debug "${TOMCAT_DIR}/bin/catalina.sh jpda start | tee ${TOMCAT_LOG}"
  ${TOMCAT_DIR}/bin/catalina.sh jpda start | tee ${TOMCAT_LOG} 
}

main "$@"
