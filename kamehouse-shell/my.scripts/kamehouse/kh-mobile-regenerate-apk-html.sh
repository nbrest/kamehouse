#!/bin/bash

# Import common functions
source ${HOME}/my.scripts/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 1
fi

LOG_PROCESS_TO_FILE=true
KAMEHOUSE_MOBILE_APP_PATH="/var/www/kh.webserver/kame-house-mobile"
KAMEHOUSE_APK_HTML=kamehouse-apk.html
GIT_COMMIT_HASH="a1b2c3d4"
STYLE="style=\"background: #060606;color: #c0c0c0;font-size: 20px;margin: 50px;\""

mainProcess() {
  log.info "Re generating apk html file"
  GIT_COMMIT_HASH=$1

  cd ${KAMEHOUSE_MOBILE_APP_PATH}

  echo "<html><head></head><body ${STYLE}>" > ${KAMEHOUSE_APK_HTML}

  echo "<h2>KameHouse Mobile APK:</h2>" >> ${KAMEHOUSE_APK_HTML}
  echo "<pre>" >> ${KAMEHOUSE_APK_HTML}
  echo -n 'sha256sum: ' >> ${KAMEHOUSE_APK_HTML}
  sha256sum kamehouse.apk >> ${KAMEHOUSE_APK_HTML}

  ls -ln | cut -d ' ' -f 5- >> ${KAMEHOUSE_APK_HTML}

  echo "" >> ${KAMEHOUSE_APK_HTML}
  echo 'git commit hash: '${GIT_COMMIT_HASH} >> ${KAMEHOUSE_APK_HTML}
  echo "</pre>" >> ${KAMEHOUSE_APK_HTML}

  echo "</body></html>" >> ${KAMEHOUSE_APK_HTML}
}

main "$@"
