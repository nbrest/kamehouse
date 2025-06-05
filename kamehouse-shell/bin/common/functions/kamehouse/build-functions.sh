getKameHouseBuildVersion() {
  local KAMEHOUSE_RELEASE_VERSION=`grep -e "<version>.*1-KAMEHOUSE-SNAPSHOT</version>" pom.xml | awk '{print $1}'`
  KAMEHOUSE_RELEASE_VERSION=`echo ${KAMEHOUSE_RELEASE_VERSION:9:7}`
  local GIT_COMMIT_HASH=`git rev-parse --short HEAD`
  local BUILD_VERSION="${GIT_COMMIT_HASH}"
  if [ -n "${KAMEHOUSE_RELEASE_VERSION}" ]; then
    BUILD_VERSION=${KAMEHOUSE_RELEASE_VERSION}"-"${BUILD_VERSION}
  fi
  echo "${BUILD_VERSION}"
}

setKameHouseBuildVersion() {
  KAMEHOUSE_BUILD_VERSION=`getKameHouseBuildVersion`
  log.trace "KAMEHOUSE_BUILD_VERSION=${KAMEHOUSE_BUILD_VERSION}"
}

generateBuildInfo() {
  log.debug "Generating kamehouse build-info.json"
  local BUILD_DATE="$(date +%Y-%m-%d' '%H:%M:%S)"
  echo '{ "buildVersion": "'${KAMEHOUSE_BUILD_VERSION}'", "buildDate": "'${BUILD_DATE}'" }' > ./build-info.json
}

deleteGitRepoBuildInfoFiles() {
  cdToRootDirFromModule "kamehouse-mobile"
  log.debug "Removing generated build-info.json from the current git repository"
  rm -f ./build-info.json
  rm -f ./kamehouse-commons-core/src/main/resources/build-info.json
}

buildKameHouseStatic() {
  if ! ${DEPLOY_KAMEHOUSE_STATIC}; then
    log.debug "DEPLOY_KAMEHOUSE_STATIC is false so skip building kamehouse static content"
    return
  fi
  if ${INTEGRATION_TESTS}; then
    log.debug "Running integration tests, skippking static code build"
    return
  fi
  if [[ -z "${MODULE}" ]]; then
    buildKameHouseUiStatic
    return
  fi
  if [[ "${MODULE}" == "kamehouse-ui" ]]; then
    buildKameHouseUiStatic
    return
  fi
  if [[ "${MODULE}" == "kamehouse-mobile" ]]; then
    buildKameHouseUiStatic
    buildKameHouseMobileStatic
    return
  fi
}

checkBuildStaticOnly() {
  if ! ${STATIC_ONLY}; then
    return
  fi 
  if [[ -z "${MODULE}" ]]; then
    log.info "Finished building static code for all modules"
  else 
    log.info "Finished building static code for module ${COL_PURPLE}${MODULE}"
  fi
  exitSuccessfully
}

buildKameHouseUiStatic() {
  if [[ -n "${MODULE_SHORT}" 
    && "${MODULE_SHORT}" != "ui"
    && "${MODULE_SHORT}" != "mobile" ]]; then
    return
  fi
  cdToKameHouseModule "kamehouse-ui"
  log.info "Building ${COL_PURPLE}kamehouse-ui${COL_DEFAULT_LOG} static code"
  log.debug "Cleaning up dist directory"
  rm -rf ./dist/*

  buildFrontendCode

  log.debug "Updating sourcemap relative paths"
  find . -regex ".*.js.map" -type f -exec sed -i "s#../../src/main/typescript#../../../../src/main/typescript#g" {} \;

  log.info "Building kamehouse-ui bundle in dist folder"
  cp -r ./src/main/public/* ./dist
  cp -f ../build-info.json ./dist/
  cdToRootDirFromModule "kamehouse-ui"
}

buildKameHouseMobileStatic() {
  if [[ -n "${MODULE_SHORT}" && "${MODULE_SHORT}" != "mobile" ]]; then
    return
  fi
  cdToKameHouseModule "kamehouse-mobile"
  log.info "Building ${COL_PURPLE}kamehouse-mobile${COL_DEFAULT_LOG} static code"
  log.debug "Cleaning up kamehouse-mobile www directory js files"
  rm -rf ./www/kame-house-mobile
  rm -rf ./www/kamehouse-mobile
  rm -rf ./www/kamehouse-ui

  buildFrontendCode

  log.debug "Updating sourcemap relative paths"
  find . -regex ".*.js.map" -type f -exec sed -i "s#../../../../../../../src/main/typescript#../../../src/main/typescript#g" {} \;
  
  log.info "Building kamehouse-mobile bundle in www folder"
  mkdir -p ./www/kame-house-mobile
  cp -r ./src/main/public/* ./www/kame-house-mobile
  mv ./www/kamehouse-mobile/src/main/typescript/kame-house-mobile/js ./www/kame-house-mobile/js
  mv ./www/kamehouse-mobile/src/main/typescript/kame-house-mobile/kamehouse-mobile/js ./www/kame-house-mobile/kamehouse-mobile/js
  mv ./www/kamehouse-mobile/src/main/typescript/kame-house-mobile/kamehouse-mobile/plugin/js ./www/kame-house-mobile/kamehouse-mobile/plugin/js  
  rm -rf ./www/kamehouse-mobile
  rm -rf ./www/kamehouse-ui
  buildMobileBackendJson
  setMobileKeys
  cdToRootDirFromModule "kamehouse-mobile"
}

buildMobileBackendJson() {
  log.info "Building backend.json for mobile app"
  local BACKEND_JSON_FILE="./www/kame-house-mobile/json/config/backend.json"
  mkdir -p ./www/kame-house-mobile/json/config
  echo '{' > ${BACKEND_JSON_FILE}
  echo '  "selected" : "'${MOBILE_BACKEND_SELECTED_SERVER}'",' >> ${BACKEND_JSON_FILE}
  echo '  "servers": [' >> ${BACKEND_JSON_FILE}
  while read KAMEHOUSE_CONFIG_ENTRY; do
    if [ -n "${KAMEHOUSE_CONFIG_ENTRY}" ]; then
      local KAMEHOUSE_CONFIG_ENTRY_SPLIT=$(echo ${KAMEHOUSE_CONFIG_ENTRY} | tr "," "\n")
      local KAMEHOUSE_CONFIG=()
      while read KAMEHOUSE_CONFIG_ENTRY_FIELD; do
        if [ -n "${KAMEHOUSE_CONFIG_ENTRY_FIELD}" ]; then
          KAMEHOUSE_CONFIG+=("${KAMEHOUSE_CONFIG_ENTRY_FIELD}")
        fi
      done <<< ${KAMEHOUSE_CONFIG_ENTRY_SPLIT}
      local SKIP_SSL_CHECK=false
      if [ "${KAMEHOUSE_CONFIG[4]}" == "--skip-ssl-check" ]; then
        SKIP_SSL_CHECK=true
      fi
      local IS_URL_EDITABLE=false
      if [ "${KAMEHOUSE_CONFIG[5]}" == "--url-editable" ]; then
        IS_URL_EDITABLE=true
      fi
      echo '    {' >> ${BACKEND_JSON_FILE}
      echo '      "name" : "'${KAMEHOUSE_CONFIG[0]}'",' >> ${BACKEND_JSON_FILE}
      echo '      "url" : "'${KAMEHOUSE_CONFIG[1]}'",' >> ${BACKEND_JSON_FILE}
      echo '      "username" : "'${KAMEHOUSE_CONFIG[2]}'",' >> ${BACKEND_JSON_FILE}
      echo '      "password" : "'${KAMEHOUSE_CONFIG[3]}'",' >> ${BACKEND_JSON_FILE}
      echo '      "isLoggedIn" : false,' >> ${BACKEND_JSON_FILE}
      echo '      "skipSslCheck" : '${SKIP_SSL_CHECK}',' >> ${BACKEND_JSON_FILE}
      echo '      "isUrlEditable" : '${IS_URL_EDITABLE} >> ${BACKEND_JSON_FILE}
      echo '    },' >> ${BACKEND_JSON_FILE}
    fi
  done <<< ${MOBILE_BACKEND_SERVERS} 
  sed -i '$ d' ${BACKEND_JSON_FILE}
  echo '    }' >> ${BACKEND_JSON_FILE}
  echo '  ]' >> ${BACKEND_JSON_FILE}
  echo '}' >> ${BACKEND_JSON_FILE}

  log.debug "BACKEND_JSON_FILE: ${BACKEND_JSON_FILE}"
} 

buildFrontendCode() {
  log.debug "npm install ; npm run build ; npm run scan"
  npm install

  npm run build
  if [ "$?" != "0" ]; then
    log.warn "npm run build failed, running manually tsc"
    tsc
  fi  
  
  npm run scan
  if [ "$?" != "0" ]; then
    log.warn "npm run scan failed, running manually tsc --build --force --verbose --pretty"
    tsc --build --force --verbose --pretty
  fi  
}

buildKameHouseBackend() {
  RUN_MAVEN_COMMAND=false
  if ${DEPLOY_KAMEHOUSE_TOMCAT_MODULES}; then
    RUN_MAVEN_COMMAND=true
  fi
  if ${DEPLOY_KAMEHOUSE_CMD}; then
    RUN_MAVEN_COMMAND=true
  fi
  if [ "${MODULE_SHORT}" == "mobile" ] ||
     [ "${MODULE_SHORT}" == "shell" ] ||
     [ "${MODULE_SHORT}" == "groot" ]; then
    RUN_MAVEN_COMMAND=false
  fi
  if ! ${RUN_MAVEN_COMMAND}; then
    log.debug "RUN_MAVEN_COMMAND is false so skip running maven command"
    return
  fi
  source ${HOME}/programs/kamehouse-shell/bin/kamehouse/set-java-home.sh --override --log
  log.info "Building ${COL_PURPLE}${PROJECT}${COL_DEFAULT_LOG} backend with profile ${COL_PURPLE}${MAVEN_PROFILE}${COL_DEFAULT_LOG}"
  exportBuildInfoToCommonsCore
  buildMavenCommand
  executeMavenCommand
  cleanLogsInGitRepoFolder
}

exportBuildInfoToCommonsCore() {
  cdToRootDirFromModule "kamehouse-mobile"
  log.info "Exporting build info to commons-core"
  cp -f ./build-info.json ./kamehouse-commons-core/src/main/resources/
}

buildMavenCommand() {
  MAVEN_COMMAND="mvn clean install -Dstyle.color=always -P ${MAVEN_PROFILE}"

  if ${FAST_BUILD}; then
    log.info "Executing fast build. Skipping checkstyle, findbugs and tests"
    MAVEN_COMMAND="${MAVEN_COMMAND} -Dmaven.test.skip=true -Dcheckstyle.skip=true -Dspotbugs.skip=true"
  fi

  if ${INTEGRATION_TESTS}; then
    if ${CONTINUE_INTEGRATION_TESTS_ON_ERRORS}; then
      MAVEN_COMMAND="mvn clean test-compile failsafe:integration-test -Dstyle.color=always -P ${MAVEN_PROFILE}"
    else
      MAVEN_COMMAND="mvn clean test-compile failsafe:integration-test failsafe:verify -Dstyle.color=always -P ${MAVEN_PROFILE}"
    fi
  fi

  if [ -n "${MODULE}" ]; then
    log.info "Building module ${COL_PURPLE}${MODULE}"
    if ${RESUME_BUILD}; then
      log.info "Resuming from last build"
      MAVEN_COMMAND="${MAVEN_COMMAND} -rf :${MODULE}"
    else
      MAVEN_COMMAND="${MAVEN_COMMAND} -pl :${MODULE} -am"
    fi
  else
    log.info "Building all modules"
  fi
  MAVEN_COMMAND="${MAVEN_COMMAND} -Dfilter.kamehouse-r2d2.mac=${KAMEHOUSE_R2D2_MAC} "
  MAVEN_COMMAND="${MAVEN_COMMAND} -Dfilter.kamehouse-r2d2.broadcast=${KAMEHOUSE_R2D2_BROADCAST} "
  MAVEN_COMMAND="${MAVEN_COMMAND} -Dfilter.playlists.path=${PLAYLISTS_PATH} "
  MAVEN_COMMAND="${MAVEN_COMMAND} -Dfilter.jdbc.password=${MARIADB_PASS_KAMEHOUSE} "
}

executeMavenCommand() {
  local MAVEN_COMMAND_MASKED="${MAVEN_COMMAND}"
  MAVEN_COMMAND_MASKED="`sed 's#filter.jdbc.password=.* #filter.jdbc.password=**** #' <<<"${MAVEN_COMMAND_MASKED}"`"
  log.info "${MAVEN_COMMAND_MASKED}"
  ${MAVEN_COMMAND}
  checkCommandStatus "$?" "An error occurred building the project ${PROJECT_DIR}"
}

buildKameHouseMobile() {
  if [[ "${MODULE}" != "kamehouse-mobile" ]]; then
    return
  fi
  if ! ${DEPLOY_KAMEHOUSE_MOBILE}; then
    log.warn "DEPLOY_KAMEHOUSE_MOBILE is false so skip building kamehouse-mobile"
    return
  fi    
  log.info "${COL_PURPLE}Building kamehouse-mobile app"
  setKameHouseMobileApkPath
  syncStaticFilesOnMobile
  cdToKameHouseModule "kamehouse-mobile"
  setLinuxBuildEnv
  source ${HOME}/programs/kamehouse-shell/bin/kamehouse/set-java-home-for-mobile.sh
  prepareCordovaProject
  setMobileKeys
  setBuildInfoOnMobile
  updateConfigWithGitHash
  buildCordovaProject
  source ${HOME}/programs/kamehouse-shell/bin/kamehouse/set-java-home.sh --override --log
  resetMobileAppConfig
  cdToRootDirFromModule "kamehouse-mobile"
  deleteStaticFilesOnMobile
  cleanLogsInGitRepoFolder
}

setLinuxBuildEnv() {
  if ! ${IS_LINUX_HOST}; then
    return
  fi
  log.info "Setting android build env for linux"
  export ANDROID_SDK_ROOT=${HOME}/Android/Sdk
  export PATH=${PATH}:${ANDROID_SDK_ROOT}/platform-tools:${ANDROID_SDK_ROOT}/cmdline-tools/latest/bin
}

prepareCordovaProject() {
  log.debug "npm install ; cordova clean ; cordova prepare"
  npm install
  cordova clean
  cordova prepare
}

# runs from root directory of kamehouse project
syncStaticFilesOnMobile() {
  ${HOME}/programs/kamehouse-shell/bin/kamehouse/kamehouse-mobile-resync-static-files.sh -c
}

setMobileKeys() {
  log.debug "Setting mobile encryption key"
  echo "${KAMEHOUSE_MOBILE_ENCRYPTION_KEY}" > www/kame-house-mobile/encryption.key
}

setBuildInfoOnMobile() {
  log.debug "Setting ui build info on mobile build"
  cp -f ../build-info.json ./www/kame-house/
}

buildCordovaProject() {
  log.info "Executing: ${COL_PURPLE}cordova build android"
  cordova build android
  checkCommandStatus "$?" "An error occurred building kamehouse-mobile"

  log.info "Built apk file status"
  ls -lh "${KAMEHOUSE_ANDROID_APK_PATH}"
}

deleteStaticFilesOnMobile() {
  ${HOME}/programs/kamehouse-shell/bin/kamehouse/kamehouse-mobile-resync-static-files.sh -c -d
}

updateConfigWithGitHash() {
  log.info "Setting git commit hash on config.xml"
  GIT_COMMIT_HASH=`git rev-parse --short HEAD`
  cp -f source-config.xml config.xml
  sed -i "s#GIT_COMMIT_HASH#${GIT_COMMIT_HASH}#" config.xml
  cat config.xml | grep "<widget id="
}

resetMobileAppConfig() {
  log.debug "Removing config.xml after build"
  rm -f config.xml
}
