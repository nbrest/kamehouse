#!/bin/bash

main() {
  cat ${HOME}/.kamehouse/config/keys/.kamehouse-secrets.cfg | grep "MARIADB_PASS_KAMEHOUSE"
}

main "$@"