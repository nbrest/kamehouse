#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/functions/common-functions.sh
if [ "$?" != "0" ]; then
	echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
	exit 99
fi

USE_SCHEDULED_TASK=false

mainProcess() {
  if ${USE_SCHEDULED_TASK}; then
    log.info "Locking screen using scheduled task"
    ${HOME}/programs/kamehouse-shell/bin/win/bat/screen-lock-scheduled-task.bat
  else
    log.info "Locking screen"
    rundll32.exe user32.dll,LockWorkStation
  fi
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
      --use-scheduled-task)
        USE_SCHEDULED_TASK=true
        ;;
      -?|-??*)
        parseInvalidArgument "${CURRENT_OPTION}"
        ;;        
    esac
  done    
}

printHelpOptions() {
  addHelpOption "--use-scheduled-task" "Use scheduled task to lock screen. Use this when running from an ssh session"
}

main "$@"
