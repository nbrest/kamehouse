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
MP3_ROOT_DIR=/d/niko9enzo/mp3
MEDIA_TYPES_REGEX="\.mp3$"
M3U_WINDOWS_ROOT="${HOME}/git/kamehouse-audio-playlists/playlists/windows"
ALL_MUSIC_M3U_WINDOWS="${M3U_WINDOWS_ROOT}/All.m3u"
MP3_SUBDIR_SEPARATOR="/.*\|.*/"

mainProcess() {
  clearDirectories
  printWarning
  runItunesExport
  removeExtraPlaylists  
  createAllPlaylists
  splitTruecryptPlaylists
  movePlaylistsToSubdirectories
  printOutput
  validateOutput
}

clearDirectories() {
  cd ${PROJECT_DIR}
  git rm -rf ${PROJECT_DIR}/windows
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
  rm -f ${PROJECT_DIR}/windows/Cartoons.m3u
  rm -f ${PROJECT_DIR}/windows/Guns-N-Roses.m3u
  rm -f ${PROJECT_DIR}/windows/Metal.m3u
  rm -f ${PROJECT_DIR}/windows/Movies.m3u
  rm -f ${PROJECT_DIR}/windows/Others.m3u
  rm -f ${PROJECT_DIR}/windows/Punk.m3u
}

createAllPlaylists() {
  local METAL="AC-DC${MP3_SUBDIR_SEPARATOR}\
Angra${MP3_SUBDIR_SEPARATOR}\
Dream Theater${MP3_SUBDIR_SEPARATOR}\
Gamma Ray${MP3_SUBDIR_SEPARATOR}\
Heavens Gate${MP3_SUBDIR_SEPARATOR}\
Helloween${MP3_SUBDIR_SEPARATOR}\
Iron Maiden${MP3_SUBDIR_SEPARATOR}\
Judas Priest${MP3_SUBDIR_SEPARATOR}\
Metallica${MP3_SUBDIR_SEPARATOR}\
Ozzy${MP3_SUBDIR_SEPARATOR}\
Raphsody${MP3_SUBDIR_SEPARATOR}\
Satriani"

  local POP="Pop${MP3_SUBDIR_SEPARATOR}\
Michael Jackson${MP3_SUBDIR_SEPARATOR}\
Madonna${MP3_SUBDIR_SEPARATOR}\
Bruno Mars"

  local PUNK="Bad Religion${MP3_SUBDIR_SEPARATOR}\
Blender${MP3_SUBDIR_SEPARATOR}\
Blink-182${MP3_SUBDIR_SEPARATOR}\
Eminem${MP3_SUBDIR_SEPARATOR}\
Foo Fighters${MP3_SUBDIR_SEPARATOR}\
Green Day${MP3_SUBDIR_SEPARATOR}\
Limp Bizkit${MP3_SUBDIR_SEPARATOR}\
Linkin Park${MP3_SUBDIR_SEPARATOR}\
Nirvana${MP3_SUBDIR_SEPARATOR}\
Offspring${MP3_SUBDIR_SEPARATOR}\
Ramones"

  local ROCK_NACIONAL="Rock Nacional${MP3_SUBDIR_SEPARATOR}\
Attaque 77${MP3_SUBDIR_SEPARATOR}\
Autenticos Decadentes${MP3_SUBDIR_SEPARATOR}\
Blender${MP3_SUBDIR_SEPARATOR}\
Cadena Perpetua${MP3_SUBDIR_SEPARATOR}\
Callejeros${MP3_SUBDIR_SEPARATOR}\
Fito Paez"

  createAllPlaylist "All-Music" ".*"
  createAllPlaylist "Aerosmith" "Aerosmith"
  createAllPlaylist "Anime" "Anime"
  createAllPlaylist "Bob-And-Tom" "Bob And Tom"
  createAllPlaylist "Bajoneros" "Bajoneros"
  createAllPlaylist "Beatles" "The Beatles"
  createAllPlaylist "Cartoons" "Cartoons"
  createAllPlaylist "Cirque-Du-Soleil" "Cirque du Soleil"
  createAllPlaylist "Elton-John" "Elton John"
  createAllPlaylist "Enya" "Enya"
  createAllPlaylist "Futbol" "Futbol"
  createAllPlaylist "Guns-N-Roses" "Guns N Roses"
  createAllPlaylist "Hits" "Otros/Hits"
  createAllPlaylist "Jack-Johnson" "Jack Johnson"
  createAllPlaylist "Madonna" "Madonna"
  createAllPlaylist "Metal" "${METAL}"
  createAllPlaylist "Michael-Jackson" "Michael Jackson"
  createAllPlaylist "Movies" "Movies"
  createAllPlaylist "Oasis" "Oasis"
  createAllPlaylist "Others" "Otros"
  createAllPlaylist "Phill-Collins" "Phill Collins"
  createAllPlaylist "Pop" "${POP}"
  createAllPlaylist "Punk" "${PUNK}"
  createAllPlaylist "Queen" "Queen"
  createAllPlaylist "Red-Hot-Chili-Peppers" "Red Hot Chili Peppers"
  createAllPlaylist "Rock-Nacional" "${ROCK_NACIONAL}"
  createAllPlaylist "Rolling-Stones" "Rolling Stones"
  createAllPlaylist "SKA-P" "SKA-P"
  createAllPlaylist "Trance" "Trance"
  createAllPlaylist "U2" "U2"
}

createAllPlaylist() {
  local PLAYLIST_NAME=$1
  local MP3_SUBDIRS=$2

  local PLAYLIST_FILENAME="${PLAYLIST_NAME}.All.m3u"
  local PLAYLIST_FILE="${M3U_WINDOWS_ROOT}/${PLAYLIST_FILENAME}"
  log.info "Creating ${COL_PURPLE}${PLAYLIST_FILENAME}${COL_DEFAULT_LOG} playlist"
  echo "" > ${PLAYLIST_FILE}
  find ${MP3_ROOT_DIR} -regex ".*/${MP3_SUBDIRS}/.*" | grep --ignore-case -e ${MEDIA_TYPES_REGEX} | sort | while read FILE; do
      local FILE_WITHOUT_ROOT_PREFIX=${FILE#$ROOT_PREFIX}
      echo "${FILE_WITHOUT_ROOT_PREFIX}" >> ${PLAYLIST_FILE}
  done
  sed -i "s#/d/niko9enzo#D:/niko9enzo#Ig" "${PLAYLIST_FILE}"
  sed -i "s#/\\./#/#Ig" "${PLAYLIST_FILE}"
  sed -i "s#/#\\\#Ig" "${PLAYLIST_FILE}"
}

splitTruecryptPlaylists() {
  log.info "Splitting tc container mp3 to separate playlists"
  local TEMP_DIR=${PATH_PLS_SOURCE}/temp
  mkdir ${TEMP_DIR}
  local PATH_PLS_SOURCE=${PROJECT_DIR}/windows
  find ${PATH_PLS_SOURCE} -maxdepth 1 | grep --ignore-case -e "\.m3u$" | while read FILE; do
    local PLAYLIST_RELATIVE_FILENAME=${FILE#${PATH_PLS_SOURCE}}
    local PLAYLIST_FILE_NAME="$(basename "${PLAYLIST_RELATIVE_FILENAME}")"
    log.info "Splitting playlist ${COL_PURPLE}${PLAYLIST_FILE_NAME}"
    
    # Create tc playlist
    cat "${PATH_PLS_SOURCE}/${PLAYLIST_FILE_NAME}" | grep "Z:\\\\mp3" > /dev/null
    if [ "$?" == "0" ]; then
      log.debug "Creating TC playlist for ${COL_PURPLE}${PLAYLIST_FILE_NAME}"
      cat "${PATH_PLS_SOURCE}/${PLAYLIST_FILE_NAME}" | grep "Z:\\\\mp3" > "${TEMP_DIR}/Z-TC-${PLAYLIST_FILE_NAME}"
    fi  

    # Create normal playlist
    cat "${PATH_PLS_SOURCE}/${PLAYLIST_FILE_NAME}" | grep -v "Z:\\\\mp3" > /dev/null
    if [ "$?" == "0" ]; then
      log.debug "Creating normal playlist for ${COL_PURPLE}${PLAYLIST_FILE_NAME}"
      cat "${PATH_PLS_SOURCE}/${PLAYLIST_FILE_NAME}" | grep -v "Z:\\\\mp3" > "${TEMP_DIR}/${PLAYLIST_FILE_NAME}"
    fi  
    
  done
  rm -f ${PATH_PLS_SOURCE}/*.m3u
  mv ${TEMP_DIR}/*.m3u ${PATH_PLS_SOURCE}
  rm -r ${TEMP_DIR}
}

movePlaylistsToSubdirectories() {
  local PATH_PLS_SOURCE=${PROJECT_DIR}/windows
  find ${PATH_PLS_SOURCE} -maxdepth 1 | grep --ignore-case -e "\.m3u$" | while read FILE; do
    local PLAYLIST_RELATIVE_FILENAME=${FILE#${PATH_PLS_SOURCE}}
    local PLAYLIST_SUBDIR=${PLAYLIST_RELATIVE_FILENAME::-4}
    local PLAYLIST_FILE_NAME="$(basename "${PLAYLIST_RELATIVE_FILENAME}")"
    log.info "Moving playlist ${COL_PURPLE}${PLAYLIST_FILE_NAME}${COL_DEFAULT_LOG} to subfolder"
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
