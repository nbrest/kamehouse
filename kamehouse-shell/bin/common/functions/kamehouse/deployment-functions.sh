deployKameHouseProject() {
  setKameHouseDeploymentParameters
  setKameHouseRootProjectDir
  pullLatestKameHouseChanges
  setKameHouseBuildVersion
  deployKameHouseShell
  buildKameHouseStatic
  deployKameHouseStatic
  buildKameHouseBackend
  deployKameHouseBackend
  buildKameHouseMobile
  deployKameHouseMobile
  cleanUpMavenRepository
  checkForDeploymentErrors
}

setKameHouseDeploymentParameters() {
  loadDockerContainerEnv
  DEPLOYMENT_DIR="${TOMCAT_DIR}/webapps"
  if [ -n "${MODULE_SHORT}" ]; then
    if [ "${MODULE_SHORT}" == "admin" ] ||
       [ "${MODULE_SHORT}" == "media" ] ||
       [ "${MODULE_SHORT}" == "tennisworld" ] ||
       [ "${MODULE_SHORT}" == "testmodule" ] ||
       [ "${MODULE_SHORT}" == "ui" ] ||
       [ "${MODULE_SHORT}" == "vlcrc" ]; then
      DEPLOY_TO_TOMCAT=true
    fi
  else
    DEPLOY_TO_TOMCAT=true
  fi
}

setKameHouseBuildVersion() {
  KAMEHOUSE_BUILD_VERSION=`getKameHouseBuildVersion`
  log.trace "KAMEHOUSE_BUILD_VERSION=${KAMEHOUSE_BUILD_VERSION}"
}

deployKameHouseShell() {
  if [[ -z "${MODULE_SHORT}" || "${MODULE_SHORT}" == "shell" ]]; then
    log.info "Deploying ${COL_PURPLE}kamehouse-shell${COL_DEFAULT_LOG}"
    chmod a+x kamehouse-shell/bin/kamehouse/install-kamehouse-shell.sh
    ./kamehouse-shell/bin/kamehouse/install-kamehouse-shell.sh -l ${LOG_LEVEL}
    checkCommandStatus "$?" "An error occurred deploying kamehouse-shell"

    log.info "Finished deploying ${COL_PURPLE}kamehouse-shell${COL_DEFAULT_LOG}"

    if [ "${MODULE_SHORT}" == "shell" ]; then
      exitSuccessfully
    fi
  fi
}

deployKameHouseBackend() {
  deployTomcatModules
  deployKameHouseCmd  
}

deployTomcatModules() {
  if ${DEPLOY_TO_TOMCAT}; then
    executeOperationInTomcatManager "stop" ${TOMCAT_PORT} ${MODULE_SHORT}
    executeOperationInTomcatManager "undeploy" ${TOMCAT_PORT} ${MODULE_SHORT}
    deployToTomcat
  fi
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

  log.info "Finished deploying ${COL_PURPLE}${PROJECT}${COL_DEFAULT_LOG} to ${COL_PURPLE}${DEPLOYMENT_DIR}${COL_DEFAULT_LOG}"
  log.info "Execute ${COL_CYAN}\`  tail-log.sh -f tomcat -n 2000  \`${COL_DEFAULT_LOG} to check tomcat startup progress"
}

deployKameHouseCmd() {
  if [[ -z "${MODULE_SHORT}" || "${MODULE_SHORT}" == "cmd" ]]; then
    log.info "Deploying ${COL_PURPLE}kamehouse-cmd${COL_DEFAULT_LOG} to ${COL_PURPLE}${KAMEHOUSE_CMD_DEPLOY_PATH}${COL_DEFAULT_LOG}" 
    mkdir -p ${KAMEHOUSE_CMD_DEPLOY_PATH}
    rm -r -f ${KAMEHOUSE_CMD_DEPLOY_PATH}/kamehouse-cmd
    unzip -o -q kamehouse-cmd/target/kamehouse-cmd-bundle.zip -d ${KAMEHOUSE_CMD_DEPLOY_PATH}/ 
    mv ${KAMEHOUSE_CMD_DEPLOY_PATH}/kamehouse-cmd/bin/kamehouse-cmd.bt ${KAMEHOUSE_CMD_DEPLOY_PATH}/kamehouse-cmd/bin/kamehouse-cmd.bat
    local CMD_VERSION_FILE="${KAMEHOUSE_CMD_DEPLOY_PATH}/kamehouse-cmd/lib/cmd-version.txt"
    echo "buildVersion=${KAMEHOUSE_BUILD_VERSION}" > ${CMD_VERSION_FILE}
    local BUILD_DATE=`date +%Y-%m-%d' '%H:%M:%S`
    echo "buildDate=${BUILD_DATE}" >> ${CMD_VERSION_FILE}
    chmod -R 700 ${KAMEHOUSE_CMD_DEPLOY_PATH}/kamehouse-cmd
    ls -lh ${KAMEHOUSE_CMD_DEPLOY_PATH}/kamehouse-cmd/bin/kamehouse-cmd*
    ls -lh ${KAMEHOUSE_CMD_DEPLOY_PATH}/kamehouse-cmd/lib/kamehouse-cmd*.jar
    checkCommandStatus "$?" "An error occurred deploying kamehouse-cmd"
  fi
}

deployKameHouseMobile() {
  if [[ "${MODULE}" == "kamehouse-mobile" ]]; then
    if [ -f "${KAMEHOUSE_ANDROID_APK_PATH}" ]; then
      uploadKameHouseMobileApkToGDrive
      uploadKameHouseMobileApkToHttpdServer
    else
      log.error "${KAMEHOUSE_ANDROID_APK_PATH} not found. Was the build successful?"
      EXIT_CODE=${EXIT_ERROR}
    fi
  fi
}

uploadKameHouseMobileApkToHttpdServer() {
  log.info "Deploying ${COL_PURPLE}kamehouse-mobile${COL_DEFAULT_LOG} app to kame.com server"
  log.debug "scp -v ${KAMEHOUSE_ANDROID_APK_PATH} ${KAMEHOUSE_MOBILE_APP_USER}@${KAMEHOUSE_MOBILE_APP_SERVER}:${KAMEHOUSE_MOBILE_APP_PATH}/kamehouse.apk"
  scp -v ${KAMEHOUSE_ANDROID_APK_PATH} ${KAMEHOUSE_MOBILE_APP_USER}@${KAMEHOUSE_MOBILE_APP_SERVER}:${KAMEHOUSE_MOBILE_APP_PATH}/kamehouse.apk
  checkCommandStatus "$?" "An error occurred deploying kamehouse-mobile through ssh"

  log.debug "ssh ${KAMEHOUSE_MOBILE_APP_USER}@${KAMEHOUSE_MOBILE_APP_SERVER} -C \"\\\${HOME}/programs/kamehouse-shell/bin/kamehouse/kamehouse-mobile-regenerate-apk-html.sh -b ${KAMEHOUSE_BUILD_VERSION}\""
  ssh ${KAMEHOUSE_MOBILE_APP_USER}@${KAMEHOUSE_MOBILE_APP_SERVER} -C "\${HOME}/programs/kamehouse-shell/bin/kamehouse/kamehouse-mobile-regenerate-apk-html.sh -b ${KAMEHOUSE_BUILD_VERSION}"
  checkCommandStatus "$?" "An error occurred regenerating apk html"
}

uploadKameHouseMobileApkToGDrive() {
  if [ -d "${KAMEHOUSE_MOBILE_GDRIVE_PATH_WIN}" ]; then
    log.info "${COL_PURPLE}Uploading${COL_DEFAULT_LOG} kamehouse-mobile apk ${COL_PURPLE}to google drive${COL_DEFAULT_LOG} folder ${KAMEHOUSE_MOBILE_GDRIVE_PATH_WIN}"
    cp ${KAMEHOUSE_ANDROID_APK_PATH} "${KAMEHOUSE_MOBILE_GDRIVE_PATH_WIN}/kamehouse.apk"
  fi

  if [ -d "${HOME}/GoogleDrive" ]; then
    log.info "Mounting google drive"
    google-drive-ocamlfuse ${HOME}/GoogleDrive
    sleep 8
  fi

  if [ -d "${KAMEHOUSE_MOBILE_GDRIVE_PATH_LIN}" ]; then
    log.info "${COL_PURPLE}Uploading${COL_DEFAULT_LOG} kamehouse-mobile apk ${COL_PURPLE}to google drive${COL_DEFAULT_LOG} folder ${KAMEHOUSE_MOBILE_GDRIVE_PATH_LIN}"
    cp ${KAMEHOUSE_ANDROID_APK_PATH} "${KAMEHOUSE_MOBILE_GDRIVE_PATH_LIN}/kamehouse.apk"
  fi
}

deployKameHouseStatic() {
  deployKameHouseUiStatic
  deployKameHouseGroot
  deployKameHouseMobileStatic
  if ${STATIC_ONLY}; then
    log.info "Finished deploying static code"
    exitSuccessfully    
  fi 
}

deployKameHouseUiStatic() {
  if [[ -z "${MODULE_SHORT}" || "${MODULE_SHORT}" == "ui" ]]; then
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

    log.info "Finished deploying ${COL_PURPLE}kamehouse-ui static content${COL_DEFAULT_LOG}"
  fi
}

deployKameHouseGroot() {
  if [[ -z "${MODULE_SHORT}" || "${MODULE_SHORT}" == "groot" ]]; then
    log.info "Deploying ${COL_PURPLE}kamehouse-groot${COL_DEFAULT_LOG}" 
    local HTTPD_CONTENT_ROOT=`getHttpdContentRoot`
    rm -rf ${HTTPD_CONTENT_ROOT}/kame-house-groot
    mkdir -p ${HTTPD_CONTENT_ROOT}/kame-house-groot
    cp -rf ./kamehouse-groot/dist/kame-house-groot/* ${HTTPD_CONTENT_ROOT}/kame-house-groot/
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

    local GROOT_VERSION_FILE="${HTTPD_CONTENT_ROOT}/kame-house-groot/groot-version.txt"
    echo "buildVersion=${KAMEHOUSE_BUILD_VERSION}" > ${GROOT_VERSION_FILE}
    local BUILD_DATE=`date +%Y-%m-%d' '%H:%M:%S`
    echo "buildDate=${BUILD_DATE}" >> ${GROOT_VERSION_FILE}

    log.info "Finished deploying ${COL_PURPLE}kamehouse-groot${COL_DEFAULT_LOG}"

    if [ "${MODULE_SHORT}" == "groot" ]; then
      exitSuccessfully
    fi
  fi
}

deployKameHouseMobileStatic() {
  if [[ "${MODULE}" == "kamehouse-mobile" ]]; then
    log.info "Deploying ${COL_PURPLE}kamehouse-mobile static content${COL_DEFAULT_LOG}"
    local HTTPD_CONTENT_ROOT=`getHttpdContentRoot`
    rm -rf ${HTTPD_CONTENT_ROOT}/kame-house-mobile
    mkdir -p ${HTTPD_CONTENT_ROOT}/kame-house-mobile
    cp -rf ./kamehouse-mobile/www/kame-house-mobile/* ${HTTPD_CONTENT_ROOT}/kame-house-mobile/
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

    log.info "Finished deploying ${COL_PURPLE}kamehouse-mobile static content${COL_DEFAULT_LOG}"
  fi
}

checkForDeploymentErrors() {
  if [ "${EXIT_CODE}" != "0" ]; then
    log.error "Error executing kamehouse deployment"
    exitProcess ${EXIT_CODE}
  fi
}
