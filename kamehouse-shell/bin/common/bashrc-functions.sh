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
