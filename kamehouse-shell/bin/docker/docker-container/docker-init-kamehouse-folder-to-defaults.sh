#!/bin/bash

source ${HOME}/.kamehouse/opt/kamehouse-shell/bin/functions/common-functions.sh
if [ "$?" != "0" ]; then echo "Error importing common-functions.sh" ; exit 99 ; fi

mainProcess() {
  log.info "Reinit container .kamehouse folder with docker defaults"
  
  log.info "Reinit kamehouse.cfg"
  mkdir -p ${HOME}/.kamehouse/conf/
  cp -v -f ${HOME}/.kamehouse/git/kamehouse/docker/setup-kamehouse/conf/kamehouse.cfg ${HOME}/.kamehouse/conf/
  cp -v -f ${HOME}/.kamehouse/git/kamehouse/docker/setup-kamehouse/conf/kamehouse-desktop.toml ${HOME}/.kamehouse/conf/
  cp -v -f ${HOME}/.kamehouse/git/kamehouse/docker/setup-kamehouse/conf/kamehouse-groot.toml ${HOME}/.kamehouse/conf/
  cp -v -f ${HOME}/.kamehouse/git/kamehouse/docker/setup-kamehouse/conf/kamehouse-snape.toml ${HOME}/.kamehouse/conf/

  log.info "Reinit keys"
  mkdir -p ${HOME}/.kamehouse/secrets
  cp -v -f ${HOME}/.kamehouse/git/kamehouse/docker/setup-kamehouse/secrets/.kamehouse-secrets.cfg.enc ${HOME}/.kamehouse/secrets/
  cp -v -f ${HOME}/.kamehouse/git/kamehouse/kamehouse-commons-core/src/test/resources/commons/keys/sample.pkcs12 ${HOME}/.kamehouse/secrets/kamehouse.pkcs12
  cp -v -f ${HOME}/.kamehouse/git/kamehouse/kamehouse-commons-core/src/test/resources/commons/keys/sample.crt ${HOME}/.kamehouse/secrets/kamehouse.crt
  cp -v -f ${HOME}/.kamehouse/git/kamehouse/kamehouse-commons-core/src/test/resources/commons/keys/sample.key ${HOME}/.kamehouse/secrets/kamehouse.key
  cp -v -f ${HOME}/.kamehouse/git/kamehouse/kamehouse-commons-core/src/test/resources/commons/keys/sample.pub ${HOME}/.kamehouse/secrets/kamehouse.pub
  cp -v -f ${HOME}/.kamehouse/git/kamehouse/kamehouse-commons-core/src/test/resources/commons/keys/secrets.key.enc ${HOME}/.kamehouse/secrets/kamehouse-secrets.key.enc

  log.info "Reinit mariadb dump data"
  mkdir -p ${HOME}/.kamehouse/backup/db/dump/old
  cp -v -f ${HOME}/.kamehouse/git/kamehouse/kamehouse-shell/sql/mariadb/dump-kamehouse.sql ${HOME}/.kamehouse/backup/db/dump

  log.info "Reinit other .kamehouse dirs"
  mkdir -p ${HOME}/.kamehouse/conf
  mkdir -p ${HOME}/.kamehouse/backup/local
  mkdir -p ${HOME}/.kamehouse/data
}

main "$@"
