# call this script with 'source PATH-TO-SCRIPT/set-java-home.sh'

log.info "Setting JAVA_HOME"

if ${IS_LINUX_HOST}; then
  source ${HOME}/programs/kamehouse-shell/bin/lin/bashrc/java-home.sh
else
  export JAVA_HOME="C:\Program Files\Java\jdk-17"
fi

log.info "JAVA_HOME=${JAVA_HOME}"