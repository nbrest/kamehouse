# call this script with 'source PATH-TO-SCRIPT/set-java-home-for-itunes-export.sh'

log.info "Setting JAVA_HOME for itunes export"

export JAVA_HOME="C:\Program Files\Java\jdk-11.0.12"

if [ -d "/usr/lib/jvm/java-11-openjdk-armhf" ]; then
  export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-armhf
fi

if [ -d "/usr/lib/jvm/java-11-openjdk-amd64" ]; then
  export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
fi

log.info "JAVA_HOME=${JAVA_HOME}"

