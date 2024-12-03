#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/functions/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 99
fi

mainProcess() {
  log.info "Reinit container .kamehouse folder with docker defaults"
  
  log.info "Reinit kamehouse.cfg"
  mkdir -p ${HOME}/.kamehouse/
  cp -v -f ${HOME}/git/kamehouse/docker/config/kamehouse.cfg ${HOME}/.kamehouse/

  log.info "Reinit shell.pwd"
  mkdir -p ${HOME}/.kamehouse/.shell/
  cp -v -f ${HOME}/git/kamehouse/docker/keys/shell.pwd ${HOME}/.kamehouse/.shell/

  log.info "Reinit keys"
  mkdir -p ${HOME}/.kamehouse/keys
  cp -v -f ${HOME}/git/kamehouse/docker/keys/.*.pwd.enc ${HOME}/.kamehouse/keys
  cp -v -f ${HOME}/git/kamehouse/docker/keys/integration-test-cred.enc ${HOME}/.kamehouse/keys
  cp -v -f ${HOME}/git/kamehouse/kamehouse-commons-core/src/test/resources/commons/keys/sample.pkcs12 ${HOME}/.kamehouse/keys/kamehouse.pkcs12
  cp -v -f ${HOME}/git/kamehouse/kamehouse-commons-core/src/test/resources/commons/keys/sample.crt ${HOME}/.kamehouse/keys/kamehouse.crt

  log.info "Reinit mariadb dump data"
  mkdir -p ${HOME}/.kamehouse/mariadb/dump/old
  cp -v -f ${HOME}/git/kamehouse/kamehouse-shell/sql/mariadb/dump-kamehouse.sql ${HOME}/.kamehouse/mariadb/dump

  log.info "Reinit other .kamehouse dirs"
  mkdir -p ${HOME}/.kamehouse/httpd
}

main "$@"
