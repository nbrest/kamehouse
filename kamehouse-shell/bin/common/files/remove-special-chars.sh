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

EMPTY_DIRS_PATH=${HOME}/temp/remove-special-chars/empty-dirs

DRY_RUN=false
PROCESS_MEDIA_SERVER_VIDEOS=false
PROCESS_MEDIA_SERVER_AUDIO=false
PROCESS_CUSTOM_PATH=false
CUSTOM_PATH=""

# Regex to remove special chars:
declare -a toAt=("[^a-zA-Z0-9:/\\_\. ]" "\]" "@ " " @" "@ @" "@_" "_@" "@_@" " @ ")
declare -a toNumeralTextStart=("^#") # for playlists
declare -a toNumeralSignStart=("^NUMERALSTART") # for playlists
declare -a toUnderscore=(" +" "_+")
declare -a toDash=("@+")
declare -a toDot=("-+\." "\.-+" "_+\." "\._+" "\.+")
declare -a toSlash=("/-" "-/")
declare -a toBackslash=("\\\-" '-\\') # for playlists
declare -a toRemove=("-$")

mainProcess() {
  if ${DRY_RUN}; then
    log.warn "Running with ${COL_RED}DRY-RUN"
  fi
  initEmptyDirsPath
  removeSpecialChars
  exitMessage
}

initEmptyDirsPath() {
  if ${DRY_RUN}; then
    return
  fi
  mkdir -p ${EMPTY_DIRS_PATH}
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
    moveFile "${FILE}" "${FILE_UPDATED}"
    moveEmptyDirToTempFiles "${FILE}" "${FILE_UPDATED}"
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

moveFile() {
  local FILE=$1
  local FILE_UPDATED=$2
  if [ "${FILE}" == "${FILE_UPDATED}" ]; then
    return
  fi
  if ${DRY_RUN}; then
    log.info "${COL_YELLOW}DRY-RUN:${COL_DEFAULT_LOG} Updating name from ${COL_PURPLE}${FILE}${COL_DEFAULT_LOG} to ${COL_CYAN}${FILE_UPDATED}${COL_DEFAULT_LOG}"
    return
  fi
  if [ ! -d "${FILE}" ]; then
    local FILE_UPDATED_DIR=$(dirname "${FILE_UPDATED}")
    log.trace "Making dir "${FILE_UPDATED_DIR}""
    mkdir -p "${FILE_UPDATED_DIR}"
    log.info "Updating name from ${COL_PURPLE}${FILE}${COL_DEFAULT_LOG} to ${COL_CYAN}${FILE_UPDATED}${COL_DEFAULT_LOG}"
    mv -f "${FILE}" "${FILE_UPDATED}"
  fi
}

moveEmptyDirToTempFiles() {
  local FILE=$1
  local FILE_UPDATED=$2
  if ${DRY_RUN}; then
    return
  fi
  if [ -d "${FILE}" ] && [ -z "$(ls -A "${FILE}")" ]; then
    log.info "Moving empty directory to temp: ${COL_PURPLE}${FILE}"
    mkdir -p "${EMPTY_DIRS_PATH}${FILE}"
    mv -f "${FILE}" "${EMPTY_DIRS_PATH}${FILE}"
  fi
  if [ -d "${FILE_UPDATED}" ] && [ -z "$(ls -A "${FILE_UPDATED}")" ]; then
    log.info "Moving empty directory to temp: ${COL_PURPLE}${FILE_UPDATED}"
    mkdir -p "${EMPTY_DIRS_PATH}${FILE_UPDATED}"
    mv -f "${FILE_UPDATED}" "${EMPTY_DIRS_PATH}${FILE_UPDATED}"
  fi
}

removeSpecialCharsInPlaylists() {
  local PLAYLISTS_BASE_PATH=$1
  if [ ! -d "${PLAYLISTS_BASE_PATH}" ]; then
    log.warn "Directory ${COL_PURPLE}${PLAYLISTS_BASE_PATH}${COL_DEFAULT_LOG} doesn't exist. Skipping..."
    return
  fi

  log.info "Removing special chars from playlists in ${COL_PURPLE}${PLAYLISTS_BASE_PATH}"
  find ${PLAYLISTS_BASE_PATH} | grep ".m3u" | while read FILE; do
    log.info "Processing file ${COL_PURPLE}${FILE}"
    updatePlaylist "${FILE}"
  done
}

updatePlaylist() {
  local FILE=$1
  for ((i = 0; i < ${#toNumeralTextStart[@]}; i++)); do
    sed -E -i "s/${toNumeralTextStart[$i]}/NUMERALSTART/Ig" "${FILE}" 
  done  
  for ((i = 0; i < ${#toUnderscore[@]}; i++)); do
    sed -E -i "s/${toUnderscore[$i]}/_/Ig" "${FILE}" 
  done
  for ((i = 0; i < ${#toAt[@]}; i++)); do
    sed -E -i "s/${toAt[$i]}/@/Ig" "${FILE}" 
  done
  for ((i = 0; i < ${#toNumeralSignStart[@]}; i++)); do
    sed -E -i "s/${toNumeralSignStart[$i]}/#/Ig" "${FILE}" 
  done    
  for ((i = 0; i < ${#toDash[@]}; i++)); do
    sed -E -i "s/${toDash[$i]}/-/Ig" "${FILE}" 
  done
  for ((i = 0; i < ${#toDot[@]}; i++)); do
    sed -E -i "s/${toDot[$i]}/\./Ig" "${FILE}" 
  done
  for ((i = 0; i < ${#toSlash[@]}; i++)); do
    sed -E -i "s#${toSlash[$i]}#/#Ig" "${FILE}" 
  done
  for ((i = 0; i < ${#toBackslash[@]}; i++)); do
    sed -E -i "s#${toBackslash[$i]}#\\\#Ig" "${FILE}" 
  done  
  for ((i = 0; i < ${#toRemove[@]}; i++)); do
    sed -E -i "s#${toRemove[$i]}##Ig" "${FILE}" 
  done
}

exitMessage() {
  if ${DRY_RUN}; then
    return
  fi
  log.info "Removing special chars process finished"
  log.info "Check the remaining empty directories in: ${COL_RED}${EMPTY_DIRS_PATH}"
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
