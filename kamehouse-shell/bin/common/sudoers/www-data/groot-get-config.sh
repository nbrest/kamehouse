#!/bin/bash

main() {
  cat ${HOME}/.kamehouse/.shell/shell.pwd | grep "MARIADB_PASS_KAMEHOUSE"
}

main "$@"