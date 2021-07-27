#!/bin/bash

# Import common functions
source ${HOME}/my.scripts/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 1
fi
source ${HOME}/my.scripts/.cred/.cred

# Global variables
# LOG_PROCESS_TO_FILE=true

mainProcess() {
  curl --location --request GET 'localhost:9090/kame-house/api/v1/admin/power-management/suspend' \
    --header "Content-Type: application/json" \
    --header "Authorization: Basic ${KH_ADMIN_API_BASIC_AUTH}"
  echo ""
  echo ""
  read -p "Press enter to exit"
}

main "$@"
