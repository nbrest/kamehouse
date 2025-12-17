@echo off
rem simplified bash.bat without any logging at all. For use on scripts where I only want returned the main script output
setlocal EnableDelayedExpansion
set "arguments=%*"
rem Start !arguments! with -c ". /etc/profile ; command I want to run"
"C:\msys64\usr\bin\bash.exe" !arguments!
exit
