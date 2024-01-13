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
source ${HOME}/.kamehouse/.shell/.cred

GIT_REMOTE=all
GIT_BRANCH=dev

EMPTY_DIRS_RM_FILE=${HOME}/temp/create-all-video-playlists-rm-empty-dirs.sh
EMPTY_DIRS_CHECK_FILE=${HOME}/temp/create-all-video-playlists-check-empty-dirs.sh

declare -a toAt=("[^a-zA-Z0-9:/\\_\. ]" "\]" "@ " " @" "@ @" "@_" "_@" "@_@" " @ ")
declare -a toUnderscore=(" +" "_+")
declare -a toDash=("@+")
declare -a toDot=("-+\." "\.-+" "_+\." "\._+" "\.+")
declare -a toSlash=("/-" "-/")
declare -a toRemove=("-$")

mainProcess() {
  initEmptyDirsFiles
  removeSpecialCharsInAllFilenames
  exitMessage
}

initEmptyDirsFiles() {
  mkdir -p ${HOME}/temp
  echo "#!/bin/bash" > ${EMPTY_DIRS_RM_FILE}
  echo "" >> ${EMPTY_DIRS_RM_FILE}

  echo "#!/bin/bash" > ${EMPTY_DIRS_CHECK_FILE}
  echo "" >> ${EMPTY_DIRS_CHECK_FILE}  
}

removeSpecialCharsInAllFilenames() {
  removeSpecialCharsInFilenames "/n/anime"
  removeSpecialCharsInFilenames "/n/cartoons"
  removeSpecialCharsInFilenames "/n/funny_videos"
  removeSpecialCharsInFilenames "/n/futbol"
  removeSpecialCharsInFilenames "/n/futbol_4K"
  removeSpecialCharsInFilenames "/n/movies"
  removeSpecialCharsInFilenames "/n/music_videos"
  removeSpecialCharsInFilenames "/n/series"
  removeSpecialCharsInFilenames "/n/tennis"
  removeSpecialCharsInFilenames "/n/Videos-Mobile"
}

removeSpecialCharsInFilenames() {
  local FILES_BASE_PATH=$1
  log.info "Removing special chars from filenames in ${COL_PURPLE}${FILES_BASE_PATH}"
  find ${FILES_BASE_PATH} | sort -r | while read FILE; do
    log.debug "Processing file ${COL_PURPLE}${FILE}"
    local FILE_UPDATED=${FILE}
    for ((i = 0; i < ${#toUnderscore[@]}; i++)); do
      FILE_UPDATED=$(echo "$FILE_UPDATED" | sed -E '$s'"/${toUnderscore[$i]}/_/g")
      log.trace "Used regex: '${toUnderscore[$i]}' toUnderscore output is: ${FILE_UPDATED}"
    done
    for ((i = 0; i < ${#toAt[@]}; i++)); do
      FILE_UPDATED=$(echo "$FILE_UPDATED" | sed -E '$s'"/${toAt[$i]}/@/g")
      log.trace "Used regex: '${toAt[$i]}' toAt output is: ${FILE_UPDATED}"
    done
    for ((i = 0; i < ${#toDash[@]}; i++)); do
      FILE_UPDATED=$(echo "$FILE_UPDATED" | sed -E '$s'"/${toDash[$i]}/-/g")
      log.trace "Used regex: '${toDash[$i]}' toDash output is: ${FILE_UPDATED}"
    done
    for ((i = 0; i < ${#toDot[@]}; i++)); do
      FILE_UPDATED=$(echo "$FILE_UPDATED" | sed -E '$s'"/${toDot[$i]}/\./g")
      log.trace "Used regex: '${toDot[$i]}' toDot output is: ${FILE_UPDATED}"
    done
    for ((i = 0; i < ${#toSlash[@]}; i++)); do
      FILE_UPDATED=$(echo "$FILE_UPDATED" | sed -E '$s'"#${toSlash[$i]}#/#g")
      log.trace "Used regex: '${toSlash[$i]}' toSlash output is: ${FILE_UPDATED}"
    done
    for ((i = 0; i < ${#toRemove[@]}; i++)); do
      FILE_UPDATED=$(echo "$FILE_UPDATED" | sed -E '$s'"#${toRemove[$i]}##g")
      log.trace "Used regex: '${toRemove[$i]}' toRemove output is: ${FILE_UPDATED}"
    done
    if [ "${FILE}" != "${FILE_UPDATED}" ]; then
      local FILE_UPDATED_DIR=$(dirname "${FILE_UPDATED}")
      mkdir -p "${FILE_UPDATED_DIR}"
      log.info "Updating name from ${COL_PURPLE}${FILE}${COL_DEFAULT_LOG} to ${COL_CYAN}${FILE_UPDATED}${COL_DEFAULT_LOG}"
      mv "${FILE}" "${FILE_UPDATED}"
    fi
    if [ -d "${FILE}" ] && [ -z "$(ls -A "${FILE}")" ]; then
      log.debug "Empty directory: ${COL_PURPLE}${FILE}"
      echo "echo \"----------- ${FILE} -----------\"" >> ${EMPTY_DIRS_CHECK_FILE}
      echo "ls \"${FILE}\"" >> ${EMPTY_DIRS_CHECK_FILE}

      echo "echo \"----------- ${FILE} -----------\"" >> ${EMPTY_DIRS_RM_FILE}
      echo "rm -rv \"${FILE}\"" >> ${EMPTY_DIRS_RM_FILE}
    fi
  done
}

exitMessage() {
  log.info "Removing special chars process finished"
  log.info "Check the remaining empty directories with: ${COL_RED}chmod a+x ${EMPTY_DIRS_CHECK_FILE} ; ${EMPTY_DIRS_CHECK_FILE}"
  log.info "Remove them with: ${COL_RED}chmod a+x ${EMPTY_DIRS_RM_FILE} ; ${EMPTY_DIRS_RM_FILE}"
  log.info "Then delete the temp files with: ${COL_RED}rm ${EMPTY_DIRS_RM_FILE} ${EMPTY_DIRS_CHECK_FILE}"
}

main "$@"
