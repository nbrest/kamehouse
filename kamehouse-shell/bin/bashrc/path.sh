KAMEHOUSE_SHELL_PATH_FILE=${HOME}/programs/kamehouse-shell/conf/path.conf
if [ -f "${KAMEHOUSE_SHELL_PATH_FILE}" ]; then
  source ${KAMEHOUSE_SHELL_PATH_FILE}

  if [[ ! ${PATH} =~ "${KAMEHOUSE_SHELL_SCRIPTS_PATH}" ]]; then
    # "${PATH} doesn't contain ${KAMEHOUSE_SHELL_SCRIPTS_PATH}"
    export PATH=${PATH}:${KAMEHOUSE_SHELL_SCRIPTS_PATH}
  fi
fi
