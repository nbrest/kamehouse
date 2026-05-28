
setDefaultScriptConfigVlcStart() {
  # Set to true to use a visualizer effect when loading music playlists and files
  # Besides setting true here, the visualizer needs to be enabled in vlc players app configuration
  #       - Preferences > Audio > Visualization: Visualizer Filter
  # To turn off, set to false here and disable in vlc player configuration
  #       - Preferences > Audio > Visualization: Disabled
  VLC_MUSIC_USE_VISUALIZER=false
}

setVlcStartParams() {
  log.debug "Setting vlc start params"
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
    if ${VLC_MUSIC_USE_VISUALIZER}; then
      log.info "Using visualizer filter. The filter needs to be enabled also on vlc player config"
      VLC_PARAMS="${VLC_PARAMS} --effect-list=spectrum"
    else
      log.info "Starting vlc minimized. The visualization filter needs to be disabled on vlc player config"
      VLC_PARAMS="${VLC_PARAMS} --qt-start-minimized --qt-system-tray"
    fi
  fi
}
