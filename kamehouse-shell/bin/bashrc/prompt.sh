##### Prompt Settings
PROMPT_SH_PATH=`dirname ${BASH_SOURCE[0]}`
source ${PROMPT_SH_PATH}/git-prompt.sh

if (( $EUID != 0 )); then
  # Normal user
  PS1_PREFIX="\[\e]0;\u@\h:\w\a\]"
  PS1_SCREEN="\[${COL_BLUE}\]S:"
  PS1_TMUX="\[${COL_BLUE}\]T:"
  PS1_KAMEHOUSE="\[${COL_BLUE}\][\[${COL_PURPLE}\]KameHouse\[${COL_BLUE}\]] "
  PS1_USERNAME="\[${COL_GREEN}\]\u"
  PS1_AT="\[${COL_BLUE}\]@"
  PS1_HOSTNAME="\[${COL_PURPLE}\]\h"
  PS1_DOT="\[${COL_BLUE}\]:"
  PS1_PWD="\[${COL_CYAN}\]\w"
  PS1_GIT="\[${COL_YELLOW}\]"'`__git_ps1 " (%s)"`'
  PS1_SUFFIX="\[${COL_BLUE}\]\$ \[${COL_NORMAL}\]"
else
  # root
  PS1_PREFIX="\[\e]0;\u@\h:\w\a\]"
  PS1_SCREEN="\[${COL_BLUE}\]S:"
  PS1_TMUX="\[${COL_BLUE}\]T:"
  PS1_KAMEHOUSE="\[${COL_BLUE}\][\[${COL_RED}\]KameHouse\[${COL_BLUE}\]] "
  PS1_USERNAME="\[${COL_RED}\]\u"
  PS1_AT="\[${COL_BLUE}\]@"
  PS1_HOSTNAME="\[${COL_RED}\]\h"
  PS1_DOT="\[${COL_BLUE}\]:"
  PS1_PWD="\[${COL_CYAN}\]\w"
  PS1_GIT="\[${COL_YELLOW}\]"'`__git_ps1 " (%s)"`'
  PS1_SUFFIX="\[${COL_BLUE}\]\$ \[${COL_NORMAL}\]"
fi

if [ -n "$STY" ]; then
  # using screen
  PS1=${PS1_PREFIX}${PS1_SCREEN}${PS1_KAMEHOUSE}${PS1_USERNAME}${PS1_AT}${PS1_HOSTNAME}${PS1_DOT}${PS1_PWD}${PS1_GIT}${PS1_SUFFIX}
else
  if [ "${TERM_PROGRAM}" == "tmux" ]; then
    # using tmux
    PS1=${PS1_PREFIX}${PS1_TMUX}${PS1_KAMEHOUSE}${PS1_USERNAME}${PS1_AT}${PS1_HOSTNAME}${PS1_DOT}${PS1_PWD}${PS1_GIT}${PS1_SUFFIX}
  else
    # using standard screen
    PS1=${PS1_PREFIX}${PS1_KAMEHOUSE}${PS1_USERNAME}${PS1_AT}${PS1_HOSTNAME}${PS1_DOT}${PS1_PWD}${PS1_GIT}${PS1_SUFFIX}  
  fi
fi
