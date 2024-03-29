#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 99
fi

MEDIA_TYPES_REGEX="\.avi$\|\.flv$\|\.mpg$\|\.mpeg$\|\.mp3$\|\.mp4$\|\.mkv$\|\.m4v$\|\.ogg$\|\.ogm$\|\.webm$"

BASE_PATH=""
PLAYLIST_FULL_PATH=""
PREFIX_PATH="./"
USE_WINDOWS_PATHS=false

mainProcess() {
  log.info "Creating new playlist ${COL_PURPLE}${PLAYLIST_FULL_PATH}"
  printEnv
  createPlaylist
  log.info "Finished creating new playlist ${COL_PURPLE}${PLAYLIST_FULL_PATH}"
}

printEnv() {
  log.info "Using prefix path: ${COL_PURPLE}${PREFIX_PATH}"
  log.info "Using base path: ${COL_PURPLE}${BASE_PATH}"
  log.info "Saving playlist to: ${COL_PURPLE}${PLAYLIST_FULL_PATH}"
  log.debug "USE_WINDOWS_PATHS: ${COL_PURPLE}${USE_WINDOWS_PATHS}"
}

createPlaylist() {
  echo "#EXTM3U" > ${PLAYLIST_FULL_PATH} 
  find ${BASE_PATH} | grep --ignore-case -e ${MEDIA_TYPES_REGEX} | sort | while read FILE; do
    local FILE_RELATIVE_TO_BASE=${FILE#${BASE_PATH}}
    local FILE_FULL_PATH="${PREFIX_PATH}${FILE_RELATIVE_TO_BASE}"
    FILE_FULL_PATH=$(urlencode "${FILE_FULL_PATH}")
    log.trace "FILE_FULL_PATH: ${FILE_FULL_PATH}"
    echo "#EXTINF:0,${FILE_RELATIVE_TO_BASE}-${FILE_RELATIVE_TO_BASE}" >> ${PLAYLIST_FULL_PATH}
    echo "${FILE_FULL_PATH}" >> ${PLAYLIST_FULL_PATH}
  done
  checkCommandStatus "$?"

  if ${USE_WINDOWS_PATHS}; then
    setWindowsPaths
  fi
}

setWindowsPaths() {
  log.debug "Setting windows paths"
  sed -i "s#/#\\\#Ig" ${PLAYLIST_FULL_PATH}
}

parseArguments() {
  while getopts ":b:n:p:w" OPT; do
    case $OPT in
    ("b")
      BASE_PATH=$OPTARG
      ;;
    ("n")
      PLAYLIST_FULL_PATH=$OPTARG
      ;;
    ("p")
      PREFIX_PATH=$OPTARG
      ;;
    ("w")
      USE_WINDOWS_PATHS=true
      ;;
    (\?)
      parseInvalidArgument "$OPTARG"
      ;;
    esac
  done
}

setEnvFromArguments() {
  checkRequiredOption "-b" "${BASE_PATH}"
  checkRequiredOption "-n" "${PLAYLIST_FULL_PATH}"

  local PREFIX_PATH_END=${PREFIX_PATH: -1}
  if [ "${PREFIX_PATH_END}" != "/" ]; then
    PREFIX_PATH=${PREFIX_PATH}"/"
  fi 

  local BASE_PATH_END=${BASE_PATH: -1}
  if [ "${BASE_PATH_END}" != "/" ]; then
    BASE_PATH=${BASE_PATH}"/"
  fi 

  local PLAYLIST_FULL_PATH_END=${PLAYLIST_FULL_PATH: -4}
  if [ "${PLAYLIST_FULL_PATH_END}" != ".m3u" ]; then
    PLAYLIST_FULL_PATH=${PLAYLIST_FULL_PATH}".m3u"
  fi  
}

printHelpOptions() {
  addHelpOption "-b [basePath]" "base path to look for media files to add to the playlist" "r"
  addHelpOption "-n [name]" "full path of the playlist file to create. If only the filename is specified it will be created on the current directory" "r"
  addHelpOption "-p [prefixPath]" "prefix path to use on the playlist entries, followed by the paths after the basePath. If not specified, the file entries will have paths relative to the basePath, so the m3u file needs to be on the root of the basePath for the media player to load the files properly. Ej. -p \"D:/Downloads/videos\""
  addHelpOption "-w" "convert all paths to windows style '\\' instead of the default '/'"
}

printHelpFooter() {
  echo -e ""
  echo -e "${COL_YELLOW}Sample Usage:${COL_PURPLE} ${SCRIPT_NAME} ${COL_BLUE}-b${COL_NORMAL} \"/d/Downloads/videos\" ${COL_BLUE}-n${COL_NORMAL} \"\${HOME}/downloadVideosWindowsPlaylist\" ${COL_BLUE}-p${COL_NORMAL} \"D:/Downloads/videos\" ${COL_BLUE}-w${COL_NORMAL}"
  echo -e ""
}

main "$@"
