alias cd.git="cd ${HOME}/git"
alias cd.kamehouse='cd ${HOME}/workspace/kamehouse'
alias cd.git.kamehouse='cd ${HOME}/git/kamehouse'
alias cd.logs='cd ${HOME}/logs'
alias cd.programs="cd ${HOME}/programs"
alias cd.tomcat="cd ${HOME}/programs/apache-tomcat"
alias cd.shell="cd ${HOME}/programs/kamehouse-shell/bin"
alias cd.webapps="cd ${HOME}/programs/apache-tomcat/webapps"
alias cd.workspace="cd ${HOME}/workspace"

alias edit.kamehouse.cfg="vim ${HOME}/.kamehouse/kamehouse.cfg"

alias reload.bashrc='source ${HOME}/.bashrc'

alias ls='ls --color=auto'

alias CD=cd
alias LS=ls
alias PS=ps

alias sr='screen -D -RR'
alias sl='screen -ls'
alias sd='screen -d'
alias s='screen'

if [ -f "/usr/sbin/poweroff" ] || [ -f "/sbin/poweroff" ]; then
  alias poweroff='sudo poweroff' 
fi
if [ -f "/usr/sbin/reboot" ] || [ -f "/sbin/reboot" ]; then
  alias reboot='sudo reboot'
fi
