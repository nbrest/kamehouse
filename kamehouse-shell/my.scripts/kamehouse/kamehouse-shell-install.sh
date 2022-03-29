#!/bin/bash

main() {
  echo "Installing kamehouse-shell to ${HOME} ..."
  mkdir -p ${HOME}/temp
  cp ${HOME}/my.scripts/.cred/.cred ${HOME}/temp
  rm -r -f ${HOME}/my.scripts
  cp -r -f ${HOME}/git/kamehouse/kamehouse-shell/my.scripts ${HOME}/
  mkdir -p ${HOME}/my.scripts/.cred/
  mv ${HOME}/temp/.cred ${HOME}/my.scripts/.cred/.cred
  chmod a+x -R ${HOME}/my.scripts
  echo "Done"
}

main "$@"
