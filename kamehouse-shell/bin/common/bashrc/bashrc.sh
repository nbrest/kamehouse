source ${HOME}/programs/kamehouse-shell/bin/common/bashrc-functions.sh

if ${IS_LINUX_HOST}; then
  source ${HOME}/programs/kamehouse-shell/bin/lin/bashrc/bashrc.sh
else
  source ${HOME}/programs/kamehouse-shell/bin/win/bashrc/bashrc.sh
fi
