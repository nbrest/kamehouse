#!/bin/bash

# Import common functions
source ${HOME}/my.scripts/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 1
fi

mainProcess() {
  local FILE=$1
  if [ -z ${FILE} ]; then
    log.error "Pass a file to convert as parameter"
    exit 1
  fi 
  log.info "Converting line endings from windows to linux for file ${FILE}"
  vim ${FILE} -c "set ff=unix" -c ":wq"
}

main "$@"
