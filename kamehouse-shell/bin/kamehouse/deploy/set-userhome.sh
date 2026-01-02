USERHOME_WIN="${HOME}"
# USERHOME_LIN gets set during install kamehouse-shell
USERHOME_LIN="/home/${DEFAULT_KAMEHOUSE_USERNAME}"

setUserHome() {
  # WIN_USER_HOME=`powershell.exe -c 'echo %USERPROFILE%'`
  # WIN_USER_HOME=${WIN_USER_HOME::-1}

  if ${IS_LINUX_HOST}; then
    export HOME="${USERHOME_LIN}"
  else
    export HOME="${USERHOME_WIN}"
  fi
  log.trace "HOME=${HOME}"
}

setUserHome "$@"
