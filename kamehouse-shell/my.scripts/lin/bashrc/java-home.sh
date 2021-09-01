# Sort them in order from less priority to highest priority 
# so the last one that matches is the one I want

### Java 8
if [ -d "/usr/lib/jvm/java-8-openjdk-armhf" ]; then
  export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-armhf
fi

if [ -d "/usr/lib/jvm/java-8-oracle" ]; then
  export JAVA_HOME=/usr/lib/jvm/java-8-oracle
fi

### Java 11
if [ -d "/usr/lib/jvm/java-11-openjdk-armhf" ]; then
  export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-armhf
fi

if [ -d "/usr/lib/jvm/java-11-openjdk-amd64" ]; then
  export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
fi

