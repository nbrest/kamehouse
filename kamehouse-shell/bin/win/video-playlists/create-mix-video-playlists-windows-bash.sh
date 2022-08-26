#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 1
fi
source ${HOME}/programs/kamehouse-shell/bin/common/video-playlists/video-playlists-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing video-playlists-functions.sh\033[0;39m"
  exit 1
fi

mainProcess() {
  defineVariables
  createPlaylists
  
  wait
}

createPlaylists() {
  createCtDbzSaintSeiyaPoTMix & 
  createDbzSaintSeiyaMix &
  createDbzSaintSeiyaMoviesMix &
  createPoTRanmaMix &
  
  createAnimeAllMoviesMix &
  
  createAnimeBestMix &
  createAnimeBestConanMix &

  createDcComicsMoviesMix &
  
  createCartoons90sMix &
  createCartoons80sMix &
  createCartoonsOtherMix &

  createAnimeAllCartoonsAllMix &

  log.info "Waiting for background processes to finish"
  jobs -l
  wait
  
  createAnimeAllDcComicsMoviesMix
  
  createAnimeBestDcComicsMix
  createAnimeBestDcComicsMarvelComicsMix
}

defineVariables() {
  #########################
  ### Root Playlists Paths
  #########################
  PATH_VLC_PLS_ROOT=${PROJECT_DIR}/windows-bash/media-drive
  
  PATH_ANIME_PLS=${PATH_VLC_PLS_ROOT}/anime
  PATH_CARTOON_PLS=${PATH_VLC_PLS_ROOT}/cartoons
  PATH_SERIES_PLS=${PATH_VLC_PLS_ROOT}/series
  
  PATH_MIX_PLS=${PATH_VLC_PLS_ROOT}/mix
  
  ##########################
  ### Anime Playlists Paths
  ##########################
  PATH_CAPTAIN_TSUBASA_PLS=${PATH_ANIME_PLS}/captain_tsubasa
  PATH_DRAGONBALL_PLS=${PATH_ANIME_PLS}/dragonball
  PATH_DETECTIVE_CONAN_PLS=${PATH_ANIME_PLS}/detective_conan
  PATH_PRINCE_OF_TENNIS_PLS=${PATH_ANIME_PLS}/prince_of_tennis
  PATH_RANMA_PLS=${PATH_ANIME_PLS}/ranma
  PATH_SAINT_SEIYA_PLS=${PATH_ANIME_PLS}/saint_seiya

  ############################
  ### Cartoon Playlists Paths
  ############################
  ## DC Comics
  PATH_BATMAN_PLS=${PATH_CARTOON_PLS}/batman
  PATH_JUSTICE_LEAGUE_PLS=${PATH_CARTOON_PLS}/justice_league
  PATH_SUPERMAN_PLS=${PATH_CARTOON_PLS}/superman

  # 90s Cartoons
  PATH_ANIMANIACS_PLS=${PATH_CARTOON_PLS}/animaniacs
  PATH_DEXTERS_LAB_PLS=${PATH_CARTOON_PLS}/dexters_lab
  PATH_PINKYBRAIN_PLS=${PATH_CARTOON_PLS}/pinky_and_the_brain
  PATH_PETERPAN_PLS=${PATH_CARTOON_PLS}/peter_pan_and_the_pirates
  PATH_REBOOT_PLS=${PATH_CARTOON_PLS}/reboot
  PATH_SCOOBY_DOO_PLS=${PATH_CARTOON_PLS}/scooby_doo
  PATH_SOUTHPARK_PLS=${PATH_CARTOON_PLS}/southpark
  PATH_TINY_TOONS_PLS=${PATH_CARTOON_PLS}/tiny_toons

  ## 80s Cartoons
  PATH_HEMAN_PLS=${PATH_CARTOON_PLS}/heman
  PATH_NINJA_TURTLES_PLS=${PATH_CARTOON_PLS}/ninja_turtles
  PATH_THE_SMURFS_PLS=${PATH_CARTOON_PLS}/the_smurfs
  PATH_THUNDERCATS_PLS=${PATH_CARTOON_PLS}/thundercats
  PATH_TRANSFORMERS_PLS=${PATH_CARTOON_PLS}/transformers

  # Other Cartoons
  PATH_LOONEY_TUNES_PLS=${PATH_CARTOON_PLS}/looney_tunes
  PATH_STAR_WARS_CARTOON_PLS=${PATH_CARTOON_PLS}/star_wars
  PATH_THE_FLINTSTONES_PLS=${PATH_CARTOON_PLS}/the_flintstones
  PATH_THE_SIMPSONS_PLS=${PATH_CARTOON_PLS}/the_simpsons
  PATH_TOM_AND_JERRY_PLS=${PATH_CARTOON_PLS}/tom_and_jerry
  PATH_TOP_CAT_PLS=${PATH_CARTOON_PLS}/top_cat
  PATH_WHACKY_RACES_PLS=${PATH_CARTOON_PLS}/whacky_races

  ########################
  ### All Playlists Files (existing playlists)
  ########################
  FILE_ANIME_ALL_PLS=${PATH_ANIME_PLS}/anime_all.m3u
  FILE_CARTOON_ALL_PLS=${PATH_CARTOON_PLS}/cartoons_all.m3u
  FILE_DC_COMICS_PLS=${PATH_CARTOON_PLS}/dc_comics_all.m3u
  FILE_MARVEL_COMICS_PLS=${PATH_CARTOON_PLS}/marvel_all.m3u

  ########################
  ### Mix Playlists Files
  ########################
  FILE_MIX_80S_CARTOONS_PLS=${PATH_MIX_PLS}/cartoons_80s_mix.m3u
  FILE_MIX_90S_CARTOONS_PLS=${PATH_MIX_PLS}/cartoons_90s_mix.m3u
  FILE_MIX_OTHER_CARTOONS_PLS=${PATH_MIX_PLS}/cartoons_other_mix.m3u
  
  FILE_MIX_ANIME_ALL_MOVIES_PLS=${PATH_MIX_PLS}/anime_all_movies_mix.m3u
  FILE_MIX_ANIME_ALL_DC_COMICS_MOVIES_PLS=${PATH_MIX_PLS}/anime_all_dc_comics_movies_mix.m3u
  FILE_MIX_ANIME_ALL_CARTOON_ALL_PLS=${PATH_MIX_PLS}/anime_all_cartoons_all_mix.m3u

  FILE_MIX_ANIME_BEST_PLS=${PATH_MIX_PLS}/anime_best_mix.m3u
  FILE_MIX_ANIME_BEST_DETECTIVE_CONAN_PLS=${PATH_MIX_PLS}/anime_best_detective_conan_mix.m3u
  FILE_MIX_ANIME_BEST_DC_COMICS_PLS=${PATH_MIX_PLS}/anime_best_dc_comics_mix.m3u
  FILE_MIX_ANIME_BEST_DC_MARVEL_PLS=${PATH_MIX_PLS}/anime_best_dc_marvel_mix.m3u

  FILE_MIX_CT_DBZ_SS_POT_PLS=${PATH_MIX_PLS}/ct_dbz_saintseiya_pot_mix.m3u
  
  FILE_MIX_DBZ_SS_PLS=${PATH_MIX_PLS}/dbz_saintseiya_mix.m3u
  FILE_MIX_DBZ_SS_MOVIES_PLS=${PATH_MIX_PLS}/dbz_saintseiya_movies_mix.m3u
  
  FILE_MIX_POT_RANMA_PLS=${PATH_MIX_PLS}/pot_ranma_mix.m3u
  
  FILE_MIX_DC_COMICS_MOVIES_PLS=${PATH_MIX_PLS}/dc_comics_movies_mix.m3u  
}

createCtDbzSaintSeiyaPoTMix() {
  # This playlist CAN run in backround. Doesn't depend on other playlists generated in this script

  #####################################################################
  ### captain tsubasa - dragonball - saint seiya - prince of tennis mix
  ######################################################################
  log.info "Creating playlist: ${COL_PURPLE}${FILE_MIX_CT_DBZ_SS_POT_PLS}" 

  cat ${PATH_DRAGONBALL_PLS}*.m3u > ${FILE_MIX_CT_DBZ_SS_POT_PLS} 
  checkCommandStatus "$?"

  cat ${PATH_CAPTAIN_TSUBASA_PLS}*.m3u >> ${FILE_MIX_CT_DBZ_SS_POT_PLS}
  checkCommandStatus "$?"

  cat ${PATH_SAINT_SEIYA_PLS}*.m3u >> ${FILE_MIX_CT_DBZ_SS_POT_PLS}
  checkCommandStatus "$?"

  cat ${PATH_PRINCE_OF_TENNIS_PLS}*.m3u >> ${FILE_MIX_CT_DBZ_SS_POT_PLS}
  checkCommandStatus "$?"
}

createDbzSaintSeiyaMix() {
  # This playlist CAN run in backround. Doesn't depend on other playlists generated in this script

  #################################
  ### dragonball - saint seiya mix
  #################################
  log.info "Creating playlist: ${COL_PURPLE}${FILE_MIX_DBZ_SS_PLS}" 
  
  cat ${PATH_DRAGONBALL_PLS}*.m3u > ${FILE_MIX_DBZ_SS_PLS} 
  checkCommandStatus "$?"

  cat ${PATH_SAINT_SEIYA_PLS}*.m3u >> ${FILE_MIX_DBZ_SS_PLS}
  checkCommandStatus "$?"
}

createDbzSaintSeiyaMoviesMix() {
  # This playlist CAN run in backround. Doesn't depend on other playlists generated in this script

  ########################################
  ### dragonball - saint seiya movies mix
  ########################################
  log.info "Creating playlist: ${COL_PURPLE}${FILE_MIX_DBZ_SS_MOVIES_PLS}" 
  
  cat ${PATH_DRAGONBALL_PLS}*movies*.m3u > ${FILE_MIX_DBZ_SS_MOVIES_PLS} 
  checkCommandStatus "$?"

  cat ${PATH_SAINT_SEIYA_PLS}*movies*.m3u >> ${FILE_MIX_DBZ_SS_MOVIES_PLS}
  checkCommandStatus "$?"
}

createPoTRanmaMix() {
  # This playlist CAN run in backround. Doesn't depend on other playlists generated in this script

  #################################
  ### prince of tennis - ranma mix
  #################################
  log.info "Creating playlist: ${COL_PURPLE}${FILE_MIX_POT_RANMA_PLS}" 
  
  cat ${PATH_PRINCE_OF_TENNIS_PLS}*.m3u > ${FILE_MIX_POT_RANMA_PLS}
  checkCommandStatus "$?"
  
  cat ${PATH_RANMA_PLS}*.m3u >> ${FILE_MIX_POT_RANMA_PLS}
  checkCommandStatus "$?"
}

createAnimeAllMoviesMix() {
  # This playlist CAN run in backround. Doesn't depend on other playlists generated in this script

  #####################
  ### anime all movies mix
  #####################
  log.info "Creating playlist: ${COL_PURPLE}${FILE_MIX_ANIME_ALL_MOVIES_PLS}" 
  
  cat ${PATH_DRAGONBALL_PLS}*movies*.m3u > ${FILE_MIX_ANIME_ALL_MOVIES_PLS}
  checkCommandStatus "$?"

  cat ${PATH_DETECTIVE_CONAN_PLS}*movies*.m3u >> ${FILE_MIX_ANIME_ALL_MOVIES_PLS}
  checkCommandStatus "$?"

  cat ${PATH_PRINCE_OF_TENNIS_PLS}*movies*.m3u >> ${FILE_MIX_ANIME_ALL_MOVIES_PLS}
  checkCommandStatus "$?"
  
  cat ${PATH_RANMA_PLS}*movies*.m3u >> ${FILE_MIX_ANIME_ALL_MOVIES_PLS}
  checkCommandStatus "$?"

  cat ${PATH_SAINT_SEIYA_PLS}*movies*.m3u >> ${FILE_MIX_ANIME_ALL_MOVIES_PLS}
  checkCommandStatus "$?"
} 

createAnimeBestMix() {
  # This playlist CAN run in backround. Doesn't depend on other playlists generated in this script

  ##############################################################################
  ### captain tsubasa - dragonball - saint seiya - prince of tennis - ranma mix
  ##############################################################################
  log.info "Creating playlist: ${COL_PURPLE}${FILE_MIX_ANIME_BEST_PLS}" 

  cat ${PATH_DRAGONBALL_PLS}*.m3u > ${FILE_MIX_ANIME_BEST_PLS} 
  checkCommandStatus "$?"

  cat ${PATH_CAPTAIN_TSUBASA_PLS}*.m3u >> ${FILE_MIX_ANIME_BEST_PLS}
  checkCommandStatus "$?"  

  cat ${PATH_SAINT_SEIYA_PLS}*.m3u >> ${FILE_MIX_ANIME_BEST_PLS}
  checkCommandStatus "$?"

  cat ${PATH_PRINCE_OF_TENNIS_PLS}*.m3u >> ${FILE_MIX_ANIME_BEST_PLS}
  checkCommandStatus "$?"
  
  cat ${PATH_RANMA_PLS}*.m3u >> ${FILE_MIX_ANIME_BEST_PLS}
  checkCommandStatus "$?"
}

createAnimeBestConanMix() {
  # This playlist CAN run in backround. Doesn't depend on other playlists generated in this script

  #####################################################################################
  ### captain tsubasa - dragonball - saint seiya - prince of tennis - ranma - conan mix
  #####################################################################################
  log.info "Creating playlist: ${COL_PURPLE}${FILE_MIX_ANIME_BEST_DETECTIVE_CONAN_PLS}" 

  cat ${PATH_DRAGONBALL_PLS}*.m3u > ${FILE_MIX_ANIME_BEST_DETECTIVE_CONAN_PLS} 
  checkCommandStatus "$?"

  cat ${PATH_CAPTAIN_TSUBASA_PLS}*.m3u >> ${FILE_MIX_ANIME_BEST_DETECTIVE_CONAN_PLS}
  checkCommandStatus "$?"  

  cat ${PATH_SAINT_SEIYA_PLS}*.m3u >> ${FILE_MIX_ANIME_BEST_DETECTIVE_CONAN_PLS}
  checkCommandStatus "$?"

  cat ${PATH_PRINCE_OF_TENNIS_PLS}*.m3u >> ${FILE_MIX_ANIME_BEST_DETECTIVE_CONAN_PLS}
  checkCommandStatus "$?"
  
  cat ${PATH_RANMA_PLS}*.m3u >> ${FILE_MIX_ANIME_BEST_DETECTIVE_CONAN_PLS}
  checkCommandStatus "$?"

  cat ${PATH_DETECTIVE_CONAN_PLS}*.m3u >> ${FILE_MIX_ANIME_BEST_DETECTIVE_CONAN_PLS}
  checkCommandStatus "$?"  
}

createDcComicsMoviesMix() {
  # This playlist CAN run in backround. Doesn't depend on other playlists generated in this script

  #########################
  ### dc comics movies mix
  #########################
  log.info "Creating playlist: ${COL_PURPLE}${FILE_MIX_DC_COMICS_MOVIES_PLS}" 
  
  cat ${PATH_BATMAN_PLS}*movies*.m3u > ${FILE_MIX_DC_COMICS_MOVIES_PLS}
  checkCommandStatus "$?"
  
  cat ${PATH_JUSTICE_LEAGUE_PLS}*movies*.m3u >> ${FILE_MIX_DC_COMICS_MOVIES_PLS}
  checkCommandStatus "$?"
  
  cat ${PATH_SUPERMAN_PLS}*movies*.m3u >> ${FILE_MIX_DC_COMICS_MOVIES_PLS}
  checkCommandStatus "$?"
}

createCartoons90sMix() {
  # This playlist CAN run in backround. Doesn't depend on other playlists generated in this script

  #####################
  ### 90s cartoons mix
  #####################
  log.info "Creating playlist: ${COL_PURPLE}${FILE_MIX_90S_CARTOONS_PLS}" 
  
  cat ${PATH_ANIMANIACS_PLS}*.m3u > ${FILE_MIX_90S_CARTOONS_PLS}
  checkCommandStatus "$?"

  cat ${PATH_DEXTERS_LAB_PLS}*.m3u >> ${FILE_MIX_90S_CARTOONS_PLS}
  checkCommandStatus "$?"

  cat ${PATH_PINKYBRAIN_PLS}*.m3u >> ${FILE_MIX_90S_CARTOONS_PLS}
  checkCommandStatus "$?"
  
  cat ${PATH_PETERPAN_PLS}*.m3u >> ${FILE_MIX_90S_CARTOONS_PLS}
  checkCommandStatus "$?"
  
  cat ${PATH_REBOOT_PLS}*.m3u >> ${FILE_MIX_90S_CARTOONS_PLS}
  checkCommandStatus "$?"

  cat ${PATH_SCOOBY_DOO_PLS}*.m3u >> ${FILE_MIX_90S_CARTOONS_PLS}
  checkCommandStatus "$?"

  cat ${PATH_SOUTHPARK_PLS}*.m3u >> ${FILE_MIX_90S_CARTOONS_PLS}
  checkCommandStatus "$?"

  cat ${PATH_TINY_TOONS_PLS}*.m3u >> ${FILE_MIX_90S_CARTOONS_PLS}
  checkCommandStatus "$?"
}

createCartoons80sMix() {
  # This playlist CAN run in backround. Doesn't depend on other playlists generated in this script

  #####################
  ### 80s cartoons mix
  #####################
  log.info "Creating playlist: ${COL_PURPLE}${FILE_MIX_80S_CARTOONS_PLS}" 
  
  cat ${PATH_HEMAN_PLS}*.m3u > ${FILE_MIX_80S_CARTOONS_PLS}
  checkCommandStatus "$?"
  
  cat ${PATH_NINJA_TURTLES_PLS}*.m3u >> ${FILE_MIX_80S_CARTOONS_PLS}
  checkCommandStatus "$?"

  cat ${PATH_THE_SMURFS_PLS}*.m3u >> ${FILE_MIX_80S_CARTOONS_PLS}
  checkCommandStatus "$?"

  cat ${PATH_THUNDERCATS_PLS}*.m3u >> ${FILE_MIX_80S_CARTOONS_PLS}
  checkCommandStatus "$?"

  cat ${PATH_TRANSFORMERS_PLS}*.m3u >> ${FILE_MIX_80S_CARTOONS_PLS}
  checkCommandStatus "$?"
}

createCartoonsOtherMix() {
  # This playlist CAN run in backround. Doesn't depend on other playlists generated in this script

  #######################
  ### Other cartoons mix
  #######################
  log.info "Creating playlist: ${COL_PURPLE}${FILE_MIX_OTHER_CARTOONS_PLS}" 
  
  cat ${PATH_LOONEY_TUNES_PLS}*.m3u > ${FILE_MIX_OTHER_CARTOONS_PLS}
  checkCommandStatus "$?"
  
  cat ${PATH_STAR_WARS_CARTOON_PLS}*.m3u >> ${FILE_MIX_OTHER_CARTOONS_PLS}
  checkCommandStatus "$?"

  cat ${PATH_THE_FLINTSTONES_PLS}*.m3u >> ${FILE_MIX_OTHER_CARTOONS_PLS}
  checkCommandStatus "$?"

  cat ${PATH_THE_SIMPSONS_PLS}*.m3u >> ${FILE_MIX_OTHER_CARTOONS_PLS}
  checkCommandStatus "$?"

  cat ${PATH_TOM_AND_JERRY_PLS}*.m3u >> ${FILE_MIX_OTHER_CARTOONS_PLS}
  checkCommandStatus "$?"
  
  cat ${PATH_TOP_CAT_PLS}*.m3u >> ${FILE_MIX_OTHER_CARTOONS_PLS}
  checkCommandStatus "$?"
  
  cat ${PATH_WHACKY_RACES_PLS}*.m3u >> ${FILE_MIX_OTHER_CARTOONS_PLS}
  checkCommandStatus "$?" 
}

createAnimeAllCartoonsAllMix() {
  # This playlist CAN run in backround. Doesn't depend on other playlists generated in this script

  #############################
  ### anime all - cartoons all mix
  #############################
  log.info "Creating playlist: ${COL_PURPLE}${FILE_MIX_ANIME_ALL_CARTOON_ALL_PLS}" 
  
  cat ${FILE_ANIME_ALL_PLS} > ${FILE_MIX_ANIME_ALL_CARTOON_ALL_PLS}
  checkCommandStatus "$?"
  
  cat ${FILE_CARTOON_ALL_PLS} >> ${FILE_MIX_ANIME_ALL_CARTOON_ALL_PLS}
  checkCommandStatus "$?"
}

######################################################################################
# The playlists below depend on some of the genrated above, so can't run in backgound
######################################################################################

createAnimeAllDcComicsMoviesMix() {
  # This playlist CAN'T run in backround. It depends on other playlists generated in this script

  #################################
  ### anime all - dc comics movies mix
  #################################
  log.info "Creating playlist: ${COL_PURPLE}${FILE_MIX_ANIME_ALL_DC_COMICS_MOVIES_PLS}" 
  
  cat ${FILE_MIX_ANIME_ALL_MOVIES_PLS} > ${FILE_MIX_ANIME_ALL_DC_COMICS_MOVIES_PLS}
  checkCommandStatus "$?"
  
  cat ${FILE_MIX_DC_COMICS_MOVIES_PLS} >> ${FILE_MIX_ANIME_ALL_DC_COMICS_MOVIES_PLS}
  checkCommandStatus "$?"
}

createAnimeBestDcComicsMix() {
  # This playlist CAN'T run in backround. It depends on other playlists generated in this script

  ##########################
  ### anime best - dc comics mix
  ##########################
  log.info "Creating playlist: ${COL_PURPLE}${FILE_MIX_ANIME_BEST_DC_COMICS_PLS}" 
  
  cat ${FILE_MIX_ANIME_BEST_PLS} > ${FILE_MIX_ANIME_BEST_DC_COMICS_PLS}
  checkCommandStatus "$?"
  
  cat ${FILE_DC_COMICS_PLS} >> ${FILE_MIX_ANIME_BEST_DC_COMICS_PLS}
  checkCommandStatus "$?"
}

createAnimeBestDcComicsMarvelComicsMix() {
  # This playlist CAN'T run in backround. It depends on other playlists generated in this script

  ##########################################
  ### anime best - dc comics - marvel comics mix
  ##########################################
  log.info "Creating playlist: ${COL_PURPLE}${FILE_MIX_ANIME_BEST_DC_MARVEL_PLS}" 
  
  cat ${FILE_MIX_ANIME_BEST_PLS} > ${FILE_MIX_ANIME_BEST_DC_MARVEL_PLS}
  checkCommandStatus "$?"
  
  cat ${FILE_DC_COMICS_PLS} >> ${FILE_MIX_ANIME_BEST_DC_MARVEL_PLS}
  checkCommandStatus "$?"
  
  cat ${FILE_MARVEL_COMICS_PLS} >> ${FILE_MIX_ANIME_BEST_DC_MARVEL_PLS}
  checkCommandStatus "$?"
}

main "$@"
