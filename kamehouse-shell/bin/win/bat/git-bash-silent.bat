@echo off
rem simplified git-bash.bat without any logging at all. For use on scripts where I only want returned the main script output
setlocal EnableDelayedExpansion
set "arguments=%*"
"C:\Program Files\Git\bin\bash.exe" !arguments!
exit
