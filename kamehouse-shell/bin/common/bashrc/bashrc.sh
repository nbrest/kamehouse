source ${HOME}/programs/kamehouse-shell/bin/common/functions/colors-functions.sh
source ${HOME}/programs/kamehouse-shell/bin/common/bashrc/alias.sh
source ${HOME}/programs/kamehouse-shell/bin/common/bashrc/functions.sh
source ${HOME}/programs/kamehouse-shell/bin/common/bashrc/prompt.sh
source ${HOME}/programs/kamehouse-shell/bin/common/bashrc/message.sh
source ${HOME}/programs/kamehouse-shell/bin/common/bashrc/path.sh
source ${HOME}/programs/kamehouse-shell/bin/common/bashrc/alias.sh

export EDITOR=vim

### Java 17
# Sort them in order from less priority to highest priority 
# so the last one that matches is the one I want
if [ -d "/usr/lib/jvm/java-17-openjdk-armhf" ]; then
  export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-armhf
fi

if [ -d "/usr/lib/jvm/java-17-openjdk-amd64" ]; then
  export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
fi
