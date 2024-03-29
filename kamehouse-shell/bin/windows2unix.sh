#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 99
fi

mainProcess() {
  local FILE=$1
  if [ -z ${FILE} ]; then
    log.error "Pass a file to convert as parameter"
    exitProcess ${EXIT_INVALID_ARG}
  fi 
  log.info "Converting line endings from windows to linux for file ${FILE}"
  vim ${FILE} -c "set ff=unix" -c ":wq"
}

main "$@"
