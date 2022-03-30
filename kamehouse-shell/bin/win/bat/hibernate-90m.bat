@echo off
setlocal EnableDelayedExpansion
REM git-bash.bat -c "${HOME}/programs/kamehouse-shell/bin/win/shutdown/shutdown.sh -t 90"
echo !date! !time! - [INFO] - Sleeping for 90 minutes, then hibernating. Press Ctrl+C to abort
FOR /L %%G IN (10,10,90) DO (
  REM simulate sleep with ping command
  ping 127.0.0.1 -n 601 > null
  set /a SLEEP_TIME = 90-%%G
  echo !date! !time! - [INFO] - Sleeping for !SLEEP_TIME! minutes, then hibernating. Press Ctrl+C to abort
)
echo !date! !time! - [INFO] - Hibernating now!
shutdown.exe /h /f
