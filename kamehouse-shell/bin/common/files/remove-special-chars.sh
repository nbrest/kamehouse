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

EMPTY_DIRS_RM_FILE=${HOME}/temp/remove-special-chars/rm-empty-dirs.sh
EMPTY_DIRS_CHECK_FILE=${HOME}/temp/remove-special-chars/check-empty-dirs.sh

DRY_RUN=false
PROCESS_MEDIA_SERVER_VIDEOS=false
PROCESS_MEDIA_SERVER_AUDIO=false
PROCESS_CUSTOM_PATH=false
CUSTOM_PATH=""

# Regex to remove special chars:
declare -a toAt=("[^a-zA-Z0-9:/\\_\. ]" "\]" "@ " " @" "@ @" "@_" "_@" "@_@" " @ ")
declare -a toUnderscore=(" +" "_+")
declare -a toDash=("@+")
declare -a toDot=("-+\." "\.-+" "_+\." "\._+" "\.+")
declare -a toSlash=("/-" "-/")
declare -a toRemove=("-$")

mainProcess() {
  if ${DRY_RUN}; then
    log.warn "Running with ${COL_RED}DRY-RUN"
  fi
  initEmptyDirsFiles
  removeSpecialChars
  exitMessage
}

initEmptyDirsFiles() {
  if ${DRY_RUN}; then
    return
  fi
  mkdir -p ${HOME}/temp
  echo "#!/bin/bash" > ${EMPTY_DIRS_RM_FILE}
  echo "" >> ${EMPTY_DIRS_RM_FILE}

  echo "#!/bin/bash" > ${EMPTY_DIRS_CHECK_FILE}
  echo "" >> ${EMPTY_DIRS_CHECK_FILE}  
}

removeSpecialChars() {
  if ${PROCESS_MEDIA_SERVER_VIDEOS}; then
    removeSpecialCharsForMediaServerVideos
    exitSuccessfully
  fi

  if ${PROCESS_MEDIA_SERVER_AUDIO}; then
    removeSpecialCharsForMediaServerAudio
    exitSuccessfully
  fi

  if ${PROCESS_CUSTOM_PATH}; then
    removeSpecialCharsInCustomPath
    exitSuccessfully
  fi  

  removeSpecialCharsInDefaultPath
}

removeSpecialCharsForMediaServerVideos() {
  removeSpecialCharsInPath "/n/anime"
  removeSpecialCharsInPath "/n/cartoons"
  removeSpecialCharsInPath "/n/funny_videos"
  removeSpecialCharsInPath "/n/futbol"
  removeSpecialCharsInPath "/n/futbol_4K"
  removeSpecialCharsInPath "/n/movies"
  removeSpecialCharsInPath "/n/music_videos"
  removeSpecialCharsInPath "/n/series"
  removeSpecialCharsInPath "/n/tennis"
  removeSpecialCharsInPath "/n/Videos-Mobile"
}

removeSpecialCharsForMediaServerAudio() {
  removeSpecialCharsInPath "/d/niko9enzo/mp3"
  removeSpecialCharsInPlaylists "${HOME}/git/kamehouse-audio-playlists/playlists"
}

removeSpecialCharsInCustomPath() {
  removeSpecialCharsInPath "${CUSTOM_PATH}"
  removeSpecialCharsInPlaylists "${CUSTOM_PATH}"
}

removeSpecialCharsInDefaultPath() {
  removeSpecialCharsInPath "/n/remove-special-chars/video"
  removeSpecialCharsInPath "/n/remove-special-chars/audio"
  removeSpecialCharsInPlaylists "/n/remove-special-chars/playlists"

  removeSpecialCharsInPath "${HOME}/temp/remove-special-chars/video"
  removeSpecialCharsInPath "${HOME}/temp/remove-special-chars/audio"
  removeSpecialCharsInPlaylists "${HOME}/temp/remove-special-chars/playlists"  
}

removeSpecialCharsInPath() {
  local FILES_BASE_PATH=$1
  if [ ! -d "${FILES_BASE_PATH}" ]; then
    log.warn "Directory ${COL_PURPLE}${FILES_BASE_PATH}${COL_DEFAULT_LOG} doesn't exist. Skipping..."
    return
  fi
  log.info "Removing special chars from filenames in ${COL_PURPLE}${FILES_BASE_PATH}"
  find ${FILES_BASE_PATH} | sort -r | while read FILE; do
    log.debug "Processing file ${COL_PURPLE}${FILE}"
    local FILE_UPDATED=`getUpdatedFileName "${FILE}"`
    updateFileName "${FILE}" "${FILE_UPDATED}"
    addEmptyDirToTempFiles "${FILE}"
  done
}

getUpdatedFileName() {
  local FILE_UPDATED=$1
  for ((i = 0; i < ${#toUnderscore[@]}; i++)); do
    FILE_UPDATED=$(echo "$FILE_UPDATED" | sed -E '$s'"/${toUnderscore[$i]}/_/g")
  done
  for ((i = 0; i < ${#toAt[@]}; i++)); do
    FILE_UPDATED=$(echo "$FILE_UPDATED" | sed -E '$s'"/${toAt[$i]}/@/g")
  done
  for ((i = 0; i < ${#toDash[@]}; i++)); do
    FILE_UPDATED=$(echo "$FILE_UPDATED" | sed -E '$s'"/${toDash[$i]}/-/g")
  done
  for ((i = 0; i < ${#toDot[@]}; i++)); do
    FILE_UPDATED=$(echo "$FILE_UPDATED" | sed -E '$s'"/${toDot[$i]}/\./g")
  done
  for ((i = 0; i < ${#toSlash[@]}; i++)); do
    FILE_UPDATED=$(echo "$FILE_UPDATED" | sed -E '$s'"#${toSlash[$i]}#/#g")
  done
  for ((i = 0; i < ${#toRemove[@]}; i++)); do
    FILE_UPDATED=$(echo "$FILE_UPDATED" | sed -E '$s'"#${toRemove[$i]}##g")
  done
  echo "${FILE_UPDATED}"
}

updateFileName() {
  local FILE=$1
  local FILE_UPDATED=$2
  if [ "${FILE}" != "${FILE_UPDATED}" ]; then
    if ${DRY_RUN}; then
      log.info "${COL_YELLOW}DRY-RUN:${COL_DEFAULT_LOG} Updating name from ${COL_PURPLE}${FILE}${COL_DEFAULT_LOG} to ${COL_CYAN}${FILE_UPDATED}${COL_DEFAULT_LOG}"
    else
      local FILE_UPDATED_DIR=$(dirname "${FILE_UPDATED}")
      mkdir -p "${FILE_UPDATED_DIR}"
      log.info "Updating name from ${COL_PURPLE}${FILE}${COL_DEFAULT_LOG} to ${COL_CYAN}${FILE_UPDATED}${COL_DEFAULT_LOG}"
      mv "${FILE}" "${FILE_UPDATED}"
    fi
  fi
}

addEmptyDirToTempFiles() {
  local FILE=$1
  if ! ${DRY_RUN}; then
    if [ -d "${FILE}" ] && [ -z "$(ls -A "${FILE}")" ]; then
      log.debug "Empty directory: ${COL_PURPLE}${FILE}"
      echo "echo \"----------- ${FILE} -----------\"" >> ${EMPTY_DIRS_CHECK_FILE}
      echo "ls \"${FILE}\"" >> ${EMPTY_DIRS_CHECK_FILE}

      echo "echo \"----------- ${FILE} -----------\"" >> ${EMPTY_DIRS_RM_FILE}
      echo "rm -rv \"${FILE}\"" >> ${EMPTY_DIRS_RM_FILE}
    fi
  fi
}

removeSpecialCharsInPlaylists() {
  local PLAYLISTS_BASE_PATH=$1
  if [ ! -d "${PLAYLISTS_BASE_PATH}" ]; then
    log.warn "Directory ${COL_PURPLE}${PLAYLISTS_BASE_PATH}${COL_DEFAULT_LOG} doesn't exist. Skipping..."
    return
  fi

  log.info "Removing special chars from playlists in ${COL_PURPLE}${PLAYLISTS_BASE_PATH}"
}

exitMessage() {
  if ${DRY_RUN}; then
    return
  fi
  log.info "Removing special chars process finished"
  log.info "Check the remaining empty directories with: ${COL_RED}chmod a+x ${EMPTY_DIRS_CHECK_FILE} ; ${EMPTY_DIRS_CHECK_FILE}"
  log.info "Remove them with: ${COL_RED}chmod a+x ${EMPTY_DIRS_RM_FILE} ; ${EMPTY_DIRS_RM_FILE}"
  log.info "Then delete the temp files with: ${COL_RED}rm ${EMPTY_DIRS_RM_FILE} ${EMPTY_DIRS_CHECK_FILE}"
}

parseArguments() {
  while getopts ":ac:dv" OPT; do
    case $OPT in
    ("a")
      PROCESS_MEDIA_SERVER_AUDIO=true
      ;;
    ("c")
      CUSTOM_PATH="$OPTARG"
      PROCESS_CUSTOM_PATH=true
      ;;
    ("d")
      DRY_RUN=true
      ;;
    ("v")
      PROCESS_MEDIA_SERVER_VIDEOS=true
      ;;
    (\?)
      parseInvalidArgument "$OPTARG"
      ;;
    esac
  done 
}

printHelpOptions() {
  addHelpOption "-a" "process media server audio files"
  addHelpOption "-c [path]" "custom path to remove special chars from"
  addHelpOption "-d" "dry run. don't update any files"
  addHelpOption "-v" "process media server video files"
}

main "$@"
