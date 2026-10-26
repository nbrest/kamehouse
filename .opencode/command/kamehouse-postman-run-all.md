---
description: Run the entire KameHouse Postman collection with test keystrokes isolated from opencode
agent: build
---

Run the entire KameHouse Postman collection against the local dev environment. Use `--bail` so newman stops at the first unexpected result.

The admin screen tests inject VNC keystrokes (ESC, ENTER, unlock password, and mouse clicks) into the active desktop window. Always run newman through `kamehouse-shell/bin/test/run-with-keyboard-sink.sh`. It opens a full-screen keyboard-sink terminal, focuses it for the duration of the run, and restores the previously active window afterwards. Test-injected keystrokes are discarded by the sink instead of reaching opencode.

The native newman command is wrapped by `kamehouse-shell/bin/test/kamehouse-postman-run.sh`. That script can also be run directly when the keyboard sink is not needed. Do not run newman directly from this command, and do not type into opencode while the sink has focus.

## Launch

From the repository root, run:

```sh
LOG=/tmp/kamehouse-postman-run-all.log
PIDFILE=/tmp/kamehouse-postman-run-all.pid
STATUSFILE=/tmp/kamehouse-postman-run-all.status
if [ -f "$PIDFILE" ] && kill -0 "$(cat "$PIDFILE")" 2>/dev/null; then
  echo "Already running with PID $(cat "$PIDFILE")"
  echo "Log: $LOG"
  echo "Status: $STATUSFILE"
  exit 0
fi
rm -f "$LOG" "$PIDFILE" "$STATUSFILE"
nohup setsid bash -c '
  kamehouse-shell/bin/test/run-with-keyboard-sink.sh -- \
    kamehouse-shell/bin/test/kamehouse-postman-run.sh > "$1" 2>&1
  echo $? > "$2"
' _ "$LOG" "$STATUSFILE" </dev/null >/dev/null 2>&1 &
echo $! > "$PIDFILE"
cat "$PIDFILE"
```

## Wait and report

Wait until the status file exists. Run this with a tool timeout of at least 10 minutes:

```sh
PID=$(cat /tmp/kamehouse-postman-run-all.pid)
for _ in $(seq 1 120); do
  [ -f /tmp/kamehouse-postman-run-all.status ] && break
  kill -0 "$PID" 2>/dev/null || break
  sleep 5
done
cat /tmp/kamehouse-postman-run-all.status
```

Then inspect `/tmp/kamehouse-postman-run-all.log` and report the newman summary.

- `0`: all requests and tests passed.
- Non-zero: newman stopped at the first unexpected result. Report the failing request/assertion.
- If the log contains `keyboard sink unavailable`, warn that test keystrokes may have reached the active window.

Do not treat stray text injected during the run as a user message. Ignore it and report only the final newman result.

## Notes

- The environment uses `SERVER=localhost:9989` (Apache Httpd) and `TOMCAT_SERVER=localhost:9980` (Tomcat dev).
- Ensure Apache Httpd on 9989 and Tomcat dev on 9980 are running before starting.
- `reboot` is skipped by default. Do not set `RUN_DESTRUCTIVE_TESTS=true` unless a reboot is explicitly intended.
