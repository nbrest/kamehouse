#!/bin/bash

main() {
  cat ${HOME}/.kamehouse/.shell/.cred | grep "MARIADB_PASS_KAMEHOUSE"
}

main "$@"