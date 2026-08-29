IS_DEV_ENVIRONMENT="false"

parseArguments() {
  local OPTIONS=("$@")
  for i in "${!OPTIONS[@]}"; do
    local CURRENT_OPTION="${OPTIONS[i]}"
    if [ "${CURRENT_OPTION:0:1}" != "-" ]; then
      continue
    fi
    local CURRENT_OPTION_ARG="${OPTIONS[i+1]}"
    case "${CURRENT_OPTION}" in
      --dev-env)
        IS_DEV_ENVIRONMENT="true"
        ;;
    esac
  done    
}

printHelpOptions() {
  addHelpOption "--dev-env" "Set the environment to dev"
}
