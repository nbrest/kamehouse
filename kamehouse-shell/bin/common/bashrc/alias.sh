# cd
alias cd.git="cd ${HOME}/git"
alias cd.git.kamehouse='cd ${HOME}/git/kamehouse'
alias cd.kamehouse='cd ${HOME}/workspace/kamehouse'
alias cd.logs='cd ${HOME}/logs'
alias cd.programs="cd ${HOME}/programs"
alias cd.tomcat="cd ${HOME}/programs/apache-tomcat"
alias cd.shell="cd ${HOME}/programs/kamehouse-shell/bin"
alias cd.webapps="cd ${HOME}/programs/apache-tomcat/webapps"
alias cd.workspace="cd ${HOME}/workspace"

# edit
alias edit.kamehouse.cfg="vim ${HOME}/.kamehouse/config/kamehouse.cfg"

# reload
alias reload.bashrc='source ${HOME}/.bashrc'

# bash
alias aa='alias'

alias ls='ls --color=auto'

alias CD=cd
alias LS=ls
alias PS=ps

# screen
alias sr='screen -D -RR'
alias sl='screen -ls'
alias sd='screen -d'
alias s='screen'

# sudo
if [ -f "/usr/sbin/poweroff" ] || [ -f "/sbin/poweroff" ]; then
  alias poweroff='sudo poweroff' 
fi
if [ -f "/usr/sbin/reboot" ] || [ -f "/sbin/reboot" ]; then
  alias reboot='sudo reboot'
fi

# kamehouse-shell scripts shortcuts
alias kat="keep-alive-toggle.sh"
alias kas="keep-alive-status.sh"
alias snape="snape.sh"
