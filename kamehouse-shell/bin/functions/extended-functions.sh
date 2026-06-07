IS_ROOT_USER=false
if (( $EUID == 0 )); then
  IS_ROOT_USER=true
fi

urlencode() {
    # urlencode <string>
    old_lc_collate=$LC_COLLATE
    LC_COLLATE=C
    local length="${#1}"
    for (( i = 0; i < length; i++ )); do
        local c="${1:$i:1}"
        case $c in
            [a-zA-Z0-9.~_-]) printf '%s' "$c" ;;
            *) printf '%%%02X' "'$c" ;;
        esac
    done
    LC_COLLATE=$old_lc_collate
}

urldecode() {
    # urldecode <string>
    local url_encoded="${1//+/ }"
    printf '%b' "${url_encoded//%/\\x}"
}


# Remove bash header message and other unexpected output 
# from variable holding command output (usually from ssh)
removeUnexpectedOutputFromVar() {
  local VAR_TO_PROCESS=$1 
  local VALID_REGEX=$2
  local VALID_OUTPUT=""
  local LINE_COUNT=0
  for LINE_TO_PROCESS in ${VAR_TO_PROCESS}; do
    if [[ ${LINE_TO_PROCESS} =~ ${VALID_REGEX} ]] ; then
      if [[ ${LINE_COUNT} -gt "0" ]] ; then
          # Append line to previous output
          VALID_OUTPUT=${VALID_OUTPUT}$'\n'${LINE_TO_PROCESS}
      else
          # First line
          VALID_OUTPUT=${LINE_TO_PROCESS}
      fi
      LINE_COUNT=$((LINE_COUNT + 1))
    fi
  done
  echo ${VALID_OUTPUT}
}

# Execute the first function and retries if it fails. 
# The second function is a recovery operation to excute between retries
# Example:  executeWithRetry gitPush gitPull
# With: gitPull() { git pull origin dev } ; gitPush() { git push origin dev }
executeWithRetry() {
  local OPERATION=$1
  local RECOVERY_OPERATION=$2
  local RETRY_COUNT=1
  local MAX_RETRIES=5
  local WAIT_TIME=30 # Seconds
  local OPERATION_FAILED=true
  local OPERATION_RESPONSE=1
  while [[ ${RETRY_COUNT} -le ${MAX_RETRIES} && ${OPERATION_FAILED} == true ]]
  do 
    log.info "Executing operation: ${OPERATION}"
    ${OPERATION}
    OPERATION_RESPONSE=$?
    if [ "${OPERATION_RESPONSE}" == "0" ]; then
      OPERATION_FAILED=false
    else
      let RETRY_COUNT=RETRY_COUNT+1
      log.warn "Operation ${OPERATION} failed. Retrying again in ${WAIT_TIME} seconds." 
      if [ -z "${RECOVERY_OPERATION}" ]; then
        log.warn "No recovery operation set"
      else
        log.info "Executing recovery operation ${RECOVERY_OPERATION} before retrying ${OPERATION}"
        ${RECOVERY_OPERATION}
      fi
      sleep ${WAIT_TIME}
    fi
  done
  checkCommandStatus "${OPERATION_RESPONSE}" "An error occurred executing ${OPERATION}"
}

resetLogFile() {
  echo "" > ${PROCESS_LOG_FILE}
  log.info "Reset log file"
}
