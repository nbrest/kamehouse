#!/bin/bash

source ${HOME}/.kamehouse/opt/kamehouse-shell/bin/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

initScriptEnv() {
  ALLOW_PARALLEL_RUNS=true
}

mainProcess() {
  fixEol
}

fixEol() {
  log.info "Fixing end of line on scripts in `pwd`"
  #find . -regex ".*sh" -type f -exec vim {} -c "set ff=unix" -c ":wq" \;
  find . -regex ".*sh" -type f -exec sed -i 's/\r$//' {} \;
}

main "$@"
