#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 99
fi

LOG_PROCESS_TO_FILE=false
SERVICE="wssh"

mainProcess() {
  log.info "Call this script with nohup"
  # --origin='ssh.nicobrest.com,pi,192.168.0.129' --xsrf=false
  ${HOME}/.local/bin/wssh --port=3901 --origin='*' --debug --xsrf=false > ${HOME}/logs/wssh.log 2>&1 &
}

main "$@"
