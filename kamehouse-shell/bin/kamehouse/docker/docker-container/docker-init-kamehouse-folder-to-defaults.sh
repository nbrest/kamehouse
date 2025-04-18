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
  mkdir -p ${HOME}/.kamehouse/config/
  cp -v -f ${HOME}/git/kamehouse/docker/config/kamehouse.cfg ${HOME}/.kamehouse/config/

  log.info "Reinit keys"
  mkdir -p ${HOME}/.kamehouse/config/keys
  cp -v -f ${HOME}/git/kamehouse/docker/keys/.*.pwd.enc ${HOME}/.kamehouse/config/keys
  cp -v -f ${HOME}/git/kamehouse/docker/keys/integration-test-cred.enc ${HOME}/.kamehouse/config/keys
  cp -v -f ${HOME}/git/kamehouse/docker/keys/.kamehouse-secrets.cfg.enc ${HOME}/.kamehouse/config/keys/
  cp -v -f ${HOME}/git/kamehouse/kamehouse-commons-core/src/test/resources/commons/keys/sample.pkcs12 ${HOME}/.kamehouse/config/keys/kamehouse.pkcs12
  cp -v -f ${HOME}/git/kamehouse/kamehouse-commons-core/src/test/resources/commons/keys/sample.crt ${HOME}/.kamehouse/config/keys/kamehouse.crt
  cp -v -f ${HOME}/git/kamehouse/kamehouse-commons-core/src/test/resources/commons/keys/sample.key ${HOME}/.kamehouse/config/keys/kamehouse.key
  cp -v -f ${HOME}/git/kamehouse/kamehouse-commons-core/src/test/resources/commons/keys/sample.pub ${HOME}/.kamehouse/config/keys/kamehouse.pub
  cp -v -f ${HOME}/git/kamehouse/kamehouse-commons-core/src/test/resources/commons/keys/secrets.key.enc ${HOME}/.kamehouse/config/keys/kamehouse-secrets.key.enc

  log.info "Reinit mariadb dump data"
  mkdir -p ${HOME}/.kamehouse/config/mariadb/dump/old
  cp -v -f ${HOME}/git/kamehouse/kamehouse-shell/sql/mariadb/dump-kamehouse.sql ${HOME}/.kamehouse/config/mariadb/dump

  log.info "Reinit other .kamehouse dirs"
  mkdir -p ${HOME}/.kamehouse/config
  mkdir -p ${HOME}/.kamehouse/server-config
  mkdir -p ${HOME}/.kamehouse/data
}

main "$@"
