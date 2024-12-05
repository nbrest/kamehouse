#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/functions/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 99
fi

mainProcess() {
  FILE_TO_PLAY="`sed 's#"##Ig' <<<"${FILE_TO_PLAY}"`"
  log.info "Playing file ${FILE_TO_PLAY}"
  if ${IS_LINUX_HOST}; then
    XDG_RUNTIME_DIR=/run/user/$(id -u) DISPLAY=:0.0 vlc ${FILE_TO_PLAY} >> /dev/null 2>&1
  else 
    vlc.exe ${FILE_TO_PLAY} >> /dev/null 2>&1
  fi
}

parseArguments() {
  while getopts ":f:" OPT; do
    case $OPT in
    ("f")
      FILE_TO_PLAY="$OPTARG"
      ;;
    (\?)
      parseInvalidArgument "$OPTARG"
      ;;
    esac
  done 
}

setEnvFromArguments() {
  checkRequiredOption "-f" "${FILE_TO_PLAY}" 
}

printHelpOptions() {
  addHelpOption "-f file" "File to play" "r"
}

main "$@"
