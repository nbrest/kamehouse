#!/bin/bash

# Import kamehouse functions
source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing kamehouse-functions.sh\033[0;39m"
  exit 99
fi

SKIP_LOG_START_FINISH=true
PLAYLIST_FILE=""

mainProcess() {
  if ${IS_LINUX_HOST}; then
    cat "${PLAYLIST_FILE}"
  else
    PLAYLIST_FILE="`sed 's#/#\\\#Ig' <<<"${PLAYLIST_FILE}"`"
    powershell.exe -c "cat ${PLAYLIST_FILE}"
  fi
}

parseArguments() {
  while getopts ":f:" OPT; do
    case $OPT in
    ("f")
      PLAYLIST_FILE="$OPTARG"
      ;;
    (\?)
      parseInvalidArgument "$OPTARG"
      ;;
    esac
  done 
}

setEnvFromArguments() {
  checkRequiredOption "-f" "${PLAYLIST_FILE}" 
}

printHelpOptions() {
  addHelpOption "-f file" "playlist file to load content from" "r"
}

main "$@"
