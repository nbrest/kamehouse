#!/bin/bash

main() {
  cat ${HOME}/.kamehouse/config/.shell/shell.pwd | grep "MARIADB_PASS_KAMEHOUSE"
}

main "$@"