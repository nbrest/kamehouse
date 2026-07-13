source ${HOME}/programs/kamehouse-shell/bin/functions/colors-functions.sh
source ${HOME}/programs/kamehouse-shell/bin/bashrc/alias.sh
source ${HOME}/programs/kamehouse-shell/bin/bashrc/functions.sh
source ${HOME}/programs/kamehouse-shell/bin/bashrc/prompt.sh
source ${HOME}/programs/kamehouse-shell/bin/bashrc/message.sh
source ${HOME}/programs/kamehouse-shell/bin/bashrc/path.sh
source ${HOME}/programs/kamehouse-shell/bin/bashrc/alias.sh
source ${HOME}/programs/kamehouse-shell/bin/bashrc/tmux.sh
source ${HOME}/programs/kamehouse-shell/bin/deploy/set-java-home.sh --skip-override --skip-log

export EDITOR=vim

# Use same sorting order from tty and ssh
export LC_ALL=C

