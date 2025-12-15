@echo off
rem simplified git-bash.bat without any logging at all. For use on scripts where I only want returned the main script output
setlocal EnableDelayedExpansion
if "%SWITCH_TO_MSYS2%"=="true" (
  %USERPROFILE%/programs/kamehouse-shell/bin/win/bat/bash-silent.bat %*
  exit
)
set "arguments=%*"
"C:\Program Files\Git\bin\bash.exe" !arguments!
exit
