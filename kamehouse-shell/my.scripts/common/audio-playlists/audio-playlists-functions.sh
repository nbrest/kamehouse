# Common audio-playlists variables
LOG_PROCESS_TO_FILE=true

PROJECT_DIR="${HOME}/git/kamehouse-audio-playlists/playlists"
GIT_REMOTE=all
GIT_BRANCH=dev
AUDIO_SERVER="niko-w"

PATH_BASE_SOURCE="D:\\\niko9enzo\\\mp3"
PATH_BASE_DEST=""

PATH_BASE_N2_SOURCE="Z:\\\mp3"
PATH_BASE_N2_DEST=""

PATH_PLS_SOURCE=${PROJECT_DIR}/windows
PATH_PLS_DEST=""

# Common audio-playlists functions
mainProcess() {
  validateVariables
  removeDestPlaylists
  copySourcePlaylists
  replaceDestPaths
}

validateVariables() {
  if [ -z ${PATH_PLS_SOURCE} ]; then
    log.error "PATH_PLS_SOURCE is not set"
    exitProcess 1
  fi

  if [ -z ${PATH_PLS_DEST} ]; then
    log.error "PATH_PLS_DEST is not set"
    exitProcess 1
  fi
}

removeDestPlaylists() {
  log.info "removeDestPlaylists"
  rm -r ${PATH_PLS_DEST}/*.m3u
}

copySourcePlaylists() {
  log.info "copySourcePlaylists"
  mkdir -p ${PATH_PLS_DEST}
  cp -r ${PATH_PLS_SOURCE}/* ${PATH_PLS_DEST}/
  checkCommandStatus "$?" 
}

replaceDestPaths() {
  log.info "replaceDestPaths"
  PLAYLISTS_DEST=`find ${PATH_PLS_DEST} | grep -e  "m3u\|M3U" | sort`
  while read FILE; do
    replaceDestPath ${FILE} &
  done < <(echo "${PLAYLISTS_DEST}")
  checkCommandStatus "$?" 
  log.info "Waiting for all background replace processes to finish"
  jobs -l
  wait
}

replaceDestPath() {
  local FILE=$1
  log.info "Updating file ${COL_PURPLE}${FILE}"
  # Ig: I to ignore case, needed here
  sed -i "s#${PATH_BASE_SOURCE}#${PATH_BASE_DEST}#Ig" "${FILE}"
  sed -i "s#${PATH_BASE_N2_SOURCE}#${PATH_BASE_N2_DEST}#Ig" "${FILE}"
  sed -i "s#\\\#/#Ig" "${FILE}"
  checkCommandStatus "$?"

  local FILE_CONTENT=`cat ${FILE}`
  local TEMP_FILE=${FILE}".tmp"
  echo "#EXTM3U" > ${TEMP_FILE}
  echo -e "${FILE_CONTENT}" | while read PLAYLIST_ENTRY; do
    local FIRST_CHAR=${PLAYLIST_ENTRY:0:1}
    if [ "${FIRST_CHAR}" != "#" ]; then
      local PLAYLIST_ENTRY_SHORT=${PLAYLIST_ENTRY#"$PATH_BASE_DEST"}
      echo "#EXTINF:0,${PLAYLIST_ENTRY_SHORT}" >> ${TEMP_FILE}
      echo "${PLAYLIST_ENTRY}" >> ${TEMP_FILE}
    fi
  done
  mv ${TEMP_FILE} ${FILE}
}

deleteExistingM3uFiles() {
  local OS=$1
  log.info "Deleting existing m3u files for os ${OS}"
  cd ${PROJECT_DIR}/${OS}
  local M3U_FILES=`find . | grep -e ".m3u" | sort`;
  while read M3U_FILE; do
    rm -f ${M3U_FILE}
  done <<< ${M3U_FILES}
}

checkAudioServer() {
  if [ "${HOSTNAME}" != "${AUDIO_SERVER}" ]; then
    log.error "This script can only run in ${AUDIO_SERVER}. Trying to run in ${HOSTNAME}"
    exitProcess 1
  fi
}

pullChangesFromGit() {
  gitCdCheckoutAndPull "${PROJECT_DIR}" "${GIT_REMOTE}" "${GIT_BRANCH}"
}

pushChangesToGit() {
  gitCdCommitAllChangesAndPush "${PROJECT_DIR}" "${GIT_REMOTE}" "${GIT_BRANCH}" "Updated audio playlists"
}
