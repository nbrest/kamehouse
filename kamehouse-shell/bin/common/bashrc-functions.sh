##########################################
# Common variables used by other scripts #
##########################################

# Boolean returning true if I'm running in a Linux host.
IS_LINUX_HOST=false

##########################################
# Common functions used by other scripts #
##########################################

# Is the script running under windows or linux. Sets the variable IS_LINUX_HOST
# to be used in the script when needed. Default is false.
# If this changes, also change it in my.scripts repo path.sh
setIsLinuxHost() {
  IS_LINUX_HOST=false
  local UNAME_S=`uname -s`
  local UNAME_R=`uname -r`
  if [ "${UNAME_S}" != "Linux" ]; then
    # Using Git Bash
    IS_LINUX_HOST=false
  else 
    if [[ ${UNAME_R} == *"Microsoft"* ]]; then
      # Using Ubuntu for Windows 10 (deprecated. don't use that anymore, use an ubuntu vm)
      IS_LINUX_HOST=false
    else
      # Using Linux
      IS_LINUX_HOST=true
    fi
  fi
}

################################################
# Calls to functions that set common variables #
################################################

# Sets the IS_LINUX_HOST variable
setIsLinuxHost

################################################
# Custom non-native bash functions
################################################
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
