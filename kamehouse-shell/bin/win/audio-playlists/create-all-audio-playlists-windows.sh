#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 1
fi
source ${HOME}/programs/kamehouse-shell/bin/common/audio-playlists/audio-playlists-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing audio-playlists-functions.sh\033[0;39m"
  exit 1
fi

ITUNES_EXPORT_JAR="/d/niko9enzo/programs/Itunes/iTunesExportScala/itunesexport.jar"

mainProcess() {
  clearDirectories
  printWarning
  runItunesExport
  removeExtraPlaylists
  movePlaylistsToSubdirectories
  printOutput
  validateOutput
}

clearDirectories() {
  rm -rf ${PROJECT_DIR}/windows
  mkdir -p ${PROJECT_DIR}/windows  
}

printWarning() {
  log.warn "********************************************************************************"
  log.warn "RE GENERATE THE ITUNES LIBRARY XML FROM ITUNES BEFORE RUNNING THIS SCRIPT"
  log.warn "********************************************************************************"
  log.warn "For itues 12+ I can configure itunes to update the xml automatically: "
  log.warn "Edit > Preferences > Advanced "
  log.warn "   check 'Share iTunes Library XML with other applications'"
  log.warn "To generate it manually from itunes: File > Library > Export Library"
  log.warn "The resulting file should be in: \${HOME}\Music\iTunes\iTunes Music Library.xml"
  log.warn "For the itunesexport program to pick up the library"
  log.warn "********************************************************************************"
}

runItunesExport() {
  log.debug "java -jar ${ITUNES_EXPORT_JAR} -outputDir=${PROJECT_DIR}/windows/ -separator=WIN"
  java -jar ${ITUNES_EXPORT_JAR} -outputDir=${PROJECT_DIR}/windows/ -separator=WIN
}

removeExtraPlaylists() {
  rm -f ${PROJECT_DIR}/windows/0-Run.m3u
  rm -f ${PROJECT_DIR}/windows/Anime.m3u
  rm -f ${PROJECT_DIR}/windows/Beatles.m3u
  rm -f ${PROJECT_DIR}/windows/By-Decade.m3u
  rm -f ${PROJECT_DIR}/windows/Guns-N-Roses.m3u
  rm -f ${PROJECT_DIR}/windows/Metal.m3u
  rm -f ${PROJECT_DIR}/windows/Movies.m3u
  rm -f ${PROJECT_DIR}/windows/Others.m3u
  rm -f ${PROJECT_DIR}/windows/Punk.m3u
  rm -f ${PROJECT_DIR}/windows/Rock-Nacional.m3u
  rm -f ${PROJECT_DIR}/windows/Trance.m3u
}

movePlaylistsToSubdirectories() {
  local PATH_PLS_SOURCE=${PROJECT_DIR}/windows
  find ${PATH_PLS_SOURCE} -maxdepth 1 | grep --ignore-case -e "\.m3u$" | while read FILE; do
    local PLAYLIST_RELATIVE_FILENAME=${FILE#${PATH_PLS_SOURCE}}
    local PLAYLIST_SUBDIR=${PLAYLIST_RELATIVE_FILENAME::-4}
    local PLAYLIST_FILE_NAME="$(basename "${PLAYLIST_RELATIVE_FILENAME}")"
    log.info "Moving playlist ${COL_PURPLE}${PLAYLIST_FILE_NAME}"
    mkdir -p "${PATH_PLS_SOURCE}${PLAYLIST_SUBDIR}"
    mv -f "${FILE}" "${PATH_PLS_SOURCE}${PLAYLIST_SUBDIR}/${PLAYLIST_FILE_NAME}"
  done
}

printOutput() {
  log.info "Windows playlists"
  ls -l ${PROJECT_DIR}/windows  
}

validateOutput() {
  NUMBER_OF_M3U_CREATED=`find ${PROJECT_DIR}/windows | grep -e ".m3u" | wc -l`
  if [ "${NUMBER_OF_M3U_CREATED}" == "0" ]; then
    log.error "Error creating windows playlists. Run itunes export command manually"
    exit 1
  else
    log.info "Windows playlists re created successfully"
  fi
}
main "$@"
