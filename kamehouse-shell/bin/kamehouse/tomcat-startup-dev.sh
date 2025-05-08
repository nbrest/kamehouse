#!/bin/bash

# Import kamehouse functions
source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing kamehouse-functions.sh\033[0;39m"
  exit 99
fi

mainProcess() {
  source ${HOME}/programs/kamehouse-shell/bin/kamehouse/set-java-home.sh --skip-override --log
  
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

setInitialGlobalEnv() {
  TOMCAT_DIR=${HOME}/programs/apache-tomcat-dev
  TOMCAT_LOG=${TOMCAT_DIR}/logs/catalina.out
}

main "$@"
