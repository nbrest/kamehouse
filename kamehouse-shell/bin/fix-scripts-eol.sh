#!/bin/bash

SCRIPT_NAME=`basename "$0"`
COL_BLUE="\033[1;34m"
COL_BOLD="\033[1m"
COL_CYAN="\033[1;36m"
COL_GREEN="\033[1;32m"
COL_NORMAL="\033[0;39m"
COL_PURPLE="\033[1;35m"
COL_RED="\033[1;31m"
COL_YELLOW="\033[1;33m"
COL_PURPLE_STD="\033[0;35m"
COL_MESSAGE=${COL_GREEN}

main() {
  fixEol
}

fixEol() {
  log.info "Fixing end of line"
  #find . -regex ".*sh" -type f -exec vim {} -c "set ff=unix" -c ":wq" \;
  find . -regex ".*sh" -type f -exec sed -i 's/\r$//' {} \;
}

log.info() {
  local ENTRY_DATE="${COL_CYAN}$(date +%Y-%m-%d' '%H:%M:%S)${COL_NORMAL}"
  local LOG_MESSAGE=$1
  echo -e "${ENTRY_DATE} - [${COL_BLUE}INFO${COL_NORMAL}] - ${COL_PURPLE_STD}${SCRIPT_NAME}${COL_NORMAL} - ${COL_MESSAGE}${LOG_MESSAGE}${COL_NORMAL}"
}

main "$@"
