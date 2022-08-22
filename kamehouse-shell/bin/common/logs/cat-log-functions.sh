LOG_PROCESS_TO_FILE=false

CAT_LOG_LEVEL=""
CAT_LOG_AWK=${HOME}/programs/kamehouse-shell/bin/awk/kamehouse/cat-log.awk

mainProcess() {
  cd ${HOME}/logs
  if [ -z "${CAT_LOG_LEVEL}" ]; then
    catLogFunction
  else
    catLogFunction | ${CAT_LOG_AWK} -v logLevel=${CAT_LOG_LEVEL}
  fi
}

catLogFunction() {
  log.info "Override catLogFunction() on each script with the cat or print log command"
  echo "" 
}

parseArguments() {
  unset OPTIND
  while getopts ":l:" OPT; do
    case $OPT in
    ("l")
      CAT_LOG_LEVEL="$OPTARG"
      ;;
    (\?)
      parseInvalidArgument "$OPTARG"
      ;;
    esac
  done  
}

printHelpOptions() {
  addHelpOption "-l (ALL|TRACE|DEBUG|INFO|WARN|ERROR)" "log level"
}