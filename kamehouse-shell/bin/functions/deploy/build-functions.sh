getKameHouseBuildVersion() {
  local KAMEHOUSE_RELEASE_VERSION=`getKameHouseReleaseVersion`
  setGitCommitHash
  local BUILD_VERSION="${GIT_COMMIT_HASH}"
  if [ -n "${KAMEHOUSE_RELEASE_VERSION}" ]; then
    BUILD_VERSION=${KAMEHOUSE_RELEASE_VERSION}"-"${BUILD_VERSION}
  fi
  echo "${BUILD_VERSION}"
}

getKameHouseReleaseVersion() {
  local GRADLE_PROJECT_VERSION=`${GRADLE_WRAPPER} --quiet --console=plain printVersion 2>/dev/null | tr -d "\r" | grep -E "^[0-9]+(\.[0-9]+)*-KAMEHOUSE-SNAPSHOT$" | head -1 | sed -e "s/-KAMEHOUSE-SNAPSHOT$//"`
  echo "${GRADLE_PROJECT_VERSION}"
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
  if ! ${DEPLOY_KAMEHOUSE_UI}; then
    log.debug "DEPLOY_KAMEHOUSE_UI is false so skip building kamehouse ui"
    if ! ${CI_BUILD}; then
      return
    fi
  fi
  if ${INTEGRATION_TESTS}; then
    log.debug "Running integration tests, skippking ui static code build"
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

buildKameHouseUiStatic() {
  if [[ -n "${MODULE_SHORT}" 
    && "${MODULE_SHORT}" != "ui"
    && "${MODULE_SHORT}" != "mobile" ]]; then
    return
  fi
  cdToKameHouseModule "kamehouse-ui"
  log.info "Building ${COL_PURPLE}kamehouse-ui${COL_DEFAULT_LOG} static code"
  log.debug "Cleaning up dist directory"
  if ${USE_SUDO_FOR_UI_BUILD}; then
    sudo rm -rf ./dist
    sudo mkdir ./dist
    sudo chown -R ${USER}:${USER} ./dist
  else
    rm -rf ./dist
    mkdir ./dist
  fi

  buildFrontendCode

  log.debug "Updating sourcemap relative paths"
  find . -regex ".*.js.map" -type f -exec sed -i "s#../../src/main/typescript#../../../../src/main/typescript#g" {} \;

  log.info "Building kamehouse-ui bundle in dist folder"
  if ${USE_SUDO_FOR_UI_BUILD}; then
    sudo cp -r ./src/main/public/* ./dist
    sudo cp -f ../build-info.json ./dist/
    sudo chown -R ${USER}:${USER} ./dist
  else
    cp -r ./src/main/public/* ./dist
    cp -f ../build-info.json ./dist/
  fi
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
  trimPathForCmdExe
  npm install
  checkCommandStatus "$?" "An error occurred installing the npm dependencies"

  npm run build
  checkCommandStatus "$?" "An error occurred building the frontend code"

  npm run scan
  checkCommandStatus "$?" "An error occurred scanning the frontend code"
}

buildKameHouseBackend() {
  RUN_BUILD_COMMAND=false
  if ${DEPLOY_KAMEHOUSE_TOMCAT_MODULES}; then
    RUN_BUILD_COMMAND=true
  fi
  if ${DEPLOY_KAMEHOUSE_CMD}; then
    RUN_BUILD_COMMAND=true
  fi
  if [ "${MODULE_SHORT}" == "mobile" ] ||
     [ "${MODULE_SHORT}" == "groot" ] ||
     [ "${MODULE_SHORT}" == "shell" ] ||
     [ "${MODULE_SHORT}" == "ui" ]; then
    RUN_BUILD_COMMAND=false
  fi
  if ! ${RUN_BUILD_COMMAND}; then
    if ! ${CI_BUILD}; then
      log.debug "RUN_BUILD_COMMAND is false so skip running the build command"
      return
    fi
  fi
  source ${HOME}/programs/kamehouse-shell/bin/deploy/set-java-home.sh --override --log
  log.info "Building ${COL_PURPLE}${PROJECT}${COL_DEFAULT_LOG} backend with build profile ${COL_PURPLE}${BUILD_PROFILE}${COL_DEFAULT_LOG}"
  exportBuildInfoToCommonsCore
  buildGradleCommand
  executeGradleCommand
  cleanLogsInGitRepoFolder
}

exportBuildInfoToCommonsCore() {
  cdToRootDirFromModule "kamehouse-mobile"
  log.info "Exporting build info to commons-core"
  cp -f ./build-info.json ./kamehouse-commons-core/src/main/resources/
}

buildGradleCommand() {
  local GRADLE_LIFECYCLE_TASK="build"
  local GRADLE_COMMON_ARGS="--console=plain -PkamehouseProfile=${BUILD_PROFILE}"

  if ${INTEGRATION_TESTS}; then
    GRADLE_LIFECYCLE_TASK="integrationTest"
    if ${CONTINUE_INTEGRATION_TESTS_ON_ERRORS}; then
      GRADLE_COMMON_ARGS="${GRADLE_COMMON_ARGS} -PkamehouseIgnoreIntegrationTestFailures=true"
    fi
  fi

  GRADLE_COMMAND="${GRADLE_WRAPPER} clean ${GRADLE_LIFECYCLE_TASK} ${GRADLE_COMMON_ARGS}"

  if ${FAST_BUILD}; then
    log.info "Executing fast build. Skipping checkstyle, findbugs and tests"
    GRADLE_COMMAND="${GRADLE_COMMAND} ${GRADLE_SKIP_CHECKS_ARGS}"
  fi

  if [ -n "${MODULE}" ]; then
    log.info "Building module ${COL_PURPLE}${MODULE}"
    if ${RESUME_BUILD}; then
      log.info "Resuming from last build"
      GRADLE_COMMAND="${GRADLE_COMMAND} `getGradleTasksFromModuleOnwards ${MODULE} ${GRADLE_LIFECYCLE_TASK}`"
    else
      GRADLE_COMMAND="${GRADLE_COMMAND} :${MODULE}:${GRADLE_LIFECYCLE_TASK}"
    fi
  else
    log.info "Building all modules"
  fi
  GRADLE_COMMAND="${GRADLE_COMMAND} -Dfilter.kamehouse-server.mac=${KAMEHOUSE_SERVER_MAC} "
  GRADLE_COMMAND="${GRADLE_COMMAND} -Dfilter.kamehouse-server.broadcast=${KAMEHOUSE_SERVER_BROADCAST} "
  GRADLE_COMMAND="${GRADLE_COMMAND} -Dfilter.playlists.path=${PLAYLISTS_PATH} "
  GRADLE_COMMAND="${GRADLE_COMMAND} -Dfilter.jdbc.password=${MARIADB_PASS_KAMEHOUSE} "
}

executeGradleCommand() {
  local GRADLE_COMMAND_MASKED="${GRADLE_COMMAND}"
  GRADLE_COMMAND_MASKED="`sed 's#filter\.jdbc\.password=[^ ]*#filter.jdbc.password=****#g' <<<"${GRADLE_COMMAND_MASKED}"`"
  log.info "${GRADLE_COMMAND_MASKED}"
  ${GRADLE_COMMAND}
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
  setGradleForAndroidInPath
  source ${HOME}/programs/kamehouse-shell/bin/deploy/set-java-home-for-mobile.sh
  updateConfigWithGitHash
  prepareCordovaProject
  setMobileKeys
  setBuildInfoOnMobile
  buildCordovaProject
  source ${HOME}/programs/kamehouse-shell/bin/deploy/set-java-home.sh --override --log
  restoreGradlePathForJavaBuild
  resetMobileAppConfig
  cdToRootDirFromModule "kamehouse-mobile"
  deleteStaticFilesOnMobile
  cleanLogsInGitRepoFolder
  runKameHouseMobileTests
}

runKameHouseMobileTests() {
  if ${FAST_BUILD}; then
    log.info "Fast build. Skipping kamehouse-mobile java unit tests"
    return
  fi
  if ${INTEGRATION_TESTS}; then
    log.debug "Running integration tests. Skipping kamehouse-mobile java unit tests"
    return
  fi
  log.info "Running ${COL_PURPLE}kamehouse-mobile${COL_DEFAULT_LOG} java unit tests"
  ${GRADLE_WRAPPER} :kamehouse-mobile:check --console=plain -PkamehouseProfile=${BUILD_PROFILE}
  checkCommandStatus "$?" "An error occurred running kamehouse-mobile java unit tests"
}

setLinuxBuildEnv() {
  if ! ${IS_LIN_HOST}; then
    return
  fi
  log.info "Setting android build env for linux"
  export ANDROID_SDK_ROOT=${HOME}/Android/Sdk
  export ANDROID_HOME=${ANDROID_SDK_ROOT}
  export PATH=${PATH}:${ANDROID_SDK_ROOT}/platform-tools:${ANDROID_SDK_ROOT}/cmdline-tools/latest/bin
}

prepareCordovaProject() {
  log.debug "npm install ; remove cordova platforms and plugins ; cordova prepare"
  trimPathForCmdExe
  npm install
  rm -rf platforms plugins
  cordova prepare
  checkCommandStatus "$?" "An error occurred preparing the cordova project for kamehouse-mobile"
}

# runs from root directory of kamehouse project
syncStaticFilesOnMobile() {
  ${HOME}/programs/kamehouse-shell/bin/mobile/kamehouse-mobile-resync-static-files.sh -c
}

setGradleForAndroidInPath() {
  local GRADLE_DIR=$(find "${HOME}/.gradle/wrapper/dists" -maxdepth 3 -type d -path "*/gradle-${GRADLE_ANDROID_VERSION}-*/gradle-${GRADLE_ANDROID_VERSION}" 2>/dev/null | xargs -I{} ls -dt {} 2>/dev/null | head -1)
  if [ -n "${GRADLE_DIR}" ] && [ -d "${GRADLE_DIR}/bin" ]; then
    export PATH="${GRADLE_DIR}/bin:${PATH}"
    GRADLE_ANDROID_PATH_ADDED="${GRADLE_DIR}/bin"
    log.debug "Using gradle for android from dir: ${GRADLE_ANDROID_PATH_ADDED}"
    return
  fi
  log.error "Unable to find gradle for android distribution"
}

restoreGradlePathForJavaBuild() {
  log.debug "Removing gradle for android from PATH"
  if [ -n "${GRADLE_ANDROID_PATH_ADDED}" ]; then
    export PATH=$(echo "${PATH}" | sed -e "s#${GRADLE_ANDROID_PATH_ADDED}:##" -e "s#:${GRADLE_ANDROID_PATH_ADDED}##")
    GRADLE_ANDROID_PATH_ADDED=""
  fi
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
  trimPathForCmdExe
  cordova build android
  checkCommandStatus "$?" "An error occurred building kamehouse-mobile"

  log.info "Built apk file status"
  ls -lh "${KAMEHOUSE_ANDROID_APK_PATH}"
}

deleteStaticFilesOnMobile() {
  ${HOME}/programs/kamehouse-shell/bin/mobile/kamehouse-mobile-resync-static-files.sh -c -d
}

updateConfigWithGitHash() {
  log.info "Setting git commit hash on config.xml"
  setGitCommitHash
  cp -f source-config.xml config.xml
  sed -i "s#GIT_COMMIT_HASH#${GIT_COMMIT_HASH}#" config.xml
  cat config.xml | grep "<widget id="
}

resetMobileAppConfig() {
  log.debug "Removing config.xml after build"
  rm -f config.xml
}
