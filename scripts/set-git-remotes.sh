#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/common-functions.sh
if [ "$?" != "0" ]; then
	echo "`date +%Y-%m-%d' '%H:%M:%S` - [ERROR] - Error importing common-functions.sh" ; exit 99
fi
source ${HOME}/programs/kamehouse-shell/bin/common/functions/git/git-remotes-functions.sh
if [ "$?" != "0" ]; then
	echo "`date +%Y-%m-%d' '%H:%M:%S` - [ERROR] - Error importing git-remotes-functions.sh" ; exit 99
fi

initScriptEnv() {
  REPOSITORY_NAME="kamehouse"
}

main "$@"
