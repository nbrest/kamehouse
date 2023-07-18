# call this script with 'source PATH-TO-SCRIPT/set-java-home-for-mobile.sh'

log.info "Setting JAVA_HOME for mobile build"

if ${IS_LINUX_HOST}; then
  ### Java 11
  if [ -d "/usr/lib/jvm/java-11-openjdk-armhf" ]; then
    export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-armhf
  fi

  if [ -d "/usr/lib/jvm/java-11-openjdk-amd64" ]; then
    export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
  fi
else
  export JAVA_HOME="C:\Program Files\Java\jdk-11.0.12"
fi

log.info "JAVA_HOME=${JAVA_HOME}"

