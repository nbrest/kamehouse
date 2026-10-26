#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/functions/common-functions.sh
if [ "$?" != "0" ]; then echo "Error importing common-functions.sh" ; exit 99 ; fi

initScriptEnv() {
  TITLE="KameHouse test keyboard sink"
  SINK_WIN=""
  PREV_WIN=""
  WATCHER_PID=""
  SINK_TMP_DIR=""
  SINK_STATUS_FILE=""
  COMMAND=()
  trap cleanupKeyboardSink EXIT
  trap 'exitProcess 129' HUP
  trap 'exitProcess 143' TERM
}

mainProcess() {
  startKeyboardSink
  local STATUS=0
  runCommand || STATUS=$?
  exitProcess "${STATUS}"
}

runCommand() {
  "${COMMAND[@]}" | while IFS= read -r LINE || [ -n "${LINE}" ]; do
    printf '%s\n' "${LINE}"
    updateSinkStatus "${LINE}"
  done
  return "${PIPESTATUS[0]}"
}

updateSinkStatus() {
  if [ -z "${SINK_STATUS_FILE}" ]; then
    return
  fi
  local LINE="$1"
  LINE="${LINE//$'\r'/}"
  if [[ "${LINE}" == *$'\e'* ]]; then
    LINE=$(printf '%s' "${LINE}" | sed -E 's/\x1b\[[0-9;]*[A-Za-z]//g')
  fi
  LINE="${LINE#"${LINE%%[![:space:]]*}"}"
  LINE="${LINE%"${LINE##*[![:space:]]}"}"
  if [ -z "${LINE}" ]; then
    return
  fi
  local LOWER="${LINE,,}"
  if [[ "${LOWER}" =~ (password|passwd|secret|token|credential|apikey|api_key) ]]; then
    LINE="[redacted output]"
  fi
  printf '%s\n' "${LINE}" > "${SINK_STATUS_FILE}"
}

startKeyboardSink() {
  if ! isKeyboardSinkSupported; then
    log.warn "keyboard sink unavailable; test keystrokes may reach the active window"
    return
  fi

  ACTIVE=$(xprop -root _NET_ACTIVE_WINDOW 2>/dev/null || true)
  PREV_WIN=${ACTIVE##*# }
  PREV_WIN=${PREV_WIN%% *}

  if ! SINK_TMP_DIR=$(mktemp -d "${TMPDIR:-/tmp}/kamehouse-keyboard-sink.XXXXXX"); then
    SINK_TMP_DIR=""
    log.warn "keyboard sink unavailable; test keystrokes may reach the active window"
    return
  fi
  SINK_STATUS_FILE="${SINK_TMP_DIR}/status"
  local SINK_SCRIPT="${SINK_TMP_DIR}/render.sh"
  printf '%s\n' "Waiting for test output..." > "${SINK_STATUS_FILE}"
  cat > "${SINK_SCRIPT}" <<'SINK_RENDER_SCRIPT'
#!/bin/bash

STATUS_FILE="$1"
COMMAND_TEXT="$2"
START_EPOCH="$3"

stty -echo 2>/dev/null || true
printf '\033[?25l'
trap 'printf "\033[?25h"' EXIT

( while IFS= read -r _; do :; done ) &

printf '\033[2J\033[H'
cols=$(tput cols 2>/dev/null || echo 100)
case "${cols}" in
  ''|*[!0-9]*) cols=100 ;;
esac
[ "${cols}" -lt 60 ] && cols=60
rule=""
i=0
while [ "${i}" -lt $((cols - 4)) ]; do
  rule="${rule}━"
  i=$((i + 1))
done

printf '\033[1;36m  %s\033[0m\n' "${rule}"
printf '\033[1;35m   KAMEHOUSE TEST KEYBOARD SINK\033[0m\n'
printf '\033[2;37m   Test keystrokes are being discarded\033[0m\n'
printf '\033[1;36m  %s\033[0m\n\n' "${rule}"
printf '  \033[1;34mCommand :\033[0m \033[0;37m%s\033[0m\n' "${COMMAND_TEXT:0:cols-14}"
printf '  \033[1;34mStarted :\033[0m \033[0;37m%s\033[0m\n\n' "$(date '+%Y-%m-%d %H:%M:%S')"
printf '  \033[1;32mRunning tests, please wait...\033[0m\n\n'

spinner=('⠋' '⠙' '⠹' '⠸' '⠼' '⠴' '⠦' '⠧' '⠇' '⠏')
frame=0
while true; do
  [ -f "${STATUS_FILE%/*}/done" ] && break
  status=$(tail -n 1 "${STATUS_FILE}" 2>/dev/null)
  [ -z "${status}" ] && status="Waiting for test output..."
  now=$(date +%s)
  elapsed=$((now - START_EPOCH))
  [ "${elapsed}" -lt 0 ] && elapsed=0
  elapsed_text=$(printf '%02d:%02d' $((elapsed / 60)) $((elapsed % 60)))
  icon=${spinner[$((frame % ${#spinner[@]}))]}
  max=$((cols - 24))
  [ "${max}" -lt 20 ] && max=20
  printf '\r\033[2K  \033[1;33m%s\033[0m \033[2m[%s]\033[0m \033[1;36mCurrent:\033[0m %s' "${icon}" "${elapsed_text}" "${status:0:max}"
  frame=$((frame + 1))
  sleep 0.2
done
SINK_RENDER_SCRIPT
  chmod +x "${SINK_SCRIPT}"

  gnome-terminal --window --full-screen --title="${TITLE}" -- \
    bash "${SINK_SCRIPT}" "${SINK_STATUS_FILE}" "${COMMAND[0]##*/}" "$(date +%s)" >/dev/null 2>&1 &

  for _ in {1..50}; do
    while read -r id rest; do
      case "${rest}" in
        *"${TITLE}"*)
          SINK_WIN=${id}
          break
          ;;
      esac
    done < <(wmctrl -l 2>/dev/null || true)
    [ -n "${SINK_WIN}" ] && break
    sleep 0.1
  done

  if [ -z "${SINK_WIN}" ]; then
    log.warn "keyboard sink unavailable; test keystrokes may reach the active window"
    return
  fi

  wmctrl -i -a "${SINK_WIN}" 2>/dev/null || true
  sleep 0.2

  if [ -n "${PREV_WIN}" ]; then
    startKeyboardSinkWatcher
  fi

  log.info "Keyboard sink active. Test keystrokes will be discarded until the command finishes."
}

isKeyboardSinkSupported() {
  if [ -z "${DISPLAY:-}" ]; then
    log.debug "DISPLAY is not set"
    return 1
  fi
  if ! command -v wmctrl >/dev/null 2>&1; then
    log.debug "wmctrl is not available"
    return 1
  fi
  if ! command -v xprop >/dev/null 2>&1; then
    log.debug "xprop is not available"
    return 1
  fi
  if ! command -v gnome-terminal >/dev/null 2>&1; then
    log.debug "gnome-terminal is not available"
    return 1
  fi
  return 0
}

startKeyboardSinkWatcher() {
  (
    while true; do
      local ACTIVE=$(xprop -root _NET_ACTIVE_WINDOW 2>/dev/null || true)
      ACTIVE=${ACTIVE##*# }
      ACTIVE=${ACTIVE%% *}
      if [ "${ACTIVE}" == "${PREV_WIN}" ]; then
        wmctrl -i -a "${SINK_WIN}" 2>/dev/null || true
      fi
      sleep 0.5
    done
  ) &
  WATCHER_PID=$!
}

cleanupKeyboardSink() {
  if [ -n "${WATCHER_PID}" ]; then
    kill "${WATCHER_PID}" 2>/dev/null || true
  fi
  if [ -n "${SINK_TMP_DIR}" ]; then
    touch "${SINK_TMP_DIR}/done" 2>/dev/null || true
  fi
  if [ -n "${SINK_WIN}" ]; then
    wmctrl -i -c "${SINK_WIN}" 2>/dev/null || true
    sleep 0.2
  fi
  if [ -n "${PREV_WIN}" ]; then
    wmctrl -i -a "${PREV_WIN}" 2>/dev/null || true
    sleep 0.2
  fi
  if [ -n "${SINK_TMP_DIR}" ]; then
    rm -rf "${SINK_TMP_DIR}"
  fi
}

parseArguments() {
  local ARGS=("$@")
  if [ "${#ARGS[@]}" -eq 0 ]; then
    COMMAND=()
    return
  fi
  case "${ARGS[0]}" in
    --)
      COMMAND=("${ARGS[@]:1}")
      ;;
    -?*)
      parseInvalidArgument "${ARGS[0]}"
      ;;
    *)
      COMMAND=("${ARGS[@]}")
      ;;
  esac
}

setEnvFromArguments() {
  if [ "${#COMMAND[@]}" -eq 0 ]; then
    log.error "A command is required"
    printHelp
    exitProcess ${EXIT_INVALID_ARG}
  fi
}

main "$@"
