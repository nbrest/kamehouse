RELEASE_VERSION=v6.05
DOCKER_SSH_PORT=18222
DOCKER_HTTP_PORT=18280
DOCKER_IMAGE_TAG=java11-release
DOCKER_CONTAINER_USERNAME=`cat ${HOME}/programs/kamehouse-shell/bin/kamehouse/docker/release/java11-release/Dockerfile | grep "ARG KAMEHOUSE_USERNAME=" | awk -F'=' '{print $2}'`
if [ -z "${DOCKER_CONTAINER_USERNAME}" ]; then
  log.error "Could not set DOCKER_CONTAINER_USERNAME from Dockerfile"
  exit 1
fi 
