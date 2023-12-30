#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 99
fi
source ${HOME}/programs/kamehouse-shell/bin/common/video-playlists/video-playlists-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing video-playlists-functions.sh\033[0;39m"
  exit 99
fi

PROJECT_DIR="${HOME}/git/kamehouse-video-subtitles"
BASE_SUBS_DIR=${PROJECT_DIR}/subtitles
BASE_SOURCE_SUBS_DIR=/n/

GIT_REMOTE=all
GIT_BRANCH=dev

SUB_TYPES_REGEX="\.srt$\|\.SRT$\|\.ass$\|\.ASS$"

mainProcess() {
  cd ${PROJECT_DIR}
  checkMediaServer
  pullChangesFromGit
  deleteExistingSubtitles
  resyncSubtitleFiles "anime"
  resyncSubtitleFiles "cartoons"
  resyncSubtitleFiles "movies"
  resyncSubtitleFiles "series"

  pushChangesToGit
}

deleteExistingSubtitles() {
  log.info "Deleting existing subtitle files"
  rm -rf ${BASE_SUBS_DIR}
  mkdir -p ${BASE_SUBS_DIR}
}

resyncSubtitleFiles() {
  local BASE_DIR=$1
  log.info "Resyncing subtitles on dir ${COL_PURPLE}${BASE_DIR}"
  find ${BASE_SOURCE_SUBS_DIR}/${BASE_DIR} | grep --ignore-case -e "${SUB_TYPES_REGEX}" | grep --ignore-case -v -e "sample" | while read FILE; do
    local FILE_NAME=${FILE#${BASE_SOURCE_SUBS_DIR}}
    local SUBTITLE_SUBDIR="$(dirname "${FILE_NAME}")"
    log.debug "Resyncing subtitle ${COL_PURPLE}${FILE}"
    mkdir -p "${BASE_SUBS_DIR}${SUBTITLE_SUBDIR}"
    cp -f "${FILE}" "${BASE_SUBS_DIR}${FILE_NAME}"
  done
}

checkMediaServer() {
  if [ "${HOSTNAME}" != "${MEDIA_SERVER}" ]; then
    log.error "This script can only run in ${MEDIA_SERVER}. Trying to run in ${HOSTNAME}"
    exitProcess ${EXIT_ERROR}
  fi
}

pullChangesFromGit() {
  gitCdCheckoutAndPull "${PROJECT_DIR}" "${GIT_REMOTE}" "${GIT_BRANCH}"
}

pushChangesToGit() {
  gitCdCommitAllChangesAndPush "${PROJECT_DIR}" "${GIT_REMOTE}" "${GIT_BRANCH}" "Updated video subtitles"
}

main "$@"
