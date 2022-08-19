#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 1
fi
# Import kamehouse functions
source ${HOME}/programs/kamehouse-shell/bin/common/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing kamehouse-functions.sh\033[0;39m"
  exit 1
fi

LOG_PROCESS_TO_FILE=true
KAMEHOUSE_MOBILE_APP_PATH="/var/www/kamehouse-webserver/kame-house-mobile"
KAMEHOUSE_APK_HTML=kamehouse-apk.html
GIT_COMMIT_HASH=""
HEAD='
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width">
<meta name="author" content="nbrest">
<meta name="description" content="kame-house application">
<meta name="keywords" content="kame-house nicobrest nbrest">
<meta name="mobile-web-app-capable" content="yes">

<title>KameHouse APK</title>

<link rel="shortcut icon" href="/kame-house-groot/favicon.ico" type="image/x-icon" />
<script src="/kame-house/lib/js/jquery.js"></script>
<script src="/kame-house/js/global.js"></script>
<link rel="stylesheet" href="/kame-house/lib/css/bootstrap.min.css" />
<link rel="stylesheet" href="/kame-house/css/global.css" />
'
PRE_STYLE='style="color: #c0c0c0;font-size: 17px;margin: 30px; border:3px solid #2a2a2a; padding: 20px; background: #000000"'

mainProcess() {
  log.info "Re generating apk html file"
  cd ${KAMEHOUSE_MOBILE_APP_PATH}

  echo "<html><head>${HEAD}</head><body>" > ${KAMEHOUSE_APK_HTML}
  
  echo '<div class="default-layout main-body"><br><br>' >> ${KAMEHOUSE_APK_HTML}
  
  echo "<h2>Mobile APK Status</h2><br>" >> ${KAMEHOUSE_APK_HTML}
  echo "<pre ${PRE_STYLE}>" >> ${KAMEHOUSE_APK_HTML}
  echo -n 'sha256sum: ' >> ${KAMEHOUSE_APK_HTML}
  sha256sum kamehouse.apk >> ${KAMEHOUSE_APK_HTML}

  ls -ln | grep -v ".html" | cut -d ' ' -f 5- >> ${KAMEHOUSE_APK_HTML}

  echo "" >> ${KAMEHOUSE_APK_HTML}
  echo 'git commit hash: '${GIT_COMMIT_HASH} >> ${KAMEHOUSE_APK_HTML}
  echo "</pre>" >> ${KAMEHOUSE_APK_HTML}

  echo '</div>' >> ${KAMEHOUSE_APK_HTML}

  echo "</body></html>" >> ${KAMEHOUSE_APK_HTML}
}

parseArguments() {
  while getopts ":c:" OPT; do
    case $OPT in
    ("c")
      GIT_COMMIT_HASH=$OPTARG
      ;;
    (\?)
      parseInvalidArgument "$OPTARG"
      ;;
    esac
  done

  if [ -z "${GIT_COMMIT_HASH}" ]; then
    log.error "git commit hash not passed with argument -c"
    exitProcess 1
  fi
}

printHelpOptions() {
  addHelpOption "-c hash" "git commit hash"
}

main "$@"
