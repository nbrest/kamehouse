#!/bin/bash
# This script runs inside the docker container, not on the host

COL_BLUE="\033[1;34m"
COL_BOLD="\033[1m"
COL_CYAN="\033[1;36m"
COL_GREEN="\033[1;32m"
COL_NORMAL="\033[0;39m"
COL_PURPLE="\033[1;35m"
COL_RED="\033[1;31m"
COL_YELLOW="\033[1;33m"
COL_MESSAGE=${COL_GREEN}

DOCKER_CONTAINER_USERNAME=`ls /home | grep -v "nbrest"`

. /home/${DOCKER_CONTAINER_USERNAME}/.env

DEPLOYMENT_DIR=/home/${DOCKER_CONTAINER_USERNAME}/programs/apache-tomcat/webapps/

export PATH="/home/${DOCKER_CONTAINER_USERNAME}/programs/apache-maven/bin:${PATH}"

main() {
  log.info "Building kamehouse java11-release"
  cd /home/${DOCKER_CONTAINER_USERNAME}/git/kamehouse
  if [ -f "scripts/mvn-install-oracle-jdbc-driver.sh" ]; then
    ./scripts/mvn-install-oracle-jdbc-driver.sh
  fi
  
  deployKameHouseGRoot
  
  buildKameHouse

  deployKameHouseCmd

  log.info "Deploying kamehouse java11-release"
  if [ -f "target/kame-house.war" ]; then
    deployStandaloneWar
  fi
  deployWebappModules
  deployKameHouseShell
}

buildKameHouse() {
  mvn clean install -Dmaven.test.skip=true -Dcheckstyle.skip=true -Dfindbugs.skip=true -Dspotbugs.skip=true -Dstyle.color=always
}

deployStandaloneWar() {
  log.info "Deploying standalone kame-house.war"
  cp -v -f target/kame-house.war ${DEPLOYMENT_DIR}
}

deployWebappModules() {
  local KAMEHOUSE_MODULES=`ls -1 | grep kamehouse-${MODULE_SHORT}`
  echo -e "${KAMEHOUSE_MODULES}" | while read KAMEHOUSE_MODULE; do
    local KAMEHOUSE_MODULE_WAR=`ls -1 ${KAMEHOUSE_MODULE}/target/*.war 2>/dev/null`
    if [ -n "${KAMEHOUSE_MODULE_WAR}" ]; then
      log.info "Deploying ${KAMEHOUSE_MODULE} in ${COL_PURPLE}${DEPLOYMENT_DIR}"
      cp -v -f ${KAMEHOUSE_MODULE_WAR} ${DEPLOYMENT_DIR}
    fi
  done
}

deployKameHouseGRoot() {
  if [ -d "kamehouse-groot/public/kame-house-groot" ]; then
    log.info "Deploying kamehouse GRoot"
    local HTTPD_CONTENT_ROOT=`getHttpdContentRoot`
    mkdir -p ${HTTPD_CONTENT_ROOT}
    rm ${HTTPD_CONTENT_ROOT}/index.html
    cp -f ./kamehouse-groot/public/index.html ${HTTPD_CONTENT_ROOT}/index.html
    rm -rf ${HTTPD_CONTENT_ROOT}/kame-house-groot
    cp -rf ./kamehouse-groot/public/kame-house-groot ${HTTPD_CONTENT_ROOT}/
    cp -f ./build-info.json ${HTTPD_CONTENT_ROOT}/kame-house-groot/
  fi
}

getHttpdContentRoot() {
  if ${IS_LINUX_HOST}; then
    echo "/var/www/kamehouse-webserver"  
  else
    echo "${HOME}/programs/apache-httpd/www/kamehouse-webserver"
  fi
}

deployKameHouseShell() {
  if [ -d "kamehouse-shell/bin" ]; then
    log.info "Deploying kamehouse shell from current release version ${RELEASE_VERSION}"
    chmod a+x ./kamehouse-shell/bin/kamehouse/install-kamehouse-shell.sh
    ./kamehouse-shell/bin/kamehouse/install-kamehouse-shell.sh
  fi

  if [ -d "kamehouse-shell/my.scripts" ]; then
    log.info "Deploying kamehouse shell (my.scripts) from current release version ${RELEASE_VERSION}"
    cp -rf ./kamehouse-shell/my.scripts/* /home/${DOCKER_CONTAINER_USERNAME}/my.scripts
    chmod -R a+rx /home/${DOCKER_CONTAINER_USERNAME}
    cp -rf /home/${DOCKER_CONTAINER_USERNAME}/git/kamehouse/.git /home/${DOCKER_CONTAINER_USERNAME}/my.scripts/
  else 
    log.info "Deploying kamehouse shell (my.scripts) from latest java11 release version ${LATEST_JAVA11_VERSION} with my.scripts"
    local LATEST_JAVA11_VERSION=v7.02
    git checkout tags/${LATEST_JAVA11_VERSION} -b ${LATEST_JAVA11_VERSION}
    cp -rf ./kamehouse-shell/my.scripts/* /home/${DOCKER_CONTAINER_USERNAME}/my.scripts
    chmod -R a+rx /home/${DOCKER_CONTAINER_USERNAME}
    cp -rf /home/${DOCKER_CONTAINER_USERNAME}/git/kamehouse/.git /home/${DOCKER_CONTAINER_USERNAME}/my.scripts/
    git reset --hard
    git checkout ${RELEASE_VERSION}
    git branch -D ${LATEST_JAVA11_VERSION}
  fi
  cd /home/${DOCKER_CONTAINER_USERNAME}/my.scripts
  /home/${DOCKER_CONTAINER_USERNAME}/bin/fix-eol.sh
  cd /home/${DOCKER_CONTAINER_USERNAME}/git/kamehouse
}

deployKameHouseCmd() {
  if [ -d "kamehouse-cmd" ]; then
    log.info "Deploying kamehouse-cmd" 
    local KAMEHOUSE_CMD_DEPLOY_PATH="/home/${DOCKER_CONTAINER_USERNAME}/programs"
    mkdir -p ${KAMEHOUSE_CMD_DEPLOY_PATH}
    rm -r -f ${KAMEHOUSE_CMD_DEPLOY_PATH}/kamehouse-cmd
    unzip -o -q kamehouse-cmd/target/kamehouse-cmd-bundle.zip -d ${KAMEHOUSE_CMD_DEPLOY_PATH}/ 
    mv ${KAMEHOUSE_CMD_DEPLOY_PATH}/kamehouse-cmd/bin/kamehouse-cmd.bt ${KAMEHOUSE_CMD_DEPLOY_PATH}/kamehouse-cmd/bin/kamehouse-cmd.bat
    cp -f ./build-info.json ${KAMEHOUSE_CMD_DEPLOY_PATH}/kamehouse-cmd/lib/
    ls -lh ${KAMEHOUSE_CMD_DEPLOY_PATH}/kamehouse-cmd/bin/kamehouse-cmd.sh
    ls -lh ${KAMEHOUSE_CMD_DEPLOY_PATH}/kamehouse-cmd/lib/kamehouse-cmd*.jar
  fi
}

log.info() {
  local ENTRY_DATE="${COL_CYAN}$(date +%Y-%m-%d' '%H:%M:%S)${COL_NORMAL}"
  local LOG_MESSAGE=$1
  echo -e "${ENTRY_DATE} - [${COL_BLUE}INFO${COL_NORMAL}] - ${COL_MESSAGE}${LOG_MESSAGE}${COL_NORMAL}"
}

main "$@"
