@echo off
setlocal EnableDelayedExpansion

schtasks /delete /TN screen-lock-scheduled-task
schtasks /create /SC ONCE /TN screen-lock-scheduled-task /TR "rundll32.exe user32.dll,LockWorkStation" /ST 23:59
schtasks /run /TN screen-lock-scheduled-task
schtasks /delete /TN screen-lock-scheduled-task /F
