HTTPD_DIR="${HOME}/programs/apache-httpd"

setHttpDir() {
  if ${IS_LINUX_HOST}; then
    HTTPD_DIR="/etc/apache2"
  fi  
  export HTTPD_DIR="${HTTPD_DIR}"
  log.trace "HTTPD_DIR=${HTTPD_DIR}"
}

setHttpDir "$@"
