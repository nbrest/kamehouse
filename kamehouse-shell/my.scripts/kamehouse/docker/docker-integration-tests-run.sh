#!/bin/bash

# Run this script inside the docker container to execute integration tests

# Import common functions
source ${HOME}/my.scripts/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 1
fi

# Import kamehouse functions
source ${HOME}/my.scripts/common/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing kamehouse-functions.sh\033[0;39m"
  exit 1
fi

PROJECT_DIR=/home/nbrest/git/kamehouse
SUCCESS="SUCCESS EXECUTING INTEGRATION TESTS"
ERROR="ERROR EXECUTING INTEGRATION TESTS"

main() {
  cd ${PROJECT_DIR}
  /home/nbrest/my.scripts/kamehouse/build-kamehouse.sh -p ci -i > /home/nbrest/logs/build-kamehouse.log
  if [ "$?" == "0" ]; then
    echo "${SUCCESS}"
  else
    tail -n 50 /home/nbrest/logs/build-kamehouse.log
    echo "${ERROR}"
  fi
}

main "$@"
