# Sort them in order from less priority to highest priority 
# so the last one that matches is the one I want

### Java 17
if [ -d "/usr/lib/jvm/java-17-openjdk-armhf" ]; then
  export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-armhf
fi

if [ -d "/usr/lib/jvm/java-17-openjdk-amd64" ]; then
  export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
fi
