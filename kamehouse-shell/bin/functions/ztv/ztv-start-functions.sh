
initScriptConfigZtvStart() {
  # Set to true to use a visualizer effect when loading music playlists and files
  # Besides setting true here, the visualizer needs to be enabled in ZTV Players app configuration
  #       - Preferences > Audio > Visualization: Visualizer Filter
  # To turn off, set to false here and disable in ZTV Player configuration
  #       - Preferences > Audio > Visualization: Disabled
  ZTV_MUSIC_USE_VISUALIZER=false
}

setZtvStartParams() {
  log.debug "Setting ztv start params"
  local FILE_EXT=${FILE_TO_PLAY: -3}
  local MUSIC_PLAYLIST_RX=.*${PLAYLISTS_PATH}/music/.*
  local IS_MUSIC_PLAYLIST=false

  if [ "${FILE_EXT}" == "mp3" ]; then
    log.debug "File to play is an mp3"
    IS_MUSIC_PLAYLIST=true
  fi
  
  if [[ "${FILE_TO_PLAY}" =~ ${MUSIC_PLAYLIST_RX} ]]; then
    log.debug "File to play is a music playlist"
    IS_MUSIC_PLAYLIST=true
  fi

  if ${IS_MUSIC_PLAYLIST}; then
    if ${ZTV_MUSIC_USE_VISUALIZER}; then
      log.info "Using visualizer filter. The filter needs to be enabled also on ZTV Player config"
      ZTV_PARAMS="${ZTV_PARAMS} --effect-list=spectrum"
    else
      log.info "Starting ZTV minimized. The visualization filter needs to be disabled on ZTV Player config"
      ZTV_PARAMS="${ZTV_PARAMS} --qt-start-minimized --qt-system-tray"
    fi
  fi
}
