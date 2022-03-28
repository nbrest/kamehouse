# Common video-playlists variables
LOG_PROCESS_TO_FILE=true
PROJECT_DIR="${HOME}/git/kamehouse-video-playlists/playlists"

MEDIA_SERVER="niko-server"
MEDIA_SERVER_IP="192.168.0.109"

PATH_BASE_SOURCE=""
PATH_BASE_DEST=""

PATH_PLS_SOURCE=""
PATH_PLS_DEST=""

# Common video-playlists functions
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
  rm -r ${PATH_PLS_DEST}/*
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
  sed -i "s#${PATH_BASE_SOURCE}#${PATH_BASE_DEST}#Ig" ${FILE}
  checkCommandStatus "$?"  
}
