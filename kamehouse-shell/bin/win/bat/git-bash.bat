@echo off
rem Set DEBUG_GIT_BASH env variable to true to log debug
setlocal EnableDelayedExpansion
set INFO=- [INFO] - git-bash.bat -
set DEBUG=- [DEBUG] - git-bash.bat -
echo "" > %USERPROFILE%/logs/git-bash.log
echo %DATE% %TIME% %INFO% Started executing git-bash.bat >> %USERPROFILE%/logs/git-bash.log
if "%DEBUG_GIT_BASH%"=="true" (
  echo %DATE% %TIME% %DEBUG% Arguments: %* >> %USERPROFILE%/logs/git-bash.log
)
set "arguments=%*"

rem Execute bash.exe without arguments
if "!arguments!"=="" ( 
  echo %DATE% %TIME% %INFO% Started executing bash.exe without arguments >> %USERPROFILE%/logs/git-bash.log
  
  "C:\Program Files\Git\bin\bash.exe" 
  
  echo %DATE% %TIME% %INFO% Finished executing bash.exe without arguments >> %USERPROFILE%/logs/git-bash.log
)

rem Execute bash.exe with arguments
if defined arguments (
  if "%DEBUG_GIT_BASH%"=="true" (
    echo %DATE% %TIME% %DEBUG% Started executing bash.exe with arguments !arguments! >> %USERPROFILE%/logs/git-bash.log
  )

  "C:\Program Files\Git\bin\bash.exe" !arguments!
  
  if "%DEBUG_GIT_BASH%"=="true" (
    echo %DATE% %TIME% %DEBUG% Finished executing bash.exe with arguments !arguments! >> %USERPROFILE%/logs/git-bash.log
  )
)

exit
