#!/bin/bash

main() {
  echo "Updating kamehouse-shell scripts from kamehouse git repository"
  mkdir -p ${HOME}/temp
  cp /home/nbrest/my.scripts/.cred/.cred ${HOME}/temp
  rm -r -f /home/nbrest/my.scripts
  cp -r -f /home/nbrest/git/kamehouse/kamehouse-shell/my.scripts /home/nbrest/
  mkdir -p /home/nbrest/my.scripts/.cred/
  cp ${HOME}/temp/.cred /home/nbrest/my.scripts/.cred/.cred
  chmod a+x -R /home/nbrest/my.scripts
}

main "$@"
