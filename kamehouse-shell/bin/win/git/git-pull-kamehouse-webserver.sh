#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/common-functions.sh
if [ "$?" != "0" ]; then
	echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
	exit 99
fi

source ${HOME}/programs/kamehouse-shell/bin/common/git/git-pull-functions.sh
if [ "$?" != "0" ]; then
	echo "$(date +%Y-%m-%d' '%H:%M:%S) - [ERROR] - An error occurred importing git-pull-functions.sh"
	exit 99
fi

GIT_PROJECT_DIR=${HOME}/programs/apache-httpd/www/kamehouse-webserver

main "$@"
