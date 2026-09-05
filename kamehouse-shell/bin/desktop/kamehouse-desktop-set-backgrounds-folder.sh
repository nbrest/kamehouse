#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

initScriptConfig() {
  # Kamehouse Destkop backgrounds folder relative to ${HOME} as set in kamehouse-desktop.cfg
  BACKGROUNDS_FOLDER="/.kamehouse/data/desktop/backgrounds"
}

initScriptEnv() {
  FOLDER=""
}

mainProcessLin() {
  if [ -n "${FOLDER}" ]; then
    BACKGROUNDS_FOLDER=${BACKGROUNDS_FOLDER}/${FOLDER}
  fi
  log.info "Setting kamehouse-desktop backgrounds to ${COL_PURPLE}${BACKGROUNDS_FOLDER}"
  updateKameHouseDesktopConfig "background_slideshow_widget" "images_src_path" "${BACKGROUNDS_FOLDER}"
  log.warn "${COL_YELLOW}Restart kamehouse-desktop for the changes to take effect"
}

parseArguments() {
  local OPTIONS=("$@")
  for i in "${!OPTIONS[@]}"; do
    local CURRENT_OPTION="${OPTIONS[i]}"
    if [ "${CURRENT_OPTION:0:1}" != "-" ]; then
      continue
    fi
    local CURRENT_OPTION_ARG="${OPTIONS[i+1]}"
    case "${CURRENT_OPTION}" in
      -f|--folder)
        FOLDER=${CURRENT_OPTION_ARG}
        ;;
      -?|-??*)
        parseInvalidArgument "${CURRENT_OPTION}"
        ;;        
    esac
  done    
}

printHelpOptions() {
  addHelpOption "-f --folder" "Backgrounds folder subpath from ~${BACKGROUNDS_FOLDER}"
}

main "$@" 
