#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 1
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
  log.info "Using prefix path : ${COL_PURPLE}${PREFIX_PATH}"
  log.info "Using base path : ${COL_PURPLE}${BASE_PATH}"
  log.info "Saving playlist to : ${COL_PURPLE}${PLAYLIST_FULL_PATH}"
  log.debug "USE_WINDOWS_PATHS : ${COL_PURPLE}${USE_WINDOWS_PATHS}"
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

urlencode() {
  # urlencode <string>
  old_lc_collate=$LC_COLLATE
  LC_COLLATE=C
  local length="${#1}"
  for (( i = 0; i < length; i++ )); do
      local c="${1:$i:1}"
      case $c in
          [a-zA-Z0-9.~_-]) printf '%s' "$c" ;;
          *) printf '%%%02X' "'$c" ;;
      esac
  done
  LC_COLLATE=$old_lc_collate
}

setWindowsPaths() {
  log.debug "Setting windows paths"
  sed -i "s#/#\\\#Ig" ${PLAYLIST_FULL_PATH}
}

parseArguments() {
  while getopts ":ab:hn:p:w" OPT; do
    case $OPT in
    ("b")
      BASE_PATH=$OPTARG
      ;;
    ("h")
      parseHelp
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

  if [ -z "${BASE_PATH}" ]; then
    log.error "Option -b is required"
    parseHelp
  fi

  if [ -z "${PLAYLIST_FULL_PATH}" ]; then
    log.error "Option -n is required"
    parseHelp
  fi

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

printHelp() {
  echo -e ""
  echo -e "Usage: ${COL_PURPLE}${SCRIPT_NAME}${COL_NORMAL} [options]"
  echo -e ""
  echo -e "  Options:"
  echo -e "     ${COL_BLUE}-b [basePath]${COL_NORMAL} base path to look for media files to add to the playlist[${COL_RED}required${COL_NORMAL}]"
  echo -e "     ${COL_BLUE}-h${COL_NORMAL} display help"
  echo -e "     ${COL_BLUE}-n [name]${COL_NORMAL} full path of the playlist file to create. If only the filename is specified it will be created on the current directory [${COL_RED}required${COL_NORMAL}]"
  echo -e "     ${COL_BLUE}-p [prefixPath]${COL_NORMAL} prefix path to use on the playlist entries, followed by the paths after the basePath. If not specified, the file entries will have paths relative to the basePath, so the m3u file needs to be on the root of the basePath for the media player to load the files properly. Ej. -p \"D:/Downloads/videos\""
  echo -e "     ${COL_BLUE}-w${COL_NORMAL} convert all paths to windows style '\\' instead of the default '/'"
  echo -e ""
  echo -e "Sample Usage: ${SCRIPT_NAME} -b \"/d/Downloads/videos\" -n \"\${HOME}/downloadVideosWindowsPlaylist\" -p \"D:/Downloads/videos\" -w"
  echo -e ""
}

main "$@"
