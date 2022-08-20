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

source ${HOME}/programs/kamehouse-shell/bin/common/kamehouse/docker-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing docker-functions.sh\033[0;39m"
  exit 1
fi

source ${HOME}/.kamehouse/.shell/.cred

DEPLOY_TO_DOCKER=false
PROJECT_DIR=
TOMCAT_WEBAPPS_DIR=
TOMCAT_PORT=9980

mainProcess() {
  setGlobalVariables
  cd ${PROJECT_DIR}
  buildProject
  cleanLogsInGitRepoFolder
  undeployFromTomcat
  deployToTomcat

  log.info "Finished deploying ${COL_PURPLE}kamehouse${COL_DEFAULT_LOG} to ${COL_PURPLE}${TOMCAT_WEBAPPS_DIR}${COL_DEFAULT_LOG}"
  log.info "Execute ${COL_PURPLE}-  tail-log.sh -f ${IDE}  -${COL_DEFAULT_LOG} to check tomcat startup progress"
}

setGlobalVariables() {
  WORKSPACE=${HOME}/workspace-${IDE}
  PROJECT_DIR=${WORKSPACE}/kamehouse
  TOMCAT_WEBAPPS_DIR=${HOME}/programs/apache-tomcat-dev/webapps
  if ${IS_LINUX_HOST}; then
    source ${HOME}/programs/kamehouse-shell/bin/lin/bashrc/java-home.sh
  fi
}

buildProject() {
  log.info "Building kamehouse (skipping tests, checkstyle and findbugs) in ${IDE}"
  
  exportGitCommitHash
  
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

  if ${DEPLOY_TO_DOCKER}; then
    TOMCAT_PORT=${DOCKER_PORT_TOMCAT}
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
      log.info "Deploying ${KAMEHOUSE_MODULE} in ${IDE}"
      if ${DEPLOY_TO_DOCKER}; then
        log.debug "scp -C -P ${DOCKER_PORT_SSH} ${KAMEHOUSE_MODULE_WAR} localhost:/home/${DOCKER_USERNAME}/programs/apache-tomcat/webapps"
        scp -C -P ${DOCKER_PORT_SSH} ${KAMEHOUSE_MODULE_WAR} localhost:/home/${DOCKER_USERNAME}/programs/apache-tomcat/webapps
      else
        cp -v ${KAMEHOUSE_MODULE_WAR} ${TOMCAT_WEBAPPS_DIR}
        checkCommandStatus "$?" "An error occurred copying ${KAMEHOUSE_MODULE_WAR} to the deployment directory ${TOMCAT_WEBAPPS_DIR}"
      fi
    fi
  done
}

parseArguments() {
  parseIde "$@"
  parseKameHouseModule "$@"
  
  while getopts ":di:m:" OPT; do
    case $OPT in
    ("d")
      DEPLOY_TO_DOCKER=true
      ;;
    (\?)
      parseInvalidArgument "$OPTARG"
      ;;
    esac
  done
}

setEnvFromArguments() {
  setEnvForIde
  setEnvForKameHouseModule
}

printHelpOptions() {
  addHelpOption "-d" "deploy to docker"
  printIdeOption "ide's tomcat to deploy to"
  printKameHouseModuleOption "deploy"
}

main "$@"
