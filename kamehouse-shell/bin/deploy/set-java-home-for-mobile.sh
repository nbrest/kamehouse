# call this script with 'source PATH-TO-SCRIPT/set-java-home-for-mobile.sh'

log.info "Setting JAVA_HOME for mobile build"

KAMEHOUSE_JAVA_VERSION="${KAMEHOUSE_JAVA_VERSION:-21}"
source "$(dirname "${BASH_SOURCE[0]}")/set-java-home.sh" --override --log
unset KAMEHOUSE_JAVA_VERSION
