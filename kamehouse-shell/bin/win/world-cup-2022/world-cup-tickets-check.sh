#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/common-functions.sh
if [ "$?" != "0" ]; then echo "`date +%Y-%m-%d' '%H:%M:%S` - [ERROR] - Error importing common-functions.sh" ; exit 99 ; fi

source ${HOME}/my.scripts/.cred/.gmail
source ${HOME}/my.scripts/.cred/.telegram

initScriptEnv() {
  VERBOSE=false
  DATA_DIR=${HOME}/my.scripts/data/world-cup-tickets-check
  WWW_WORLD_CUP_BOOKINGS_PATH=${HOME}/programs/apache-httpd/www/kamehouse-webserver/world-cup-bookings
  RESALE_HOME_DATA_PAGE="${WWW_WORLD_CUP_BOOKINGS_PATH}/fifa-resale-home-data.html"
  SALE_HOME_DATA_PAGE="${WWW_WORLD_CUP_BOOKINGS_PATH}/fifa-sale-home-data.html"
}

mainProcess() {
  setupDataDir
  runNodeApp
  processDataFiles
  cleanUpFiles
}

setupDataDir() {
  log.info "Setting up data dir"
  mkdir -p ${DATA_DIR}
  rm ${DATA_DIR}/*.data
}

runNodeApp() {
  log.info "Running node app"
  cd ${HOME}/my.scripts/node-apps/world-cup-tickets-check
  npm start LOG=${LOG} verbose=${VERBOSE}
}

processDataFiles() {
  log.info "Processing node output data"
  local DATA_FILES=`ls -1 ${DATA_DIR}/*.data`;
  while read DATA_FILE; do
    processDataFile "${DATA_FILE}"
  done <<< ${DATA_FILES}
}

processDataFile() {
  local DATA_FILE=$1
  if [ -z "${DATA_FILE}" ]; then
    log.debug "Received empty DATA_FILE variable. Something went wrong"
    return
  fi
  log.debug "Processing file ${COL_PURPLE}${DATA_FILE}"
  source ${DATA_FILE}
  if [ -n "${MATCH_NAME}" ]; then
    initData
    processSale "${DATA_FILE}"
    processResale "${DATA_FILE}"
    processSaleHome "${DATA_FILE}"
    processResaleHome "${DATA_FILE}"
  else
    log.warn "Match file data didn't contain any info while processing file ${COL_PURPLE}${DATA_FILE}"
  fi
}

initData() {
  initMatchPageData
  initHomePageData
}

initHomePageData() {
  if [ -z "${SALE_HOME_TICKETS_AVAILABLE}" ]; then
    SALE_HOME_TICKETS_AVAILABLE=false
  fi
  if [ -z "${SEND_SALE_HOME_EMAIL}" ]; then
    SEND_SALE_HOME_EMAIL=false
  fi

  if [ -z "${RESALE_HOME_TICKETS_AVAILABLE}" ]; then
    RESALE_HOME_TICKETS_AVAILABLE=false
  fi
  if [ -z "${SEND_RESALE_HOME_EMAIL}" ]; then
    SEND_RESALE_HOME_EMAIL=false
  fi
}

processSaleHome() {
  local DATA_FILE=$1
  processHomePage "${DATA_FILE}" "SALE" "${SALE_HOME_TICKETS_AVAILABLE}" "${SEND_SALE_HOME_EMAIL}" "${SALE_HOME_MATCH_URL}"
}

processResaleHome() {
  local DATA_FILE=$1
  processHomePage "${DATA_FILE}" "RESALE" "${RESALE_HOME_TICKETS_AVAILABLE}" "${SEND_RESALE_HOME_EMAIL}" "${RESALE_HOME_MATCH_URL}"
}

processHomePage() {
  local DATA_FILE=$1
  local SALE_TYPE=$2
  local MATCH_TICKETS_AVAILABLE=$3
  local MATCH_SEND_HOME_EMAIL=$4
  local CURRENT_MATCH_URL=$5
  if ${MATCH_TICKETS_AVAILABLE}; then
    if ${MATCH_SEND_HOME_EMAIL}; then
      log.info "${COL_RED}${MATCH_NAME}: ${SALE_TYPE}: home page: official ${SALE_TYPE} tickets MIGHT be available! Try to BOOK NOW! Sending email"
      sendHomePageEmail "${DATA_FILE}" "${SALE_TYPE}" "${CURRENT_MATCH_URL}"
    else
      log.info "${COL_YELLOW}${MATCH_NAME}: ${SALE_TYPE}: home page: official ${SALE_TYPE} tickets MIGHT be availiable! ${COL_GREEN}set to SKIP EMAIL"
    fi
  else
    log.info "${COL_PURPLE}${MATCH_NAME}${COL_DEFAULT_LOG}: ${SALE_TYPE}: home page: no ${COL_PURPLE}${SALE_TYPE}${COL_DEFAULT_LOG} tickets available for this match on ${SALE_TYPE} ${COL_PURPLE}home page"
  fi
}

sendHomePageEmail() {
  local DATA_FILE=$1
  local SALE_TYPE=$2
  local CURRENT_MATCH_URL=$3
  local EMAIL_SUBJECT="${MATCH_NAME}: ${SALE_TYPE}: home page: MIGHT have official ${SALE_TYPE} tickets availiable! BOOK NOW! - WORLD CUP BOOKINGS -"
  sendEmail "${DATA_FILE}" "${CURRENT_MATCH_URL}" "${EMAIL_SUBJECT}"
}

sendEmail() {
  local DATA_FILE=$1 
  local CURRENT_MATCH_URL=$2
  local EMAIL_SUBJECT=$3
  local EMAIL_BODY="BOOK MY TICKET NOW!
  
  ${CURRENT_MATCH_URL}

  "
  EMAIL_BODY=${EMAIL_BODY}`cat ${DATA_FILE} | sed "s#\"##g"`
  #powershell -c "C:\Users\nbrest\my.scripts\win\ps1\send-gmail.ps1 \"${GMAIL_APP_PASS}\" \"${EMAIL_SUBJECT}\" \"${EMAIL_BODY}\""
  sendTelegramBotMessage "${EMAIL_SUBJECT} ${CURRENT_MATCH_URL}"  
}

cleanUpFiles() {
  log.debug "Cleaning up files"
  echo "" > ${RESALE_HOME_DATA_PAGE} 
  echo "" > ${SALE_HOME_DATA_PAGE} 
}

initMatchPageData() {
  if [ -z "${TICKETS_AVAILABLE}" ]; then
    TICKETS_AVAILABLE=false
  fi
  if [ -z "${SEND_EMAIL}" ]; then
    SEND_EMAIL=false
  fi
  
  if [ -z "${RESALE_TICKETS_AVAILABLE}" ]; then
    RESALE_TICKETS_AVAILABLE=false
  fi
  if [ -z "${SEND_RESALE_EMAIL}" ]; then
    SEND_RESALE_EMAIL=false
  fi
}

processSale() {
  local DATA_FILE=$1
  local AVAILABLE_CATEGORIES="{ 'Category 3':'${CATEGORY_3_AVAILABLE}', 'Category 2':'${CATEGORY_2_AVAILABLE}', 'Category 1':'${CATEGORY_1_AVAILABLE}' }"
  processMatchPage "${DATA_FILE}" "SALE" "${TICKETS_AVAILABLE}" "${SEND_EMAIL}" "${MATCH_URL}" "${AVAILABLE_CATEGORIES}"
}

processResale() {
  local DATA_FILE=$1
  local AVAILABLE_CATEGORIES="{ 'Category 3':'${RESALE_CATEGORY_3_AVAILABLE}', 'Category 2':'${RESALE_CATEGORY_2_AVAILABLE}', 'Category 1':'${RESALE_CATEGORY_1_AVAILABLE}' }"
  processMatchPage "${DATA_FILE}" "RESALE" "${RESALE_TICKETS_AVAILABLE}" "${SEND_RESALE_EMAIL}" "${MATCH_RESALE_URL}" "${AVAILABLE_CATEGORIES}"
}

processMatchPage() {
  local DATA_FILE=$1
  local SALE_TYPE=$2
  local MATCH_TICKETS_AVAILABLE=$3
  local MATCH_SEND_EMAIL=$4
  local CURRENT_MATCH_URL=$5
  local AVAILABLE_CATEGORIES=$6
  if ${MATCH_TICKETS_AVAILABLE}; then
    if ${MATCH_SEND_EMAIL}; then
      log.info "${COL_RED}${MATCH_NAME}: ${SALE_TYPE}: match page: has official ${SALE_TYPE} tickets available! BOOK NOW! Sending email ${COL_PURPLE}${AVAILABLE_CATEGORIES}"
      sendMatchPageEmail "${DATA_FILE}" "${SALE_TYPE}" "${CURRENT_MATCH_URL}"
    else
      log.info "${COL_YELLOW}${MATCH_NAME}: ${SALE_TYPE}: match page: has official ${SALE_TYPE} tickets availiable! ${COL_GREEN}set to SKIP EMAIL ${COL_PURPLE}${AVAILABLE_CATEGORIES}"
    fi
  else
    log.info "${COL_PURPLE}${MATCH_NAME}${COL_DEFAULT_LOG}: ${SALE_TYPE}: match page: no ${COL_PURPLE}${SALE_TYPE}${COL_DEFAULT_LOG} tickets available for this match on the ${COL_PURPLE}match page"
  fi
}

sendMatchPageEmail() {
  local DATA_FILE=$1
  local SALE_TYPE=$2
  local CURRENT_MATCH_URL=$3
  local EMAIL_SUBJECT="${MATCH_NAME}: ${SALE_TYPE}: match page: ${SALE_TYPE} has official tickets availiable! BOOK NOW! - WORLD CUP BOOKINGS -"
  sendEmail "${DATA_FILE}" "${CURRENT_MATCH_URL}" "${EMAIL_SUBJECT}"
}

# To send telegram messages:
# Create a telegram bot and get its http token
# Add the bot to a group chat and get the group chat id 
# Send messages to the group chat with the bot using the following code
# curl --location --request POST "https://api.telegram.org/bot${TOKEN}/getUpdates : get chat id from there, send a message in the chat if there's no updates
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

parseArguments() {
  local OPTIONS=("$@")
  for i in "${!OPTIONS[@]}"; do
    local CURRENT_OPTION="${OPTIONS[i]}"
    if [ "${CURRENT_OPTION:0:1}" != "-" ]; then
      continue
    fi
    local CURRENT_OPTION_ARG="${OPTIONS[i+1]}"
    case "${CURRENT_OPTION}" in
      -v)
        VERBOSE=true
        LOG=trace
        ;;
      -?|-??*)
        parseInvalidArgument "${CURRENT_OPTION}"
        ;;        
    esac
  done    
}

printHelpOptions() {
  addHelpOption "-v" "enable verbose logging"
}

main "$@"
