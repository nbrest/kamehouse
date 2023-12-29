#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 9
fi
source ${HOME}/programs/kamehouse-shell/bin/common/video-playlists/video-playlists-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing video-playlists-functions.sh\033[0;39m"
  exit 9
fi

MEDIA_TYPES_REGEX="\.avi$\|\.flv$\|\.mpg$\|\.mpeg$\|\.mp4$\|\.mkv$\|\.m4v$\|\.ogg$\|\.ogm$\|\.webm$"
PATH_VLC_PLS_ROOT=${PROJECT_DIR}/windows-bash/media-drive
PATH_VIDEO_FILES_ROOT=${ROOT_PREFIX}/n

mainProcess() {
  createPlaylists
}

createPlaylists() {
  createAnimePlaylists
  createCartoonPlaylists
  createFutbolPlaylists
  createFunnyVideosPlaylists
  createMoviesPlaylists
  createMusicVideoPlaylists
  createSeriesPlaylists
  createTennisPlaylists

  log.info "Waiting for all background processes to finish"
  wait
}

createAnimePlaylists() {
  local PATH_ANIME_PLS=${PATH_VLC_PLS_ROOT}/anime
  
  local FILE_ANIME_ALL_PLS=${PATH_ANIME_PLS}/anime_all.m3u
  local FILE_CAPTAIN_TSUBASA_MOVIES_PLS=${PATH_ANIME_PLS}/captain_tsubasa_movies.m3u
  local FILE_DETECTIVE_CONAN_MOVIES_PLS=${PATH_ANIME_PLS}/detective_conan_movies.m3u
  local FILE_DRAGONBALL_MOVIES_PLS=${PATH_ANIME_PLS}/dragonball_movies.m3u
  local FILE_DRAGONBALL_Z_MOVIES_PLS=${PATH_ANIME_PLS}/dragonball_z_movies.m3u
  local FILE_POKEMON_MOVIES_PLS=${PATH_ANIME_PLS}/pokemon_movies.m3u
  local FILE_PRINCE_OF_TENNIS_MOVIES_PLS=${PATH_ANIME_PLS}/prince_of_tennis_movies.m3u
  local FILE_RANMA_MOVIES_PLS=${PATH_ANIME_PLS}/ranma_movies.m3u
  local FILE_SAINT_SEIYA_MOVIES_PLS=${PATH_ANIME_PLS}/saint_seiya_movies.m3u

  local PATH_ANIME_VIDEO_FILES=${PATH_VIDEO_FILES_ROOT}/anime
  local PATH_CAPTAIN_TSUBASA_MOVIES_FILES=${PATH_ANIME_VIDEO_FILES}/captain_tsubasa/movies
  local PATH_DETECTIVE_CONAN_MOVIES_FILES=${PATH_ANIME_VIDEO_FILES}/detective_conan/movies
  local PATH_DRAGONBALL_MOVIES_FILES=${PATH_ANIME_VIDEO_FILES}/dragonball/02_dragonball/movies
  local PATH_DRAGONBALL_Z_MOVIES_FILES=${PATH_ANIME_VIDEO_FILES}/dragonball/01_dragonball_z/movies
  local PATH_POKEMON_MOVIES_VIDEO_FILES=${PATH_ANIME_VIDEO_FILES}/pokemon/movies
  local PATH_PRINCE_OF_TENNIS_MOVIES_FILES=${PATH_ANIME_VIDEO_FILES}/prince_of_tennis/movies
  local PATH_RANMA_MOVIES_FILES=${PATH_ANIME_VIDEO_FILES}/ranma/movies
  local PATH_SAINT_SEIYA_MOVIES_FILES=${PATH_ANIME_VIDEO_FILES}/saint_seiya/movies

  local ANIME_LIST=`ls -1 ${PATH_ANIME_VIDEO_FILES}`
  echo -e "${ANIME_LIST}" | while read ANIME; do
    createPlaylist ${PATH_ANIME_VIDEO_FILES}/${ANIME} ${PATH_ANIME_PLS}/${ANIME}_all.m3u ${PATH_VIDEO_FILES_ROOT} &
  done

  createPlaylist ${PATH_ANIME_VIDEO_FILES} ${FILE_ANIME_ALL_PLS} ${PATH_VIDEO_FILES_ROOT} &

  createPlaylist ${PATH_CAPTAIN_TSUBASA_MOVIES_FILES} ${FILE_CAPTAIN_TSUBASA_MOVIES_PLS} ${PATH_VIDEO_FILES_ROOT} &
  createPlaylist ${PATH_DETECTIVE_CONAN_MOVIES_FILES} ${FILE_DETECTIVE_CONAN_MOVIES_PLS} ${PATH_VIDEO_FILES_ROOT} &
  createPlaylist ${PATH_DRAGONBALL_MOVIES_FILES} ${FILE_DRAGONBALL_MOVIES_PLS} ${PATH_VIDEO_FILES_ROOT} &
  createPlaylist ${PATH_DRAGONBALL_Z_MOVIES_FILES} ${FILE_DRAGONBALL_Z_MOVIES_PLS} ${PATH_VIDEO_FILES_ROOT} &
  createPlaylist ${PATH_POKEMON_MOVIES_VIDEO_FILES} ${FILE_POKEMON_MOVIES_PLS} ${PATH_VIDEO_FILES_ROOT} &
  createPlaylist ${PATH_PRINCE_OF_TENNIS_MOVIES_FILES} ${FILE_PRINCE_OF_TENNIS_MOVIES_PLS} ${PATH_VIDEO_FILES_ROOT} &
  createPlaylist ${PATH_RANMA_MOVIES_FILES} ${FILE_RANMA_MOVIES_PLS} ${PATH_VIDEO_FILES_ROOT} &  
  createPlaylist ${PATH_SAINT_SEIYA_MOVIES_FILES} ${FILE_SAINT_SEIYA_MOVIES_PLS} ${PATH_VIDEO_FILES_ROOT} &
}

createCartoonPlaylists() {
  local PATH_CARTOON_PLS=${PATH_VLC_PLS_ROOT}/cartoons

  local FILE_CARTOONS_ALL_PLS=${PATH_CARTOON_PLS}/cartoons_all.m3u
  local FILE_BATMAN_PLS=${PATH_CARTOON_PLS}/batman_all.m3u
  local FILE_BATMAN_MOVIES_PLS=${PATH_CARTOON_PLS}/batman_movies.m3u
  local FILE_JUSTICE_LEAGUE_PLS=${PATH_CARTOON_PLS}/justice_league_all.m3u
  local FILE_JUSTICE_LEAGUE_MOVIES_PLS=${PATH_CARTOON_PLS}/justice_league_movies.m3u
  local FILE_SUPERMAN_PLS=${PATH_CARTOON_PLS}/superman_all.m3u
  local FILE_SUPERMAN_MOVIES_PLS=${PATH_CARTOON_PLS}/superman_movies.m3u
  local FILE_SPIDERMAN_PLS=${PATH_CARTOON_PLS}/spiderman_all.m3u
  local FILE_XMEN_PLS=${PATH_CARTOON_PLS}/x_men_all.m3u

  local PATH_CARTOON_VIDEO_FILES=${PATH_VIDEO_FILES_ROOT}/cartoons
  local PATH_BATMAN_VIDEO_FILES=${PATH_CARTOON_VIDEO_FILES}/dc_comics/batman
  local PATH_BATMAN_MOVIES_FILES=${PATH_BATMAN_VIDEO_FILES}/movies
  local PATH_JUSTICE_LEAGUE_VIDEO_FILES=${PATH_CARTOON_VIDEO_FILES}/dc_comics/justice_league
  local PATH_JUSTICE_LEAGUE_MOVIES_FILES=${PATH_JUSTICE_LEAGUE_VIDEO_FILES}/movies
  local PATH_SUPERMAN_VIDEO_FILES=${PATH_CARTOON_VIDEO_FILES}/dc_comics/superman
  local PATH_SUPERMAN_MOVIES_FILES=${PATH_SUPERMAN_VIDEO_FILES}/movies
  local PATH_SPIDERMAN_VIDEO_FILES=${PATH_CARTOON_VIDEO_FILES}/marvel/spiderman
  local PATH_XMEN_VIDEO_FILES=${PATH_CARTOON_VIDEO_FILES}/marvel/x_men

  local CARTOONS=`ls -1 ${PATH_CARTOON_VIDEO_FILES}`
  echo -e "${CARTOONS}" | while read CARTOON; do
    createPlaylist ${PATH_CARTOON_VIDEO_FILES}/${CARTOON} ${PATH_CARTOON_PLS}/${CARTOON}_all.m3u ${PATH_VIDEO_FILES_ROOT} &
  done

  createPlaylist ${PATH_CARTOON_VIDEO_FILES}/${CARTOON} ${FILE_CARTOONS_ALL_PLS} ${PATH_VIDEO_FILES_ROOT} &

  # DC Comics
  createPlaylist ${PATH_BATMAN_VIDEO_FILES} ${FILE_BATMAN_PLS} ${PATH_VIDEO_FILES_ROOT} &
  createPlaylist ${PATH_BATMAN_MOVIES_FILES} ${FILE_BATMAN_MOVIES_PLS} ${PATH_VIDEO_FILES_ROOT} &
  createPlaylist ${PATH_JUSTICE_LEAGUE_VIDEO_FILES} ${FILE_JUSTICE_LEAGUE_PLS} ${PATH_VIDEO_FILES_ROOT} &
  createPlaylist ${PATH_JUSTICE_LEAGUE_MOVIES_FILES} ${FILE_JUSTICE_LEAGUE_MOVIES_PLS} ${PATH_VIDEO_FILES_ROOT} &
  createPlaylist ${PATH_SUPERMAN_VIDEO_FILES} ${FILE_SUPERMAN_PLS} ${PATH_VIDEO_FILES_ROOT} &
  createPlaylist ${PATH_SUPERMAN_MOVIES_FILES} ${FILE_SUPERMAN_MOVIES_PLS} ${PATH_VIDEO_FILES_ROOT} &
  # Marvel
  createPlaylist ${PATH_SPIDERMAN_VIDEO_FILES} ${FILE_SPIDERMAN_PLS} ${PATH_VIDEO_FILES_ROOT} &
  createPlaylist ${PATH_XMEN_VIDEO_FILES} ${FILE_XMEN_PLS} ${PATH_VIDEO_FILES_ROOT} &
}

createFutbolPlaylists() {
  local PATH_FUTBOL_PLS=${PATH_VLC_PLS_ROOT}/futbol
  local FILE_FUTBOL_ALL_PLS=${PATH_FUTBOL_PLS}/futbol_all.m3u
  local PATH_FUTBOL_VIDEO_FILES=${PATH_VIDEO_FILES_ROOT}/futbol
  createPlaylist ${PATH_FUTBOL_VIDEO_FILES} ${FILE_FUTBOL_ALL_PLS} ${PATH_VIDEO_FILES_ROOT} &
}

createFunnyVideosPlaylists() {
  local PATH_FUNNY_VIDEOS_PLS=${PATH_VLC_PLS_ROOT}/funny_videos
  local FILE_FUNNY_VIDEOS_ALL_PLS=${PATH_FUNNY_VIDEOS_PLS}/funny_videos_all.m3u
  local PATH_FUNNY_VIDEOS_VIDEO_FILES=${PATH_VIDEO_FILES_ROOT}/funny_videos
  createPlaylist ${PATH_FUNNY_VIDEOS_VIDEO_FILES} ${FILE_FUNNY_VIDEOS_ALL_PLS} ${PATH_VIDEO_FILES_ROOT} &
}

createMoviesPlaylists() {
  local PATH_MOVIES_PLS=${PATH_VLC_PLS_ROOT}/movies

  local FILE_MOVIES_ALL_PLS=${PATH_MOVIES_PLS}/movies_all.m3u
  local FILE_MOVIES_DC_PLS=${PATH_MOVIES_PLS}/movies_dc.m3u
  local FILE_MOVIES_MARVEL_PLS=${PATH_MOVIES_PLS}/movies_marvel.m3u
  local FILE_MOVIES_DISNEY_PLS=${PATH_MOVIES_PLS}/movies_disney.m3u
  local FILE_MOVIES_HARRY_POTTER_PLS=${PATH_MOVIES_PLS}/movies_harry_potter.m3u
  local FILE_MOVIES_STAR_WARS_PLS=${PATH_MOVIES_PLS}/movies_star_wars.m3u   
  local FILE_MOVIES_STUDIO_GHIBLI_PLS=${PATH_MOVIES_PLS}/movies_studio_ghibli.m3u

  local PATH_MOVIES_VIDEO_FILES=${PATH_VIDEO_FILES_ROOT}/movies

  local FILTER_MOVIES_HARRY_POTTER="fantasy/harry_potter"
  local FILTER_MOVIES_HARRY_POTTER_REMOVE="EMPTY-FILTER"
  local FILTER_MOVIES_STAR_WARS="fantasy/star_wars"
  local FILTER_MOVIES_STAR_WARS_REMOVE="EMPTY-FILTER"
  local FILTER_MOVIES_DC="heroes/dc"
  local FILTER_MOVIES_DC_REMOVE="EMPTY-FILTER"
  local FILTER_MOVIES_MARVEL="heroes/marvel"
  local FILTER_MOVIES_MARVEL_REMOVE="EMPTY-FILTER"
  local FILTER_MOVIES_DISNEY="animated/disney"
  local FILTER_MOVIES_DISNEY_REMOVE="EMPTY-FILTER"
  local FILTER_MOVIES_STUDIO_GHIBLI="animated/studio_ghibli"
  local FILTER_MOVIES_STUDIO_GHIBLI_REMOVE="EMPTY-FILTER"

  local MOVIES_TYPE_LIST=`ls -1 ${PATH_MOVIES_VIDEO_FILES}`
  echo -e "${MOVIES_TYPE_LIST}" | while read MOVIE_TYPE; do
    createPlaylist ${PATH_MOVIES_VIDEO_FILES}/${MOVIE_TYPE} ${PATH_MOVIES_PLS}/movies_${MOVIE_TYPE}_all.m3u ${PATH_VIDEO_FILES_ROOT} &
  done

  createPlaylist ${PATH_MOVIES_VIDEO_FILES} ${FILE_MOVIES_ALL_PLS} ${PATH_VIDEO_FILES_ROOT} &
   
  createFilteredPlaylist ${PATH_MOVIES_VIDEO_FILES} ${FILE_MOVIES_DC_PLS} ${PATH_VIDEO_FILES_ROOT} "${FILTER_MOVIES_DC}" "${FILTER_MOVIES_DC_REMOVE}" &
  createFilteredPlaylist ${PATH_MOVIES_VIDEO_FILES} ${FILE_MOVIES_MARVEL_PLS} ${PATH_VIDEO_FILES_ROOT} "${FILTER_MOVIES_MARVEL}" "${FILTER_MOVIES_MARVEL_REMOVE}" &
  createFilteredPlaylist ${PATH_MOVIES_VIDEO_FILES} ${FILE_MOVIES_DISNEY_PLS} ${PATH_VIDEO_FILES_ROOT} "${FILTER_MOVIES_DISNEY}" "${FILTER_MOVIES_DISNEY_REMOVE}" &
  createFilteredPlaylist ${PATH_MOVIES_VIDEO_FILES} ${FILE_MOVIES_HARRY_POTTER_PLS} ${PATH_VIDEO_FILES_ROOT} "${FILTER_MOVIES_HARRY_POTTER}" "${FILTER_MOVIES_HARRY_POTTER_REMOVE}" &
  createFilteredPlaylist ${PATH_MOVIES_VIDEO_FILES} ${FILE_MOVIES_STAR_WARS_PLS} ${PATH_VIDEO_FILES_ROOT} "${FILTER_MOVIES_STAR_WARS}" "${FILTER_MOVIES_STAR_WARS_REMOVE}" &
  createFilteredPlaylist ${PATH_MOVIES_VIDEO_FILES} ${FILE_MOVIES_STUDIO_GHIBLI_PLS} ${PATH_VIDEO_FILES_ROOT} "${FILTER_MOVIES_STUDIO_GHIBLI}" "${FILTER_MOVIES_STUDIO_GHIBLI_REMOVE}" &
}

createMusicVideoPlaylists() {
  local PATH_MUSIC_VIDEOS_PLS=${PATH_VLC_PLS_ROOT}/music_videos
  local FILE_MUSIC_VIDEOS_ALL_PLS=${PATH_MUSIC_VIDEOS_PLS}/music_videos_all.m3u
  local PATH_MUSIC_VIDEO_FILES=${PATH_VIDEO_FILES_ROOT}/music_videos
  createPlaylist ${PATH_MUSIC_VIDEO_FILES} ${FILE_MUSIC_VIDEOS_ALL_PLS} ${PATH_VIDEO_FILES_ROOT} & 
}

createSeriesPlaylists() {
  local PATH_SERIES_PLS=${PATH_VLC_PLS_ROOT}/series
  local FILE_SERIES_ALL_PLS=${PATH_SERIES_PLS}/series_all.m3u
  local PATH_SERIES_VIDEO_FILES=${PATH_VIDEO_FILES_ROOT}/series
  
  local SERIES=`ls -1 ${PATH_SERIES_VIDEO_FILES}`
  echo -e "${SERIES}" | while read SERIE; do
    createPlaylist ${PATH_SERIES_VIDEO_FILES}/${SERIE} ${PATH_SERIES_PLS}/${SERIE}_all.m3u ${PATH_VIDEO_FILES_ROOT} &
  done

  createPlaylist ${PATH_SERIES_VIDEO_FILES} ${FILE_SERIES_ALL_PLS} ${PATH_VIDEO_FILES_ROOT} &

  wait
}

createTennisPlaylists() {
  local PATH_TENNIS_PLS=${PATH_VLC_PLS_ROOT}/tennis

  local FILE_TENNIS_ALL_PLS=${PATH_TENNIS_PLS}/tennis_all.m3u
  local FILE_TENNIS_HEWITT_PLS=${PATH_TENNIS_PLS}/tennis_hewitt.m3u
  local FILE_TENNIS_NADAL_PLS=${PATH_TENNIS_PLS}/tennis_nadal.m3u
  local FILE_TENNIS_FEDERER_PLS=${PATH_TENNIS_PLS}/tennis_federer.m3u

  local PATH_TENNIS_VIDEO_FILES=${PATH_VIDEO_FILES_ROOT}/tenis

  local FILTER_TENNIS_HEWITT="Hewitt\|hewitt"
  local FILTER_TENNIS_HEWITT_REMOVE="EMPTY-FILTER"
  local FILTER_TENNIS_NADAL="Nadal\|nadal"
  local FILTER_TENNIS_NADAL_REMOVE="EMPTY-FILTER"
  local FILTER_TENNIS_FEDERER="Federer\|federer"
  local FILTER_TENNIS_FEDERER_REMOVE="EMPTY-FILTER"

  createPlaylist ${PATH_TENNIS_VIDEO_FILES} ${FILE_TENNIS_ALL_PLS} ${PATH_VIDEO_FILES_ROOT} &

  createFilteredPlaylist ${PATH_TENNIS_VIDEO_FILES} ${FILE_TENNIS_HEWITT_PLS} ${PATH_VIDEO_FILES_ROOT} "${FILTER_TENNIS_HEWITT}" "${FILTER_TENNIS_HEWITT_REMOVE}" &
  createFilteredPlaylist ${PATH_TENNIS_VIDEO_FILES} ${FILE_TENNIS_NADAL_PLS} ${PATH_VIDEO_FILES_ROOT} "${FILTER_TENNIS_NADAL}" "${FILTER_TENNIS_NADAL_REMOVE}" &
  createFilteredPlaylist ${PATH_TENNIS_VIDEO_FILES} ${FILE_TENNIS_FEDERER_PLS} ${PATH_VIDEO_FILES_ROOT} "${FILTER_TENNIS_FEDERER}" "${FILTER_TENNIS_FEDERER_REMOVE}" &
}

createPlaylist() {
  local PATH_CURRENT_VIDEO_FILES=$1 
  local FILE_CURRENT_OUTPUT=$2
  local PATH_CURRENT_VIDEO_FILES_ROOT=$3 
  log.info "Creating playlist from directory: ${COL_PURPLE}${PATH_CURRENT_VIDEO_FILES}${COL_DEFAULT_LOG} and writing playlist to file: ${COL_PURPLE}${FILE_CURRENT_OUTPUT}${COL_DEFAULT_LOG}"
  echo "#EXTM3U" > ${FILE_CURRENT_OUTPUT} 
  find ${PATH_CURRENT_VIDEO_FILES} | grep --ignore-case -e ${MEDIA_TYPES_REGEX} | grep --ignore-case -v -e "sample" |sort | while read FILE; do
      local FILE_NAME=${FILE#${PATH_CURRENT_VIDEO_FILES_ROOT}} 
      local FILE_WITHOUT_ROOT_PREFIX=${FILE#$ROOT_PREFIX}
      echo "#EXTINF:0,${FILE_NAME}-${FILE_NAME}" >> ${FILE_CURRENT_OUTPUT}
      echo "${FILE_WITHOUT_ROOT_PREFIX}" >> ${FILE_CURRENT_OUTPUT}
  done
  checkCommandStatus "$?"
}

createFilteredPlaylist() {
  local PATH_CURRENT_VIDEO_FILES=$1 
  local FILE_CURRENT_OUTPUT=$2
  local PATH_CURRENT_VIDEO_FILES_ROOT=$3 
  local FILTER=$4
  local FILTER_REMOVE=$5
  log.info "Creating playlist from directory: ${COL_PURPLE}${PATH_CURRENT_VIDEO_FILES}${COL_DEFAULT_LOG} and writing playlist to file: ${COL_PURPLE}${FILE_CURRENT_OUTPUT}${COL_DEFAULT_LOG}"
  echo "#EXTM3U" > ${FILE_CURRENT_OUTPUT} 
  find ${PATH_CURRENT_VIDEO_FILES} | grep --ignore-case -e ${MEDIA_TYPES_REGEX} | grep --ignore-case -v -e "sample" | grep --ignore-case -e "${FILTER}" | grep --ignore-case -v -e "${FILTER_REMOVE}" | sort | while read FILE; do
      local FILE_NAME=${FILE#${PATH_CURRENT_VIDEO_FILES_ROOT}} 
      local FILE_WITHOUT_ROOT_PREFIX=${FILE#$ROOT_PREFIX}
      echo "#EXTINF:0,${FILE_NAME}-${FILE_NAME}" >> ${FILE_CURRENT_OUTPUT}
      echo "${FILE_WITHOUT_ROOT_PREFIX}" >> ${FILE_CURRENT_OUTPUT}
  done
  checkCommandStatus "$?"
}

main "$@"
