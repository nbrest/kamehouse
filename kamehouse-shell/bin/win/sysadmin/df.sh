#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/common-functions.sh
if [ "$?" != "0" ]; then echo "Error importing common-functions.sh" ; exit 99 ; fi

mainProcess() {
  log.info "Executing 'powershell.exe -c \"gdr\"'"
  powershell.exe -c "gdr"
}

main "$@"
