#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/common-functions.sh
if [ "$?" != "0" ]; then echo "`date +%Y-%m-%d' '%H:%M:%S` - [ERROR] - Error importing common-functions.sh" ; exit 99 ; fi

mainProcess() {
  cd ${HOME}/.ssh

  log.info "Converting private key id_rsa"
  log.debug "openssl pkcs8 -topk8 -inform PEM -outform PEM -nocrypt -in id_rsa -out id_rsa.pkcs8"
  openssl pkcs8 -topk8 -inform PEM -outform PEM -nocrypt -in id_rsa -out id_rsa.pkcs8

  log.info "Converting public key id_rsa.pub"
  log.debug "ssh-keygen -f id_rsa -e -m pem > id_rsa.pem"
  ssh-keygen -f id_rsa -e -m pem > id_rsa.pem

  log.debug "openssl rsa -RSAPublicKey_in -in id_rsa.pem -pubout > id_rsa.pub.pkcs8"
  openssl rsa -RSAPublicKey_in -in id_rsa.pem -pubout > id_rsa.pub.pkcs8
}

main "$@"
