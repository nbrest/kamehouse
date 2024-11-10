IS_ROOT_USER=false
if (( $EUID == 0 )); then
  IS_ROOT_USER=true
fi

# Subsystem root prefix for mounted drives. Use this as a prefix to all
# absolute paths I define in the script.
ROOT_PREFIX="/mnt"

# Update this function both in common-functions.sh and path.sh
export IS_LINUX_HOST=false

setIsLinuxHost() {
  export IS_LINUX_HOST=false
  local UNAME_S=`uname -s`
  local UNAME_R=`uname -r`
  if [ "${UNAME_S}" != "Linux" ]; then
    # Using Git Bash
    export IS_LINUX_HOST=false
  else 
    if [[ ${UNAME_R} == *"Microsoft"* ]]; then
      # Using Ubuntu for Windows 10 (deprecated. don't use that anymore, use an ubuntu vm)
      export IS_LINUX_HOST=false
    else
      # Using Linux
      export IS_LINUX_HOST=true
    fi
  fi
}
setIsLinuxHost

# Check if I'm using Ubuntu for windows, Git Bash or any other bash implementation.
# Default is Ubuntu for windows. Set root prefix for mounted drives based on the subsystem.
# In Git Bash drives are mounted /c /d so root prefix is empty.
# In Ubuntu for windows drives are mounted in /mnt/c /mnt/d so root prefix is /mnt
setRootPrefix() {
  # Ubuntu for windows
  ROOT_PREFIX="/mnt"
  local MSYSTEM_MINGW="${MSYSTEM}"
  MSYSTEM_MINGW=`echo ${MSYSTEM_MINGW:0:5}`
  if [ "${MSYSTEM_MINGW}" == "MINGW" ]; then 
    # Git Bash
    ROOT_PREFIX=""
  fi
}
setRootPrefix

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
