#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

setDefaultScriptConfig() {
  # Default source directory to compare kamehouse desktop backgrounds
  BACKGROUNDS_SRC_DIR="${HOME}/Downloads/kamehouse-desktop-backgrounds"
  
  # Default dest directory to compare kamehouse desktop backgrounds
  BACKGROUNDS_DEST_DIR="${HOME}/.kamehouse/data/desktop/backgrounds"
}

initScriptEnv() {
  REVERSE=false
}

mainProcessLin() {
  log.info "Comparing backgrounds folders"

  log.info "Source directory: ${BACKGROUNDS_SRC_DIR}"
  log.info "Destination directory: ${BACKGROUNDS_DEST_DIR}"
  local SORTED_SRC_DIR=$(find "${BACKGROUNDS_SRC_DIR}" -type f -exec basename {} \; | sort)
  local SORTED_DEST_DIR=$(find "${BACKGROUNDS_DEST_DIR}" -type f -exec basename {} \; | sort)

  log.info "Files in source directory but not in destination directory:"
  comm -23 <(echo "${SORTED_SRC_DIR}") <(echo "${SORTED_DEST_DIR}")

  local SRC_DIR_COUNT=$(echo "${SORTED_SRC_DIR}" | wc -l)
  local DEST_DIR_COUNT=$(echo "${SORTED_DEST_DIR}" | wc -l)
  log.info "Source dir count: ${SRC_DIR_COUNT}"
  log.info "Destination dir count: ${DEST_DIR_COUNT}"
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
      -r|--reverse)
        REVERSE=true
        ;;
      -?|-??*)
        parseInvalidArgument "${CURRENT_OPTION}"
        ;;        
    esac
  done    
}

setEnvFromArguments() {
  if ${REVERSE}; then
    log.info "Reversing source and dest directories for comparison"
    local BACKGROUNDS_SRC_DIR_TEMP="${BACKGROUNDS_SRC_DIR}"
    BACKGROUNDS_SRC_DIR="${BACKGROUNDS_DEST_DIR}"
    BACKGROUNDS_DEST_DIR="${BACKGROUNDS_SRC_DIR_TEMP}"
  fi
}

printHelpOptions() {
  addHelpOption "-r --reverse" "Reverse the source and dest directories for comparison"
}

main "$@" 
