#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 149
fi

# Global variables
# LOG_PROCESS_TO_FILE=true

mainProcess() {
  pwd
  FILELIST=`find . -maxdepth 1 -name "[0-9] *" -type f | cut -c3-`
  while IFS= read -r FILENAME; do 
    echo mv -v \'"${FILENAME}"\' \'"0${FILENAME}"\' 
  done <<< ${FILELIST}

  REQUEST_CONFIRMATION_RX=^yes\|y$
  log.info "Execute the updates? (Y/n)"
  read SHOULD_PROCEED
  SHOULD_PROCEED=`echo "${SHOULD_PROCEED}" | tr '[:upper:]' '[:lower:]'`
  if [[ "${SHOULD_PROCEED}" =~ ${REQUEST_CONFIRMATION_RX} ]]; then
    log.info "Executing the operations"
  else
    log.warn "${COL_PURPLE}${SCRIPT_NAME}${COL_DEFAULT_LOG} cancelled by the user"
    exitProcess 2
  fi

  while IFS= read -r FILENAME; do 
    mv -v "${FILENAME}" "0${FILENAME}" 
  done <<< ${FILELIST}  
}

main "$@"
