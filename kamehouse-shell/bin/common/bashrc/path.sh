KAMEHOUSE_SHELL_PATH_FILE=${HOME}/programs/kamehouse-shell/conf/kamehouse-shell-path.conf
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
