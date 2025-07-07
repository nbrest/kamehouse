#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/docker-functions.sh
if [ "$?" != "0" ]; then echo "Error importing docker-functions.sh" ; exit 99 ; fi

initScriptEnv() {
  RUN_BUILD_STEP_FOR_RELEASE_TAG=false
  BUILD_DATE_KAMEHOUSE="0000-00-00"
  DOCKER_COMMAND="docker buildx build"
  PLATFORM="linux/amd64,linux/arm64/v8"
  ACTION="--push"
  USE_CURRENT_DIR=true
}

mainProcess() {
  setKameHouseRootProjectDir
  if ${RUN_BUILD_STEP_FOR_RELEASE_TAG}; then
    runDockerBuildCommand
  else
    if ${DOCKER_BUILD_RELEASE_TAG}; then
      buildReleaseTag
    else
      buildLatestImage
    fi
  fi
}

buildLatestImage() {
  log.info "Building docker image nbrest/kamehouse:${DOCKER_IMAGE_TAG} and ${COL_PURPLE}push it to docker hub${COL_DEFAULT_LOG}"
  runDockerBuildCommand
}

runDockerBuildCommand() {
  mkdir -p ${HOME}/.docker-cache
  log.debug "docker buildx create --platform ${PLATFORM} --name kamehouse-builder --bootstrap --use"
  docker buildx create --platform ${PLATFORM} --name kamehouse-builder --bootstrap --use

  DOCKER_COMMAND=${DOCKER_COMMAND}"\
    --cache-from=type=local,src=${HOME}/.docker-cache \
    --cache-to=type=local,dest=${HOME}/.docker-cache \
    --progress plain
    --build-arg BUILD_DATE_KAMEHOUSE=\"${BUILD_DATE_KAMEHOUSE}\" \
    --build-arg DOCKER_IMAGE_BASE=${DOCKER_IMAGE_BASE} \
    --build-arg DOCKER_IMAGE_TAG=${DOCKER_IMAGE_TAG} \
    --platform=${PLATFORM} \
    ${ACTION} \
    -t nbrest/kamehouse:${DOCKER_IMAGE_TAG} .
  "
  log.debug "${DOCKER_COMMAND}"
  ${DOCKER_COMMAND}
  checkCommandStatus "$?" "Error building the kamehouse docker image" 
}

buildReleaseTag() {
  log.info "Building docker image nbrest/kamehouse:${DOCKER_IMAGE_TAG} locally"
  setupKameHouseShellForReleaseTag
  ${HOME}/programs/kamehouse-shell/bin/kamehouse/docker/docker-build-kamehouse.sh -t ${DOCKER_IMAGE_TAG} -r -b
  restoreKameHouseShell
}

setupKameHouseShellForReleaseTag() {
  log.info "Setting up kamehouse shell for release tag"
  cd ${HOME}/git
  rm -r -f kamehouse-release-${DOCKER_IMAGE_TAG}
  git clone https://github.com/nbrest/kamehouse.git kamehouse-release-${DOCKER_IMAGE_TAG}
  cd kamehouse-release-${DOCKER_IMAGE_TAG}
  git checkout tags/${DOCKER_IMAGE_TAG} -b ${DOCKER_IMAGE_TAG}
  log.debug "Installing kamehouse-shell from `pwd`"
  chmod a+x ./kamehouse-shell/bin/kamehouse/shell/install-kamehouse-shell.sh
  ./kamehouse-shell/bin/kamehouse/shell/install-kamehouse-shell.sh
}

restoreKameHouseShell() {
  log.info "Restoring kamehouse shell"
  cd ${HOME}/git
  rm -r -f kamehouse-release-${DOCKER_IMAGE_TAG}
  cd kamehouse
  log.debug "Installing kamehouse-shell from `pwd`"
  chmod a+x ./kamehouse-shell/bin/kamehouse/shell/install-kamehouse-shell.sh
  ./kamehouse-shell/bin/kamehouse/shell/install-kamehouse-shell.sh 
}

parseArguments() {
  parseDockerTag "$@"

  local OPTIONS=("$@")
  for i in "${!OPTIONS[@]}"; do
    local CURRENT_OPTION="${OPTIONS[i]}"
    if [ "${CURRENT_OPTION:0:1}" != "-" ]; then
      continue
    fi
    local CURRENT_OPTION_ARG="${OPTIONS[i+1]}"
    case "${CURRENT_OPTION}" in
      -t)
        # parsed in a previous parse options function 
        ;;
      -b)
        BUILD_DATE_KAMEHOUSE=$(date +%Y-%m-%d'_'%H:%M:%S)
        ;;
      -r)
        RUN_BUILD_STEP_FOR_RELEASE_TAG=true
        ;;
      -?|-??*)
        parseInvalidArgument "${CURRENT_OPTION}"
        ;;        
    esac
  done    
}

setEnvFromArguments() {
  setEnvForDockerTag 
}

printHelpOptions() {
  addHelpOption "-b" "force build of kamehouse. Skip docker cache from build step"
  addHelpOption "-r" "run only the build step for the release tag. Ignore. Used internally recursively by the script"
  printDockerTagOption
}

main "$@"
