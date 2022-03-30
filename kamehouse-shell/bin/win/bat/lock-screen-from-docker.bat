@echo off
setlocal EnableDelayedExpansion

schtasks /delete /TN lock-screen-from-docker-run
schtasks /create /SC ONCE /TN lock-screen-from-docker-run /TR "rundll32.exe user32.dll,LockWorkStation" /ST 23:59
schtasks /run /TN lock-screen-from-docker-run
schtasks /delete /TN lock-screen-from-docker-run /F
