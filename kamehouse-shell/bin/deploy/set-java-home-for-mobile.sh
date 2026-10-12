# call this script with 'source PATH-TO-SCRIPT/set-java-home-for-mobile.sh'

log.info "Setting JAVA_HOME for mobile build"

source "$(dirname "${BASH_SOURCE[0]}")/set-java-home.sh" --override --log
