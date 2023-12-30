#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 99
fi

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
