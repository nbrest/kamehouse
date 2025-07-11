#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

initScriptEnv() {
  SERVICE="kamehouse-desktop"
  SERVICE_STARTUP="${HOME}/programs/kamehouse-shell/bin/kamehouse/desktop/kamehouse-desktop-startup.sh"
}

mainProcess() {
  if ${KEEP_ALIVE_SCRIPTS_DISABLED}; then
    log.info "keep alive scripts are ${COL_RED}disabled${COL_DEFAULT_LOG} in kamehouse.cfg. Switching to ${COL_YELLOW}enabled"
    sed -i -E "s/^#KEEP_ALIVE_SCRIPTS_DISABLED=.*/KEEP_ALIVE_SCRIPTS_DISABLED=/I" ${HOME}/.kamehouse/config/kamehouse.cfg
    sed -i -E "s/^KEEP_ALIVE_SCRIPTS_DISABLED=.*/KEEP_ALIVE_SCRIPTS_DISABLED=false/I" ${HOME}/.kamehouse/config/kamehouse.cfg
  else
    log.info "keep alive scripts are ${COL_YELLOW}enabled${COL_DEFAULT_LOG} in kamehouse.cfg. Switching to ${COL_RED}disabled"
    sed -i -E "s/^#KEEP_ALIVE_SCRIPTS_DISABLED=.*/KEEP_ALIVE_SCRIPTS_DISABLED=/I" ${HOME}/.kamehouse/config/kamehouse.cfg
    sed -i -E "s/^KEEP_ALIVE_SCRIPTS_DISABLED=.*/KEEP_ALIVE_SCRIPTS_DISABLED=true/I" ${HOME}/.kamehouse/config/kamehouse.cfg
  fi
  log.info "KEEP_ALIVE_SCRIPTS_DISABLED status in kamehouse.cfg"
  cat ${HOME}/.kamehouse/config/kamehouse.cfg | grep "KEEP_ALIVE_SCRIPTS_DISABLED="
}

main "$@"
