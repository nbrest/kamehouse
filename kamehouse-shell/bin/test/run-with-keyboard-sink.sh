#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/functions/common-functions.sh
if [ "$?" != "0" ]; then echo "Error importing common-functions.sh" ; exit 99 ; fi

initScriptEnv() {
  TITLE="KameHouse test keyboard sink"
  SINK_WIN=""
  PREV_WIN=""
  WATCHER_PID=""
  COMMAND=()
  trap cleanupKeyboardSink EXIT
  trap 'exitProcess 129' HUP
  trap 'exitProcess 143' TERM
}

mainProcess() {
  startKeyboardSink
  local STATUS=0
  "${COMMAND[@]}" || STATUS=$?
  exitProcess "${STATUS}"
}

startKeyboardSink() {
  if ! isKeyboardSinkSupported; then
    log.warn "keyboard sink unavailable; test keystrokes may reach the active window"
    return
  fi

  ACTIVE=$(xprop -root _NET_ACTIVE_WINDOW 2>/dev/null || true)
  PREV_WIN=${ACTIVE##*# }
  PREV_WIN=${PREV_WIN%% *}

  gnome-terminal --window --full-screen --title="${TITLE}" -- \
    bash -c 'stty -echo 2>/dev/null || true; clear 2>/dev/null || true; printf "\n\nRunning kamehouse tests...\n\n"; while IFS= read -r _; do :; done' >/dev/null 2>&1 &

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
  if [ -n "${SINK_WIN}" ]; then
    wmctrl -i -c "${SINK_WIN}" 2>/dev/null || true
    sleep 0.2
  fi
  if [ -n "${PREV_WIN}" ]; then
    wmctrl -i -a "${PREV_WIN}" 2>/dev/null || true
    sleep 0.2
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
