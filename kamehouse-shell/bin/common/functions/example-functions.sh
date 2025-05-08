#######################################################################
# This file should be imported through common-functions, not directly #
#######################################################################
# Example functions to show in base-script and for reference.
# Prefix every function in this file with example[Content]. Ex. exampleArrays() {}

# Call all other example functions from here.
exampleFunctions() {
  exampleIf
  exampleRegex
  #exampleRequestConfirmation
  exampleLog
  exampleArrays
  exampleCountdown
  exampleWhileStringCheck
  exampleIteratePids
  exampleIterateFiles
  exampleBackgroundForegroundCalls
}

exampleIf() {
  log.info "${COL_RED}exampleIf"
  if ${IS_LINUX_HOST}; then
    log.info "In linux host"
  else
    log.info "Not in linux host"
  fi

  local IS_TRUE_DO_NOTHING=true
  if ${IS_TRUE_DO_NOTHING}; then
    : # Do nothing
  else
    log.info "IS_TRUE_DO_NOTHING false"
  fi

  POM_MODIFIED_STATUS="1"
  if [ "${POM_MODIFIED_STATUS}" == "0" ]; then
    log.info "POM_MODIFIED_STATUS is 0"
  fi
  if [ "${POM_MODIFIED_STATUS}" != "0" ]; then
    log.info "POM_MODIFIED_STATUS is different to 0"
  fi

  local IF_WITH_OR=1
  if [[ "${IF_WITH_OR}" == "3" || "${IF_WITH_OR}" == "1" ]]; then
    log.info "IF_WITH_OR is 3 or 1"
  else 
    log.info "IF_WITH_OR is different from 3 or 1"
  fi
}

exampleRegex() {
  log.info "${COL_RED}exampleRegex"
  local RELEASE_VERSION="v1.03"
  local RELEASE_VERSION_RX=^v[0-9]\.[0-9]{2}$
  if [[ "${RELEASE_VERSION}" =~ ${RELEASE_VERSION_RX} ]]; then
    log.info "${RELEASE_VERSION} matches ${RELEASE_VERSION_RX}"
  else
    log.info "${RELEASE_VERSION} doesn't match ${RELEASE_VERSION_RX}"
  fi   

  local RELEASE_VERSION_2="v1.04a"
  if [[ "${RELEASE_VERSION_2}" =~ ${RELEASE_VERSION_RX} ]]; then
    log.info "${RELEASE_VERSION_2} matches ${RELEASE_VERSION_RX}"
  else
    log.info "${RELEASE_VERSION_2} doesn't match ${RELEASE_VERSION_RX}"
  fi   
}

exampleRequestConfirmation() {
  log.info "${COL_RED}exampleRequestConfirmation"
  local REQUEST_CONFIRMATION_RX=^yes|y$
  log.info "Do you want to proceed? (${COL_BLUE}Yes${COL_DEFAULT_LOG}/${COL_RED}No${COL_DEFAULT_LOG}): "
  read SHOULD_PROCEED
  SHOULD_PROCEED=`echo "${SHOULD_PROCEED}" | tr '[:upper:]' '[:lower:]'`
  if [[ "$SHOULD_PROCEED" =~ ${REQUEST_CONFIRMATION_RX} ]]
  then
    log.info "${SHOULD_PROCEED} : yes, proceed"
  else
    log.info "${SHOULD_PROCEED} : no, don't proceed"
  fi
}

exampleLog() {
  log.info "${COL_RED}exampleLog"
  log.info "Run script with \`LOG=DEBUG script-name.sh\` or \`log=debug script-name.sh\` to see al debug logs"
  log.info "Run script with \`LOG=TRACE script-name.sh\` or \`log=trace script-name.sh\` to see al trace logs"
  log.info "${COL_RED}Use ROOT_PREFIX var to prefix all absolute paths, so they work in any bash implementation"
  log.info "Current value for ROOT_PREFIX: ${ROOT_PREFIX}"
  log.trace "TRACE gohan"
  log.debug "DEBUG goku"
  log.info "INFO Mada mada dane"
  log.warn "WARN Pegasus ryu sei ken!"
  log.error "ERROR Shimatta!"
}

exampleArrays() {
  log.info "${COL_RED}exampleArrays"
  log.info "String arrays example"
  stringArray=("goku.log" "gohan.log" "goten.log" "vegeta.log")
  for CURRENT_STRING in ${stringArray[@]}; do
    log.info "CURRENT_STRING: ${CURRENT_STRING}"
  done

  log.info "Number arrays example"
  numberArray=(4 99 12582 15127421)
  for INDEX in ${!numberArray[@]}; do
    # Can also loop through the array using the index
    log.info "INDEX: ${INDEX}, VALUE: ${numberArray[$INDEX]}"
  done

  log.info "Command arrays example"
  cmdArray=(
    ls
    # ps - can also comment lines and are skipped in the array
    ps
  )
  for INDEX in ${!cmdArray[@]}; do
    log.info "INDEX: ${INDEX}, VALUE: ${cmdArray[$INDEX]}"
    ${cmdArray[$INDEX]} -l
  done
}

exampleCountdown() {
  log.info "${COL_RED}exampleCountdown"
  NUMBER_OF_SECONDS=5
  secs=$((NUMBER_OF_SECONDS))
  log.info "Countdown total: ${COL_PURPLE}${NUMBER_OF_SECONDS}${COL_DEFAULT_LOG} seconds" 
  while [ $secs -gt 0 ]; do
    echo -ne "`log.info "${COL_NORMAL}Counting down ${COL_RED}${secs}${COL_NORMAL} seconds"`\033[0K\r"
    sleep 1
    : $((secs--))
  done
  log.info "${COL_NORMAL}Counting down ${COL_RED}${secs}${COL_NORMAL} seconds"
}

exampleWhileStringCheck() {
  log.info "${COL_RED}exampleWhileStringCheck"
  local LOOP_CHECK="CONTINUE"
  local let ITERATIONS=5
  local let iter=$((ITERATIONS))
  while [ "${LOOP_CHECK}" != "STOP" ]; do
    sleep 1
    : $((iter--))
    if [[ "${iter}" -le 0 ]]; then
      LOOP_CHECK="STOP"
    fi
    echo "LOOP_CHECK ${LOOP_CHECK}"
  done
}

exampleIteratePids() {
  log.info "${COL_RED}exampleIteratePids"
  local PIDS=`ps | grep -v PID | grep -v pid | awk '{print $1}'`
  echo -e "${PIDS}" | while read PID; do
    log.info "PID from PIDS list: ${PID}"
  done
}

exampleIterateFiles() {
  log.info "${COL_RED}exampleIterateFiles"
  local FILES=`ls -1`
  echo -e "${FILES}" | while read FILE; do
    log.info "FILE from FILES list: ${FILE}"
  done

  local DIRECTORIES=`ls -d */`
  echo -e "${DIRECTORIES}" | while read DIRECTORY; do
    log.info "DIRECTORY from DIRECTORIES list: ${DIRECTORY}"
  done
}

exampleBackgroundForegroundCalls() {
  log.info "${COL_RED}exampleBackgroundForegroundCalls"
  # Enable fg job control: set -m
  set -m 
  exampleBackgroundFunction "Job-1" "6" &
  exampleBackgroundFunction "Job-2" "3" &
  exampleBackgroundFunction "Job-3" "6" &
  
  # List running jobs
  log.info "listing current backgrounds tasks"
  jobs -l

  # Get jobspec of job I want to bring to foreground later
  JOB_TO_FG="Job-2"
  JOB_PID=`jobs -l | grep "exampleBackgroundFunction" | grep "${JOB_TO_FG}" | awk '{print $2}'` 
  JOB_SPEC=`jobs -l | grep "exampleBackgroundFunction" | grep "${JOB_TO_FG}" | awk '{print $1}'`
  log.info "exampleBackgroundFunction 2 : JOB_PID = ${JOB_PID} JOB_SPEC = ${JOB_SPEC}"
  local LAST_CHAR="${JOB_SPEC: -1}"
  if [[ "${LAST_CHAR}" == "-" || "${LAST_CHAR}" == "+" ]]; then
    JOB_SPEC=${JOB_SPEC: : -1}
  fi
  JOB_SPEC=${JOB_SPEC: : -1}
  JOB_SPEC=${JOB_SPEC:1}
  
  # Bring job to foreground
  log.info "Bringing job '"${JOB_SPEC}"' to foreground"
  fg %${JOB_SPEC}
  log.info "Control back to exampleBackgroundForegroundCalls"
  
  # List running jobs
  log.info "listing current backgrounds tasks"
  jobs -l
}

exampleBackgroundFunction() {
  local JOB_ID="$1"
  local let NUMBER_OF_SECONDS=$2
  echo "${JOB_ID} :: Started exampleBackgroundFunction ${JOB_ID} -- total seconds ${NUMBER_OF_SECONDS} || "
  local let secs=$((NUMBER_OF_SECONDS))
  while [ $secs -gt 0 ]; do
    echo "${JOB_ID} :: exampleBackgroundFunction ${JOB_ID} -- current seconds ${secs} || "
    sleep 1
    : $((secs--))
  done
  echo "${JOB_ID} :: [-----FINISHED-----] exampleBackgroundFunction ${JOB_ID} -- total seconds ${NUMBER_OF_SECONDS} || "
}
