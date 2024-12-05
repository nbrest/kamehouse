#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/functions/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 99
fi
# Import kamehouse functions
source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing kamehouse-functions.sh\033[0;39m"
  exit 99
fi

FILE_TO_PLAY=""
SCRIPT_DATA_DIR="vlc-start-from-ssh"
TEMP_PLAYLIST_NAME="${SCRIPT_DATA_DIR}.m3u"
TEMP_PLAYLIST_PATH="${HOME}/programs/kamehouse-shell/data/${SCRIPT_DATA_DIR}"
TEMP_PLAYLIST_FILE="${TEMP_PLAYLIST_PATH}/${TEMP_PLAYLIST_NAME}"
TEMP_PLAYLIST_FILE_WIN="${WIN_USER_HOME}\\programs\\kamehouse-shell\\data\\${SCRIPT_DATA_DIR}\\${TEMP_PLAYLIST_NAME}"

mainProcess() {
  startVlcFromSsh
}

startVlcFromSsh() {
  FILE_TO_PLAY="`sed 's#"##Ig' <<<"${FILE_TO_PLAY}"`"
  log.info "Playing file ${FILE_TO_PLAY}"
  if ${IS_LINUX_HOST}; then
    XDG_RUNTIME_DIR=/run/user/$(id -u) DISPLAY=:0.0 vlc ${FILE_TO_PLAY} >> /dev/null 2>&1
  else 
    mkdir -p ${TEMP_PLAYLIST_PATH}
    echo "${FILE_TO_PLAY}" > ${TEMP_PLAYLIST_FILE}
    log.info "${TEMP_PLAYLIST_FILE} contents"
    cat ${TEMP_PLAYLIST_FILE}
    log.info "Running vlc-start-from-ssh.ps1"
    powershell.exe -c ${KAMEHOUSE_SHELL_PS1_PATH}\\vlc-start-from-ssh.ps1 "${TEMP_PLAYLIST_FILE_WIN}" ""
    killRogueFileExplorerProcesses
  fi
}

killRogueFileExplorerProcesses() {
  # disabled: using psexec doesn't create the rogue file explorer windows as calling explorer.exe directly
  return
  
  log.info "Attempting to kill rogue file explorer windows, if present"
  local ROUNDS=5
  local ROUND=$((ROUNDS))
  while [ $ROUND -gt 0 ]; do
    log.info "Killing File Explorer windows. Round ${ROUND}"
    powershell.exe -c "taskkill.exe /FI \"WINDOWTITLE eq File Explorer\""
    powershell.exe -c "taskkill.exe /FI \"WINDOWTITLE eq Documents\""
    sleep 3
    : $((ROUND--))
  done
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
