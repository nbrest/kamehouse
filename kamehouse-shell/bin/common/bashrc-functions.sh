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

# Add the specified path and subpaths to the PATH variable
###########################################################################
# IMPORTANT: If I block a path here, also block it to csv-kamehouse-shell.sh
###########################################################################
addToPath() {
  local BASE_PATH=$1
  local PATHS_TO_SKIP_REGEX=$2
  # List all directories
  local PATH_TO_ADD=$(find ${BASE_PATH} -name '.*' -prune -o -type d)
  # Filter aws
  PATH_TO_ADD=$(echo "$PATH_TO_ADD" | grep -v '/aws')
  # Filter bashrc
  PATH_TO_ADD=$(echo "$PATH_TO_ADD" | grep -v /aws/bashrc) 
  PATH_TO_ADD=$(echo "$PATH_TO_ADD" | grep -v /lin/bashrc) 
  PATH_TO_ADD=$(echo "$PATH_TO_ADD" | grep -v /win/bashrc) 
  PATH_TO_ADD=$(echo "$PATH_TO_ADD" | grep -v /common/bashrc) 
  # Filter deprecated
  PATH_TO_ADD=$(echo "$PATH_TO_ADD" | grep -v /deprecated)
  # Filter sudoers
  PATH_TO_ADD=$(echo "$PATH_TO_ADD" | grep -v /lin/sudoers)
  # Filter path to skip parameter
  PATH_TO_ADD=$(echo "$PATH_TO_ADD" | grep -v -e "${PATHS_TO_SKIP_REGEX}") 
  # Filter .. directory
  PATH_TO_ADD=$(echo "$PATH_TO_ADD" | grep -v '/\..*')
  # Replace \n with :  
  PATH_TO_ADD=$(echo "$PATH_TO_ADD" | tr '\n' ':')
  # Remove last :
  PATH_TO_ADD=$(echo "$PATH_TO_ADD" | sed '$s/.$//')

  if [[ ! ${PATH} =~ "${PATH_TO_ADD}" ]]; then
    # "${PATH} doesn't contain ${PATH_TO_ADD}"
    export PATH=${PATH}:${PATH_TO_ADD}
  fi
} 

################################################
# Calls to functions that set common variables #
################################################

# Sets the IS_LINUX_HOST variable
setIsLinuxHost
