#!/bin/bash

# Import common functions
source ${HOME}/my.scripts/common/common-functions.sh
if [ "$?" != "0" ]; then
	echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
	exit 1
fi

# Import kamehouse functions
source ${HOME}/my.scripts/common/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing kamehouse-functions.sh\033[0;39m"
  exit 1
fi
source ${HOME}/my.scripts/.cred/.cred

# dev environment: eclipse or intellij
DEV_ENVIRONMENT=
PROJECT_DIR=
TOMCAT_WEBAPPS_DIR=
TOMCAT_PORT=9980

MODULE=
MODULE_SHORT=

mainProcess() {
  setGlobalVariables
  cd ${PROJECT_DIR}
  buildProject
  cleanLogsInGitRepoFolder
  undeployFromTomcat
  deployToTomcat

  log.info "Finished deploying ${COL_PURPLE}kamehouse${COL_DEFAULT_LOG} to ${COL_PURPLE}${TOMCAT_WEBAPPS_DIR}${COL_DEFAULT_LOG}"
  log.info "Execute ${COL_PURPLE}-  tail-log.sh -f ${DEV_ENVIRONMENT}  -${COL_DEFAULT_LOG} to check tomcat startup progress"
}

setGlobalVariables() {
  WORKSPACE=${HOME}/workspace-${DEV_ENVIRONMENT}
  PROJECT_DIR=${WORKSPACE}/java.web.kamehouse
  TOMCAT_WEBAPPS_DIR=${WORKSPACE}/apache-tomcat/webapps
  if ${IS_LINUX_HOST}; then
    source ${HOME}/my.scripts/lin/bashrc/java-home.sh
  fi
}

buildProject() {
  log.info "Building kamehouse (skipping tests, checkstyle and findbugs) in ${DEV_ENVIRONMENT}"
  MAVEN_COMMAND="mvn clean install -Dmaven.test.skip=true -Dcheckstyle.skip=true -Dspotbugs.skip=true"
  
  if [ -n "${MODULE}" ]; then
    log.info "Building module ${COL_PURPLE}${MODULE}"
    MAVEN_COMMAND="${MAVEN_COMMAND} -pl :${MODULE} -am"
  else
    log.info "Building all modules"
  fi
  
  ${MAVEN_COMMAND}
  checkCommandStatus "$?" "An error occurred building the project ${PROJECT_DIR}"
}

undeployFromTomcat() {
  log.info "Undeploying kamehouse webapps from tomcat"
  local WEBAPP=${MODULE_SHORT}
  if [ "${MODULE_SHORT}" == "ui" ]; then
    WEBAPP=""
  fi

  local KAMEHOUSE_WEBAPPS=`curl http://${TOMCAT_TEXT_USER}:${TOMCAT_TEXT_PASS}@localhost:${TOMCAT_PORT}/manager/text/list 2>/dev/null | grep "/kame-house" | grep "${WEBAPP}" | awk -F':' '{print $1}'`
  
  if [ "${MODULE_SHORT}" == "ui" ]; then
    KAMEHOUSE_WEBAPPS="/kame-house"
  fi
  
  if [ -n "${KAMEHOUSE_WEBAPPS}" ]; then
    echo -e "${KAMEHOUSE_WEBAPPS}" | while read KAMEHOUSE_WEBAPP; do
      log.info "Undeploying ${KAMEHOUSE_WEBAPP}"
      curl http://${TOMCAT_TEXT_USER}:${TOMCAT_TEXT_PASS}@localhost:${TOMCAT_PORT}/manager/text/stop?path=${KAMEHOUSE_WEBAPP} 2>/dev/null
      sleep 2
      curl http://${TOMCAT_TEXT_USER}:${TOMCAT_TEXT_PASS}@localhost:${TOMCAT_PORT}/manager/text/undeploy?path=${KAMEHOUSE_WEBAPP} 2>/dev/null
      sleep 2
    done
  fi
}

deployToTomcat() {
  log.info "Deploying kamehouse modules to tomcat"
  local KAMEHOUSE_MODULES=`ls -1 | grep kamehouse-${MODULE_SHORT}`
  echo -e "${KAMEHOUSE_MODULES}" | while read KAMEHOUSE_MODULE; do
    local KAMEHOUSE_MODULE_WAR=`ls -1 ${KAMEHOUSE_MODULE}/target/*.war 2>/dev/null`
    if [ -n "${KAMEHOUSE_MODULE_WAR}" ]; then
      log.info "Deploying ${KAMEHOUSE_MODULE} in ${DEV_ENVIRONMENT}"
      cp -v ${KAMEHOUSE_MODULE_WAR} ${TOMCAT_WEBAPPS_DIR}
      checkCommandStatus "$?" "An error occurred copying ${KAMEHOUSE_MODULE_WAR} to the deployment directory ${TOMCAT_WEBAPPS_DIR}"
    fi
  done
}

parseArguments() {
  while getopts ":hi:m:" OPT; do
    case $OPT in
    ("h")
      parseHelp
      ;;
    ("i")
      DEV_ENVIRONMENT=$OPTARG
      ;;
    ("m")
      MODULE="kamehouse-$OPTARG"
      MODULE_SHORT="$OPTARG"
      ;;
    (\?)
      parseInvalidArgument "$OPTARG"
      ;;
    esac
  done
  
  if [ -z "${DEV_ENVIRONMENT}" ]; then
    log.error "Option -i is not set. Re-run the script with that option set"
    exitProcess 1
  fi
}

printHelp() {
  echo -e ""
  echo -e "Usage: ${COL_PURPLE}${SCRIPT_NAME}${COL_NORMAL} [options]"
  echo -e ""
  echo -e "  Options:"  
  echo -e "     ${COL_BLUE}-h${COL_NORMAL} display help" 
  echo -e "     ${COL_BLUE}-i (eclipse|intellij)${COL_NORMAL} IDE's tomcat to deploy to" 
  echo -e "     ${COL_BLUE}-m (admin|cmd|groot|media|shell|tennisworld|testmodule|ui|vlcrc)${COL_NORMAL} module to deploy"
}

main "$@"
