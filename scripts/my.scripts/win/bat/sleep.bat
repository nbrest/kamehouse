@echo off
REM sleep.bat N : N = number of seconds to sleep
ping 127.0.0.1 -n %* > null && ping 127.0.0.1 -n 2 > null