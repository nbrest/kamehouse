#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 1
fi
source ${HOME}/.kamehouse/.shell/.cred

# Global variables
# LOG_PROCESS_TO_FILE=true
MEDIA_SERVER=niko-server

mainProcess() {
  curl --location --request POST "${MEDIA_SERVER}/kame-house-admin/api/v1/admin/screen/unlock" \
    --header "Content-Type: application/json" \
    --header "Authorization: Basic ${KH_ADMIN_API_BASIC_AUTH}"
  echo ""
  echo ""
}

main "$@"
