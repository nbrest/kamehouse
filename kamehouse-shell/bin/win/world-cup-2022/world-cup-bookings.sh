#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/common-functions.sh
if [ "$?" != "0" ]; then echo "`date +%Y-%m-%d' '%H:%M:%S` - [ERROR] - Error importing common-functions.sh" ; exit 99 ; fi

source ${HOME}/my.scripts/.cred/.gmail
source ${HOME}/my.scripts/.cred/.telegram

initScriptEnv() {
  SCREEN_CAPTURE_EXE="C:\Users\nbrest\my.scripts\win\bat\screen-capture.exe"
  TESSERACT_EXE="C:\Program Files\Tesseract-OCR\tesseract.exe"
  TESSERACT_USER_WORDS="C:\Users\nbrest\my.scripts\conf\world-cup-bookings\eng.user-words"
  WWW_WORLD_CUP_BOOKINGS_PATH=${HOME}/programs/apache-httpd/www/kamehouse-webserver/world-cup-bookings
  WWW_WORLD_CUP_BOOKINGS_PATH_WIN="C:\Users\nbrest\programs\apache-httpd\www\kamehouse-webserver\world-cup-bookings\\"
  IMG_WORLD_CUP_BOOKINGS_PATH=${WWW_WORLD_CUP_BOOKINGS_PATH}/img
  IMG_WORLD_CUP_BOOKINGS_PATH_WIN="${WWW_WORLD_CUP_BOOKINGS_PATH_WIN}img\\"
  TXT_WORLD_CUP_BOOKINGS_PATH=${WWW_WORLD_CUP_BOOKINGS_PATH}/txt
  TXT_WORLD_CUP_BOOKINGS_PATH_WIN="${WWW_WORLD_CUP_BOOKINGS_PATH_WIN}txt\\"
  HTML_WORLD_CUP_BOOKINGS_PATH=${WWW_WORLD_CUP_BOOKINGS_PATH}/html-snippets
  SEND_SUCCESS_EMAIL=false
  SEND_ERROR_EMAIL=false
  SEND_HEALTH_CHECK_EMAIL=true
  ARGENTINA_MATCHES=""
  ALL_MATCHES=""
  KEEP_ALIVE_TIMESTAMP=""
  SHOPPING_CART_STATUS=""
  let NUM_OF_SCREENSHOTS=45
  let HEALTH_CHECK_ITERATIONS_COUNT=500
  let HEALTH_CHECK_CURRENT_ITERATION=$((HEALTH_CHECK_ITERATIONS_COUNT))
  let MAX_ERROR_COUNT=8
  let CURRENT_ERROR_COUNT=0
  SLEEP_LOOP_TIME=60
  OFFICIAL_TICKETS_STATUS=""
  OFFICIAL_TICKETS_DATA_DIR=${HOME}/my.scripts/data/world-cup-tickets-check
  DATA_DIR=${HOME}/my.scripts/data/world-cup-tickets-check
}

mainProcess() {
  log.info "World Cup Bookings background script"
  setupDirs

  while [ "to-infinity" != "and-beyond" ]; do
    officialTicketsLoop
    sleepLoop
  done
}

officialTicketsLoop() {
  checkOfficialTickets
  setHealthCheckIterationCount
  sendEmailNotification
}

# DISABLED: I already got my Argentina vs Saudi Arabia Hospitality ticket :)
hospitalityAndOfficialTicketsLoop() {
  checkOfficialTickets
  takeSnapshots
  checkOfficialTickets
  convertSnapshotsToText
  checkOfficialTickets
  scanText
  updateHtml
  setScanResults
  setHealthCheckIterationCount
  sendEmailNotification
  
  checkOfficialTickets
}

setupDirs() {
  mkdir -p ${IMG_WORLD_CUP_BOOKINGS_PATH}
  mkdir -p ${TXT_WORLD_CUP_BOOKINGS_PATH}
  mkdir -p ${HTML_WORLD_CUP_BOOKINGS_PATH}
  rm -f ${IMG_WORLD_CUP_BOOKINGS_PATH}/*.png
  rm -f ${TXT_WORLD_CUP_BOOKINGS_PATH}/*.txt
}

takeSnapshots() {
  log.info "Capturing screen"
  local let CURRENT_SCREENSHOT=1
  while [[ "${CURRENT_SCREENSHOT}" -le "${NUM_OF_SCREENSHOTS}" ]]; do
    takeSnapshot "${CURRENT_SCREENSHOT}"
    sleep 1
    : $((CURRENT_SCREENSHOT++))
  done
}

takeSnapshot() {
  local NUMBER=$1
  "${SCREEN_CAPTURE_EXE}" "${IMG_WORLD_CUP_BOOKINGS_PATH_WIN}screen-capture-${NUMBER}.png"
}

convertSnapshotsToText() {
  log.info "Converting snapshots to text files"
  rm -f ${TXT_WORLD_CUP_BOOKINGS_PATH}/*.txt
  echo "" > ${TXT_WORLD_CUP_BOOKINGS_PATH}/dummy.txt

  find ${IMG_WORLD_CUP_BOOKINGS_PATH}/*.png | grep -v "*.png" | while read FILE; do
    local FILE_NAME=${FILE#${IMG_WORLD_CUP_BOOKINGS_PATH}/} 
    log.info "Converting snapshot ${FILE_NAME} to text"
    "${TESSERACT_EXE}" -l eng --psm 11 --dpi 75 --user-words "${TESSERACT_USER_WORDS}" "${IMG_WORLD_CUP_BOOKINGS_PATH_WIN}${FILE_NAME}" "${TXT_WORLD_CUP_BOOKINGS_PATH_WIN}${FILE_NAME}"
  done
}

scanText() {
  log.info "Scanning text files from snapshots"
  
  ALL_MATCHES=`cat ${TXT_WORLD_CUP_BOOKINGS_PATH}/*.txt | sort | grep " VS \| vs " | grep "M-"`
  ALL_MATCHES=`sanitizeContent "${ALL_MATCHES}" "M-"`
  log.info "All matches output:"
  echo "${ALL_MATCHES}"

  KEEP_ALIVE_TIMESTAMP=`cat ${TXT_WORLD_CUP_BOOKINGS_PATH}/*.txt | sort | grep " VS \| vs " | grep "M-9\|Niupi"`
  KEEP_ALIVE_TIMESTAMP=`sanitizeContent "${KEEP_ALIVE_TIMESTAMP}" "M-"`
  log.info "Keep alive timestamp:"
  echo "${KEEP_ALIVE_TIMESTAMP}"

  SHOPPING_CART_STATUS=`cat ${TXT_WORLD_CUP_BOOKINGS_PATH}/*.txt | sort | grep "Your cart will expire in"`
  SHOPPING_CART_STATUS=`sanitizeContent "${SHOPPING_CART_STATUS}" "Your cart will expire in"`
  log.info "Shopping cart status:"
  echo "${SHOPPING_CART_STATUS}"

  ARGENTINA_MATCHES=`cat ${TXT_WORLD_CUP_BOOKINGS_PATH}/*.txt | sort | grep " VS \| vs " | grep "M-" | grep "Argentina\|argentina"`
  ARGENTINA_MATCHES=`sanitizeContent "${ARGENTINA_MATCHES}" "M-"`
  log.info "Argentina matches output:"
  echo "${ARGENTINA_MATCHES}"
}

sanitizeContent() { 
  local INPUT_CONTENT=$1
  local PREFIX=$2
  local NL=$'\n'
  local TEMP_OUTPUT=""
  if [ "${INPUT_CONTENT}" == "" ]; then
    echo ""
  else
    while read INPUT_LINE; do
      TEMP_OUTPUT="${TEMP_OUTPUT}"`echo ${INPUT_LINE} | sed "s/.*${PREFIX}/${PREFIX}/" | sed "s#\"##g"`
      TEMP_OUTPUT="${TEMP_OUTPUT}${NL}"
    done <<< ${INPUT_CONTENT}
    TEMP_OUTPUT=`echo "${TEMP_OUTPUT}" | sort`
    echo "${TEMP_OUTPUT}"
  fi
}

updateHtml() {
  log.info "Update generated html files"
  local ARGENTINA_MATCHES_PREFIX=$(date +%Y-%m-%d' '%H:%M:%S)" - Argentina matches:
"
  echo "<pre>${ARGENTINA_MATCHES_PREFIX}${ARGENTINA_MATCHES}</pre>" > "${HTML_WORLD_CUP_BOOKINGS_PATH}/argentina-matches.html"

  local KEEP_ALIVE_TIMESTAMP_PREFIX=$(date +%Y-%m-%d' '%H:%M:%S)" - Keep alive timestamp:
"
  echo "<pre>${KEEP_ALIVE_TIMESTAMP_PREFIX}${KEEP_ALIVE_TIMESTAMP}</pre>" > "${HTML_WORLD_CUP_BOOKINGS_PATH}/keep-alive-timestamp.html"

  local SHOPPING_CART_STATUS_PREFIX=$(date +%Y-%m-%d' '%H:%M:%S)" - Shopping cart status:
"
  echo "<pre>${SHOPPING_CART_STATUS_PREFIX}${SHOPPING_CART_STATUS}</pre>" > "${HTML_WORLD_CUP_BOOKINGS_PATH}/shopping-cart-status.html"  

  local ALL_MATCHES_PREFIX=$(date +%Y-%m-%d' '%H:%M:%S)" - All matches:
"
  echo "<pre>${ALL_MATCHES_PREFIX}${ALL_MATCHES}</pre>" > "${HTML_WORLD_CUP_BOOKINGS_PATH}/all-matches.html"

  OFFICIAL_TICKETS_STATUS=`cat ${OFFICIAL_TICKETS_DATA_DIR}/*.data | sed "s#\"##g"`
  local OFFICIAL_TICKETS_STATUS_PREFIX=$(date +%Y-%m-%d' '%H:%M:%S)" - Official tickets status:
"
  echo "<pre>${OFFICIAL_TICKETS_STATUS_PREFIX}${OFFICIAL_TICKETS_STATUS}</pre>" > "${HTML_WORLD_CUP_BOOKINGS_PATH}/official-tickets-status.html"
}

setScanResults() {
  log.info "Setting results from text scans"
  if [ "${ARGENTINA_MATCHES}" != "" ]; then
    SEND_SUCCESS_EMAIL=true
  else
    log.info "${COL_YELLOW}No Argentina hospitality package matches :( Shimatta!"
  fi

  if [ "${KEEP_ALIVE_TIMESTAMP}" == "" ]; then
    log.warn "Keep alive text not found on this run"
    incrementErrorCount
  else
    # Compare timestamps in epoch format
    local CURRENT_TIMESTAMP=$(date +%s)
    # It seems git bash grep doesn't like the grouping [0-9]{2} in the regex, so I need to add them separately [0-9][0-9]
    local LATEST_TIMESTAMP_RX="^M-97 | Niupi VS Furano - [0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9] [0-9][0-9]-[0-9][0-9]-[0-9][0-9] .*"
    local LATEST_RUN_TIMESTAMP=`echo -e "${KEEP_ALIVE_TIMESTAMP}" | sort | grep -e "${LATEST_TIMESTAMP_RX}" | tail -n 1`
    log.trace "LATEST_RUN_TIMESTAMP: '${LATEST_RUN_TIMESTAMP}'"
    if [ -z "${LATEST_RUN_TIMESTAMP}" ]; then
      log.warn "Latest timestamp not found in the current run"
      incrementErrorCount
    else
      log.debug "Keep alive text found on this run. Resetting CURRENT_ERROR_COUNT"
      CURRENT_ERROR_COUNT=0
      LATEST_RUN_TIMESTAMP=`sanitizeContent "${LATEST_RUN_TIMESTAMP}" "Furano - "`
      # Tesseract sometimes messes up numbers, so using 0-00 for last digit of minutes and seconds to reduce the error rate of invalid dates
      LATEST_RUN_TIMESTAMP="${LATEST_RUN_TIMESTAMP:10:19}"
      LATEST_RUN_TIMESTAMP=`echo $LATEST_RUN_TIMESTAMP | sed s/./:/14`
      LATEST_RUN_TIMESTAMP=`echo $LATEST_RUN_TIMESTAMP | sed s/./:/17`
      LATEST_RUN_TIMESTAMP_ORIG="${LATEST_RUN_TIMESTAMP}"
      LATEST_RUN_TIMESTAMP="${LATEST_RUN_TIMESTAMP:0:15}0:00"
      LATEST_RUN_TIMESTAMP=`date -d "${LATEST_RUN_TIMESTAMP}" +"%s"`
      log.info "Latest run timestamp: ${COL_PURPLE}${LATEST_RUN_TIMESTAMP_ORIG}"
      log.debug "Latest run timestamp epoch: ${COL_PURPLE}${LATEST_RUN_TIMESTAMP}"
      log.debug "Current timestamp epoch:    ${COL_PURPLE}${CURRENT_TIMESTAMP}"
      local TIMESTAMP_DIFFERENCE=$((${CURRENT_TIMESTAMP}-${LATEST_RUN_TIMESTAMP}))
      log.debug "Timestamp difference: ${COL_PURPLE}${TIMESTAMP_DIFFERENCE}${COL_DEFAULT_LOG} seconds"
      if [[ "${TIMESTAMP_DIFFERENCE}" -ge "7200" ]]; then
        log.error "The text scans timestamp is older than 2 hours. Something is wrong. Is the iMacro still running??"
        SEND_ERROR_EMAIL=true
      else
        log.info "Text scans timestamp within expected range"
      fi
    fi
  fi
}

incrementErrorCount() {
  : $((CURRENT_ERROR_COUNT++))
  log.warn "Current consecutive errors count: ${CURRENT_ERROR_COUNT}"
  if [[ "${CURRENT_ERROR_COUNT}" -ge "${MAX_ERROR_COUNT}" ]]; then
    log.error "Current error count exceeds maximum. Sending error notification"
    SEND_ERROR_EMAIL=true
    CURRENT_ERROR_COUNT=0
  fi
}

sendEmailNotification() {
  local HTML_EMAIL_BODY=${HTML_EMAIL_BODY}`cat "${HTML_WORLD_CUP_BOOKINGS_PATH}/argentina-matches.html"`"
  
  "
  HTML_EMAIL_BODY=${HTML_EMAIL_BODY}`cat "${HTML_WORLD_CUP_BOOKINGS_PATH}/shopping-cart-status.html"`"
  
  "
  HTML_EMAIL_BODY=${HTML_EMAIL_BODY}`cat "${HTML_WORLD_CUP_BOOKINGS_PATH}/keep-alive-timestamp.html"`"
  
  "
  HTML_EMAIL_BODY=${HTML_EMAIL_BODY}`cat "${HTML_WORLD_CUP_BOOKINGS_PATH}/all-matches.html"`"
  
  "
  HTML_EMAIL_BODY=${HTML_EMAIL_BODY}`cat "${HTML_WORLD_CUP_BOOKINGS_PATH}/official-tickets-status.html"`"
  
  "

  if ${SEND_SUCCESS_EMAIL}; then
    log.info "${COL_CYAN}I GOT THE TICKET IN THE CART!!!!!!!. Sending SUCCESS email notification"
    local EMAIL_SUBJECT="SUCCESS - WORLD CUP BOOKINGS - I HAVE AN ARGENTINA MATCH IN THE CART!!!!!!!!!"
    local EMAIL_BODY="BOOK MY TICKET NOW!!!!!!!!!!!!!!!!!!!!!!
    
    "
    EMAIL_BODY=${EMAIL_BODY}${HTML_EMAIL_BODY}
    powershell -c "C:\Users\nbrest\my.scripts\win\ps1\send-gmail.ps1 \"${GMAIL_APP_PASS}\" \"${EMAIL_SUBJECT}\" \"${EMAIL_BODY}\""   
    SEND_SUCCESS_EMAIL=false
  fi

  if ${SEND_ERROR_EMAIL}; then
    log.error "${COL_RED}Can't find the keep alive text in the snapshots or timestamp is too old. Sending ERROR email notification"
    local EMAIL_SUBJECT="ERROR - WORLD CUP BOOKINGS - Can't find the keep alive text in snapshots or timestamp is too old"
    local EMAIL_BODY="Couldn't find an active the keep alive text in the snapshots or timestamp is too old. 

    It could be a false positive but double check everything is working. 

    If I keep getting this email, something is wrong. If it stops, then everything works
    
    "
    EMAIL_BODY=${EMAIL_BODY}${HTML_EMAIL_BODY}
     powershell -c "C:\Users\nbrest\my.scripts\win\ps1\send-gmail.ps1 \"${GMAIL_APP_PASS}\" \"${EMAIL_SUBJECT}\" \"${EMAIL_BODY}\""   
    SEND_ERROR_EMAIL=false
  fi

  if ${SEND_HEALTH_CHECK_EMAIL}; then
    log.info "Sending periodic health check email notification"
    local EMAIL_SUBJECT="HEALTH CHECK - WORLD CUP BOOKINGS - Periodic health check"
    local EMAIL_BODY="Periodic world cup bookings health check email
    
    "
    #EMAIL_BODY=${EMAIL_BODY}${HTML_EMAIL_BODY}
    local DATA=`cat ${DATA_DIR}/*.data`
    EMAIL_BODY="${EMAIL_BODY}${DATA}"
    powershell -c "C:\Users\nbrest\my.scripts\win\ps1\send-gmail.ps1 \"${GMAIL_APP_PASS}\" \"${EMAIL_SUBJECT}\" \'${EMAIL_BODY}\'"
    SEND_HEALTH_CHECK_EMAIL=false
    sendTelegramBotMessage "${EMAIL_SUBJECT}"
  fi
}

# To send telegram messages:
# Create a telegram bot and get its http token
# Add the bot to a group chat and get the group chat id 
# Send messages to the group chat with the bot using the following code
sendTelegramBotMessage() {
  local MESSAGE="$1"
  log.info "Sending telegram message to nbrest-world-cup-bookings-bot chat"
  local TELEGRAM_REQUEST_BODY='{
    "text": "'${MESSAGE}'",
    "chat_id": "'${TELEGRAM_NBREST_WORLD_CUP_BOT_CHAT_ID}'"
    "disable_web_page_preview": false,
    "disable_notification": false,
    "reply_to_message_id": null
  }'
  local RESPONSE=`curl --request POST \
    --url https://api.telegram.org/bot${TELEGRAM_NBREST_WORLD_CUP_BOT_TOKEN}/sendMessage \
    --header 'Accept: application/json' \
    --header 'Content-Type: application/json' \
    --data "${TELEGRAM_REQUEST_BODY}" 2>/dev/null`
  log.trace "${COL_CYAN}---------- curl response start"
  log.trace "${RESPONSE}" --log-message-only
  log.trace "${COL_CYAN}---------- curl response end"
}

setHealthCheckIterationCount() {
  log.info "Setting up healt check iteration count"
  : $((HEALTH_CHECK_CURRENT_ITERATION--))
  log.info "Current remaining iterations until health check email sent: ${HEALTH_CHECK_CURRENT_ITERATION}"
  if [[ "${HEALTH_CHECK_CURRENT_ITERATION}" -le 0 ]]; then
    SEND_HEALTH_CHECK_EMAIL=true
    HEALTH_CHECK_CURRENT_ITERATION=$((HEALTH_CHECK_ITERATIONS_COUNT))
  fi
}

checkOfficialTickets() {
  ${HOME}/my.scripts/node/world-cup-tickets-check.sh
}

sleepLoop() {
  log.info "World Cup Bookings infinite loop. Sleeping for ${SLEEP_LOOP_TIME} seconds. Press ctrl+C to stop the process"
  sleep ${SLEEP_LOOP_TIME}
}

main "$@"
