deployKameHouseProject() {
  displayDeployEnv  
  setKameHouseDeploymentParameters
  setKameHouseRootProjectDir
  pullLatestKameHouseChanges
  setKameHouseBuildVersion
  generateBuildInfo
  deployKameHouseShell
  deployKameHouseGroot
  buildKameHouseStatic
  deployKameHouseStatic
  buildKameHouseBackend
  deployKameHouseBackend
  buildKameHouseMobile
  deployKameHouseMobile
  cleanUpMavenRepository
  checkForDeploymentErrors
}

displayDeployEnv() {
  log.debug "DEPLOY_KAMEHOUSE_TOMCAT_MODULES=${DEPLOY_KAMEHOUSE_TOMCAT_MODULES}"
  log.debug "DEPLOY_KAMEHOUSE_STATIC=${DEPLOY_KAMEHOUSE_STATIC}"
  log.debug "DEPLOY_KAMEHOUSE_CMD=${DEPLOY_KAMEHOUSE_CMD}"
  log.debug "DEPLOY_KAMEHOUSE_SHELL=${DEPLOY_KAMEHOUSE_SHELL}"
  log.debug "DEPLOY_KAMEHOUSE_GROOT=${DEPLOY_KAMEHOUSE_GROOT}"
  log.debug "DEPLOY_KAMEHOUSE_MOBILE=${DEPLOY_KAMEHOUSE_MOBILE}"
}

setKameHouseDeploymentParameters() {
  DEPLOYMENT_DIR="${TOMCAT_DIR}/webapps"
  if ! ${DEPLOY_KAMEHOUSE_TOMCAT_MODULES}; then
    log.warn "DEPLOY_KAMEHOUSE_TOMCAT_MODULES is false so skip deploying kamehouse tomcat modules"
    DEPLOY_TO_TOMCAT=false
    return
  fi
  if [ -z "${MODULE_SHORT}" ]; then
    DEPLOY_TO_TOMCAT=true
    return
  fi
  if [ "${MODULE_SHORT}" == "admin" ] ||
     [ "${MODULE_SHORT}" == "media" ] ||
     [ "${MODULE_SHORT}" == "tennisworld" ] ||
     [ "${MODULE_SHORT}" == "testmodule" ] ||
     [ "${MODULE_SHORT}" == "ui" ] ||
     [ "${MODULE_SHORT}" == "vlcrc" ]; then
    DEPLOY_TO_TOMCAT=true
  fi
}

deployKameHouseShell() {
  if [[ -n "${MODULE_SHORT}" && "${MODULE_SHORT}" != "shell" ]]; then
    return
  fi
  if ! ${DEPLOY_KAMEHOUSE_SHELL}; then
    log.warn "DEPLOY_KAMEHOUSE_SHELL is false so skip deploying kamehouse-shell"
    return
  fi
  log.info "Deploying ${COL_PURPLE}kamehouse-shell${COL_DEFAULT_LOG}"
  chmod a+x kamehouse-shell/bin/kamehouse/install-kamehouse-shell.sh
  ./kamehouse-shell/bin/kamehouse/install-kamehouse-shell.sh -l ${LOG_LEVEL}
  checkCommandStatus "$?" "An error occurred deploying kamehouse-shell"

  log.info "Finished deploying ${COL_PURPLE}kamehouse-shell${COL_DEFAULT_LOG}"

  if [ "${MODULE_SHORT}" == "shell" ]; then
    exitSuccessfully
  fi
}

deployKameHouseGroot() {
  if [[ -n "${MODULE_SHORT}" && "${MODULE_SHORT}" != "groot" ]]; then
    return
  fi
  if ! ${DEPLOY_KAMEHOUSE_GROOT}; then
    log.warn "DEPLOY_KAMEHOUSE_GROOT is false so skip deploying kamehouse-groot"
    return
  fi
  log.info "Deploying ${COL_PURPLE}kamehouse-groot${COL_DEFAULT_LOG}" 
  local HTTPD_CONTENT_ROOT=`getHttpdContentRoot`
  rm -rf ${HTTPD_CONTENT_ROOT}/kame-house-groot
  mkdir -p ${HTTPD_CONTENT_ROOT}/kame-house-groot
  cp -rf ./kamehouse-groot/src/main/php/kame-house-groot/* ${HTTPD_CONTENT_ROOT}/kame-house-groot/
  cp -f ./build-info.cfg ${HTTPD_CONTENT_ROOT}/kame-house-groot/
  cp -f ./build-info.json ${HTTPD_CONTENT_ROOT}/kame-house-groot/
  checkCommandStatus "$?" "An error occurred deploying kamehouse groot"

  local FILES=`find ${HTTPD_CONTENT_ROOT}/kame-house-groot -name '.*' -prune -o -type f`
  while read FILE; do
    if [ -n "${FILE}" ]; then
      chmod a+rx ${FILE}
    fi
  done <<< ${FILES}

  local DIRECTORIES=`find ${HTTPD_CONTENT_ROOT}/kame-house-groot -name '.*' -prune -o -type d`
  while read DIRECTORY; do
    if [ -n "${DIRECTORY}" ]; then
      chmod a+rx ${DIRECTORY}
    fi
  done <<< ${DIRECTORIES}

  log.info "Deployed kamehouse-groot status"
  log.info "ls -lh ${COL_CYAN_STD}${HTTPD_CONTENT_ROOT}/kame-house-groot"
  ls -lh "${HTTPD_CONTENT_ROOT}/kame-house-groot"
  log.info "kamehouse-groot version"
  cat "${HTTPD_CONTENT_ROOT}/kame-house-groot/build-info.cfg"
  log.info "Finished deploying ${COL_PURPLE}kamehouse-groot${COL_DEFAULT_LOG}"

  if [ "${MODULE_SHORT}" == "groot" ]; then
    exitSuccessfully
  fi
}

deployKameHouseBackend() {
  deployTomcatModules
  deployKameHouseCmd  
}

deployTomcatModules() {
  if ! ${DEPLOY_TO_TOMCAT}; then
    return
  fi
  executeOperationInTomcatManager "stop" ${TOMCAT_PORT} ${MODULE_SHORT}
  executeOperationInTomcatManager "undeploy" ${TOMCAT_PORT} ${MODULE_SHORT}
  deployToTomcat
}

deployToTomcat() {
  log.info "Deploying ${COL_PURPLE}${PROJECT}${COL_DEFAULT_LOG} to ${COL_PURPLE}${DEPLOYMENT_DIR}${COL_DEFAULT_LOG}" 

  local KAMEHOUSE_MODULES=`ls -1 | grep kamehouse-${MODULE_SHORT}`
  echo -e "${KAMEHOUSE_MODULES}" | while read KAMEHOUSE_MODULE; do
    local KAMEHOUSE_MODULE_WAR=`ls -1 ${KAMEHOUSE_MODULE}/target/*.war 2>/dev/null`
    if [ -n "${KAMEHOUSE_MODULE_WAR}" ]; then
      log.info "Deploying ${KAMEHOUSE_MODULE} in ${COL_PURPLE}${DEPLOYMENT_DIR}"
      cp -v ${KAMEHOUSE_MODULE_WAR} ${DEPLOYMENT_DIR}
      checkCommandStatus "$?" "An error occurred copying ${KAMEHOUSE_MODULE_WAR} to the deployment directory ${DEPLOYMENT_DIR}"
    fi
  done

  log.info "Deployed tomcat modules status"
  log.info "ls -lh ${COL_CYAN_STD}${DEPLOYMENT_DIR}/*.war"
  ls -lh "${DEPLOYMENT_DIR}"/*.war
  log.info "kamehouse tomcat modules version"
  cat ./kamehouse-commons-core/src/main/resources/build-info.cfg
  log.info "Finished deploying ${COL_PURPLE}${PROJECT}${COL_DEFAULT_LOG} to ${COL_PURPLE}${DEPLOYMENT_DIR}${COL_DEFAULT_LOG}"
  local TAIL_LOG_FILE="tomcat"
  if [[ ${DEPLOYMENT_DIR} =~ .*apache-tomcat-dev.* ]]; then
    TAIL_LOG_FILE="tomcat-dev"
  fi
  log.info "Execute ${COL_CYAN}\`  tail-log.sh -f ${TAIL_LOG_FILE} -n 2000  \`${COL_DEFAULT_LOG} to check tomcat startup progress"
}

deployKameHouseCmd() {
  if [[ -n "${MODULE_SHORT}" && "${MODULE_SHORT}" != "cmd" ]]; then
    return
  fi
  if ! ${DEPLOY_KAMEHOUSE_CMD}; then
    log.warn "DEPLOY_KAMEHOUSE_CMD is false so skip deploying kamehouse-cmd"
    return
  fi
  log.info "Deploying ${COL_PURPLE}kamehouse-cmd${COL_DEFAULT_LOG} to ${COL_PURPLE}${KAMEHOUSE_CMD_DEPLOY_PATH}${COL_DEFAULT_LOG}" 
  mkdir -p ${KAMEHOUSE_CMD_DEPLOY_PATH}
  rm -r -f ${KAMEHOUSE_CMD_DEPLOY_PATH}/kamehouse-cmd
  unzip -o -q kamehouse-cmd/target/kamehouse-cmd-bundle.zip -d ${KAMEHOUSE_CMD_DEPLOY_PATH}/ 
  mv ${KAMEHOUSE_CMD_DEPLOY_PATH}/kamehouse-cmd/bin/kamehouse-cmd.bt ${KAMEHOUSE_CMD_DEPLOY_PATH}/kamehouse-cmd/bin/kamehouse-cmd.bat
  cp -f ./build-info.cfg ${KAMEHOUSE_CMD_DEPLOY_PATH}/kamehouse-cmd/lib/
  cp -f ./build-info.json ${KAMEHOUSE_CMD_DEPLOY_PATH}/kamehouse-cmd/lib/
  chmod -R 700 ${KAMEHOUSE_CMD_DEPLOY_PATH}/kamehouse-cmd
  log.info "Deployed kamehouse-cmd status"
  log.info "ls -lh ${COL_CYAN_STD}${KAMEHOUSE_CMD_DEPLOY_PATH}/kamehouse-cmd/lib/kamehouse-cmd*"
  ls -lh "${KAMEHOUSE_CMD_DEPLOY_PATH}/kamehouse-cmd/bin/kamehouse-cmd"*
  ls -lh "${KAMEHOUSE_CMD_DEPLOY_PATH}/kamehouse-cmd/lib/kamehouse-cmd"*.jar
  log.info "kamehouse-cmd version"
  cat ${HOME}/programs/kamehouse-cmd/lib/build-info.cfg
  checkCommandStatus "$?" "An error occurred deploying kamehouse-cmd"
}

deployKameHouseMobile() {
  if [[ "${MODULE}" != "kamehouse-mobile" ]]; then
    return
  fi
  if ! ${DEPLOY_KAMEHOUSE_MOBILE}; then
    log.warn "DEPLOY_KAMEHOUSE_MOBILE is false so skip deploying kamehouse-mobile"
    return
  fi
  if [ -f "${KAMEHOUSE_ANDROID_APK_PATH}" ]; then
    uploadKameHouseMobileApkToGDrive
    uploadKameHouseMobileApkToHttpdServer
  else
    log.error "${KAMEHOUSE_ANDROID_APK_PATH} not found. Was the build successful?"
    EXIT_CODE=${EXIT_ERROR}
  fi
}

uploadKameHouseMobileApkToHttpdServer() {
  log.info "Deploying ${COL_PURPLE}kamehouse-mobile${COL_DEFAULT_LOG} app to downloads server"
  log.info "Set ${COL_YELLOW}KAMEHOUSE_MOBILE_APP_SERVER_IP, KAMEHOUSE_MOBILE_APP_SERVER_PATH and KAMEHOUSE_MOBILE_APP_SERVER_USERNAME${COL_DEFAULT_LOG} in ${HOME}/.kamehouse/config/kamehouse.cfg"
  SCP_SRC="${KAMEHOUSE_ANDROID_APK_PATH}"
  SCP_DEST="${KAMEHOUSE_MOBILE_APP_SERVER_USERNAME}@${KAMEHOUSE_MOBILE_APP_SERVER_IP}:${KAMEHOUSE_MOBILE_APP_SERVER_PATH}/kamehouse.apk"
  executeScpCommand

  SSH_USER="${KAMEHOUSE_MOBILE_APP_SERVER_USERNAME}"
  SSH_SERVER="${KAMEHOUSE_MOBILE_APP_SERVER_IP}"
  IS_REMOTE_LINUX_HOST=true
  SSH_COMMAND="\${HOME}/programs/kamehouse-shell/bin/kamehouse/kamehouse-mobile-update-apk-status-html.sh -b ${KAMEHOUSE_BUILD_VERSION}"
  executeSshCommand
}

uploadKameHouseMobileApkToGDrive() {
  if [ -d "${KAMEHOUSE_MOBILE_GDRIVE_PATH_WIN}" ]; then
    log.info "${COL_PURPLE}Uploading${COL_DEFAULT_LOG} kamehouse-mobile apk ${COL_PURPLE}to google drive${COL_DEFAULT_LOG} folder ${KAMEHOUSE_MOBILE_GDRIVE_PATH_WIN}"
    cp ${KAMEHOUSE_ANDROID_APK_PATH} "${KAMEHOUSE_MOBILE_GDRIVE_PATH_WIN}/kamehouse.apk"
    log.info "Deployed kamehouse-mobile apk status"
    log.info "ls -lh ${COL_CYAN_STD}${KAMEHOUSE_MOBILE_GDRIVE_PATH_WIN}/kamehouse.apk"
    ls -lh "${KAMEHOUSE_MOBILE_GDRIVE_PATH_WIN}/kamehouse.apk"
  fi

  if [ -d "${HOME}/GoogleDrive" ]; then
    log.info "Mounting google drive"
    google-drive-ocamlfuse ${HOME}/GoogleDrive
    sleep 8
  fi

  if [ -d "${KAMEHOUSE_MOBILE_GDRIVE_PATH_LIN}" ]; then
    log.info "${COL_PURPLE}Uploading${COL_DEFAULT_LOG} kamehouse-mobile apk ${COL_PURPLE}to google drive${COL_DEFAULT_LOG} folder ${KAMEHOUSE_MOBILE_GDRIVE_PATH_LIN}"
    cp ${KAMEHOUSE_ANDROID_APK_PATH} "${KAMEHOUSE_MOBILE_GDRIVE_PATH_LIN}/kamehouse.apk"
    log.info "Deployed kamehouse-mobile apk status"
    log.info "ls -lh ${COL_CYAN_STD}${KAMEHOUSE_MOBILE_GDRIVE_PATH_LIN}/kamehouse.apk"
    ls -lh "${KAMEHOUSE_MOBILE_GDRIVE_PATH_LIN}/kamehouse.apk"
  fi
}

deployKameHouseStatic() {
  if ! ${DEPLOY_KAMEHOUSE_STATIC}; then
    log.warn "DEPLOY_KAMEHOUSE_STATIC is false so skip deploying kamehouse static content"
    return
  fi
  deployKameHouseUiStatic
  deployKameHouseMobileStatic
  if ! ${STATIC_ONLY}; then
    return
  fi
  if [[ -z "${MODULE}" ]]; then
    log.info "Finished deploying static code for all modules"
  else 
    log.info "Finished deploying static code for module ${COL_PURPLE}${MODULE}"
  fi
  exitSuccessfully    
}

deployKameHouseUiStatic() {
  if [[ -n "${MODULE_SHORT}" && "${MODULE_SHORT}" != "ui" ]]; then
    return
  fi
  log.info "Deploying ${COL_PURPLE}kamehouse-ui static content${COL_DEFAULT_LOG}"
  local HTTPD_CONTENT_ROOT=`getHttpdContentRoot`
  rm -rf ${HTTPD_CONTENT_ROOT}/kame-house
  mkdir -p ${HTTPD_CONTENT_ROOT}/kame-house
  cp -rf ./kamehouse-ui/dist/* ${HTTPD_CONTENT_ROOT}/kame-house/
  checkCommandStatus "$?" "An error occurred deploying kamehouse ui static content"

  local FILES=`find ${HTTPD_CONTENT_ROOT}/kame-house -name '.*' -prune -o -type f`
  while read FILE; do
    if [ -n "${FILE}" ]; then
      chmod a+rx ${FILE}
    fi
  done <<< ${FILES}

  local DIRECTORIES=`find ${HTTPD_CONTENT_ROOT}/kame-house -name '.*' -prune -o -type d`
  while read DIRECTORY; do
    if [ -n "${DIRECTORY}" ]; then
      chmod a+rx ${DIRECTORY}
    fi
  done <<< ${DIRECTORIES}

  log.info "Deployed kamehouse-ui status"
  log.info "ls -lh ${COL_CYAN_STD}${HTTPD_CONTENT_ROOT}/kame-house"
  ls -lh "${HTTPD_CONTENT_ROOT}/kame-house"
  log.info "kamehouse-ui static version"
  cat ${HTTPD_CONTENT_ROOT}/kame-house/build-info.cfg
  log.info "Finished deploying ${COL_PURPLE}kamehouse-ui static content${COL_DEFAULT_LOG}"
}

deployKameHouseMobileStatic() {
  if [[ -n "${MODULE}" && "${MODULE}" != "kamehouse-mobile" ]]; then
    return
  fi
  log.info "Deploying ${COL_PURPLE}kamehouse-mobile static content${COL_DEFAULT_LOG}"
  local HTTPD_CONTENT_ROOT=`getHttpdContentRoot`
  rm -rf ${HTTPD_CONTENT_ROOT}/kame-house-mobile
  mkdir -p ${HTTPD_CONTENT_ROOT}/kame-house-mobile
  cp -rf ./kamehouse-mobile/www/kame-house-mobile/* ${HTTPD_CONTENT_ROOT}/kame-house-mobile/
  cp -f ./build-info.cfg ${HTTPD_CONTENT_ROOT}/kame-house-mobile/
  checkCommandStatus "$?" "An error occurred deploying kamehouse mobile static content"

  local FILES=`find ${HTTPD_CONTENT_ROOT}/kame-house-mobile -name '.*' -prune -o -type f`
  while read FILE; do
    if [ -n "${FILE}" ]; then
      chmod a+rx ${FILE}
    fi
  done <<< ${FILES}

  local DIRECTORIES=`find ${HTTPD_CONTENT_ROOT}/kame-house-mobile -name '.*' -prune -o -type d`
  while read DIRECTORY; do
    if [ -n "${DIRECTORY}" ]; then
      chmod a+rx ${DIRECTORY}
    fi
  done <<< ${DIRECTORIES}

  log.info "Deployed kamehouse-mobile status"
  log.info "ls -lh ${COL_CYAN_STD}${HTTPD_CONTENT_ROOT}/kame-house-mobile"
  ls -lh "${HTTPD_CONTENT_ROOT}/kame-house-mobile"
  log.info "kamehouse-mobile static version"
  cat "${HTTPD_CONTENT_ROOT}/kame-house-mobile/build-info.cfg"
  log.info "Finished deploying ${COL_PURPLE}kamehouse-mobile static content${COL_DEFAULT_LOG}"
}

checkForDeploymentErrors() {
  if [ "${EXIT_CODE}" == "0" ]; then
    return
  fi
  log.error "Error executing kamehouse deployment"
  exitProcess ${EXIT_CODE}
}
