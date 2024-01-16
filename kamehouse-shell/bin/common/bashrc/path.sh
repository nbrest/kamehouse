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

KAMEHOUSE_SHELL_PATH_FILE=${HOME}/programs/kamehouse-shell/conf/path.conf
if [ -f "${KAMEHOUSE_SHELL_PATH_FILE}" ]; then
  source ${KAMEHOUSE_SHELL_PATH_FILE}
  
  KAMEHOUSE_SHELL_PATH_TO_ADD=
  if ${IS_LINUX_HOST}; then
    KAMEHOUSE_SHELL_PATH_TO_ADD=${KAMEHOUSE_SHELL_LIN_PATH}
  else
    KAMEHOUSE_SHELL_PATH_TO_ADD=${KAMEHOUSE_SHELL_WIN_PATH}
  fi 

  if [[ ! ${PATH} =~ "${KAMEHOUSE_SHELL_PATH_TO_ADD}" ]]; then
    # "${PATH} doesn't contain ${KAMEHOUSE_SHELL_PATH_TO_ADD}"
    export PATH=${PATH}:${KAMEHOUSE_SHELL_PATH_TO_ADD}
  fi
fi
