#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/common-functions.sh
if [ "$?" != "0" ]; then
	echo "$(date +%Y-%m-%d' '%H:%M:%S) - [ERROR] - An error occurred importing common-functions.sh"
	exit 1
fi

source ${HOME}/programs/kamehouse-shell/bin/common/backup/backup-server-functions.sh
if [ "$?" != "0" ]; then
	echo "$(date +%Y-%m-%d' '%H:%M:%S) - [ERROR] - An error occurred importing backup-server-functions.sh"
	exit 1
fi

# Uses the default functionality from common/backup-server-functions.sh

main "$@"
