@echo off
rem simplified bash.bat without any logging at all. For use on scripts where I only want returned the main script output
setlocal EnableDelayedExpansion
set "arguments=%*"
"C:\msys64\usr\bin\bash.exe" !arguments!
exit
