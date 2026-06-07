#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

mainProcess() {
  ${HOME}/programs/kamehouse-shell/bin/tomcat/tomcat-stop.sh -p ${DEFAULT_TOMCAT_DEV_PORT}
}

main "$@"
