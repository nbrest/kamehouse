#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 9
fi

mainProcess() {
  log.info "Testing if file system switched to read-only. If so, ${COL_RED}reboot${COL_DEFAULT_LOG} and run ${COL_RED}fsck /dev/sda1 -y && exit"
  cd ${HOME}
  rm -f goku.txt
  echo "gohan `date`" > goku.txt
  log.info "goku.txt:"
  cat goku.txt
  rm -v goku.txt
  if [ "$?" != "0" ]; then
    log.error "FILE SYSTEM SWICTHED TO READ ONLY - NEEDS FIXING"
  else
    log.info "${COL_CYAN}SUCCESS!! File system is writable"
  fi
}

main "$@"
