@echo off
rem Set DEBUG_BASH env variable to true to log debug
setlocal EnableDelayedExpansion
set INFO=- [INFO] - bash.bat -
set DEBUG=- [DEBUG] - bash.bat -
echo "" > %USERPROFILE%/logs/bash.log
echo %DATE% %TIME% %INFO% Started executing bash.bat >> %USERPROFILE%/logs/bash.log
if "%DEBUG_BASH%"=="true" (
  echo %DATE% %TIME% %DEBUG% Arguments: %* >> %USERPROFILE%/logs/bash.log
)
set "arguments=%*"

rem Execute bash.exe without arguments
if "!arguments!"=="" ( 
  echo %DATE% %TIME% %INFO% Started executing bash.exe without arguments >> %USERPROFILE%/logs/bash.log
  
  "C:\msys64\usr\bin\bash.exe" 
  
  echo %DATE% %TIME% %INFO% Finished executing bash.exe without arguments >> %USERPROFILE%/logs/bash.log
)

rem Execute bash.exe with arguments
if defined arguments (
  if "%DEBUG_BASH%"=="true" (
    echo %DATE% %TIME% %DEBUG% Started executing bash.exe with arguments !arguments! >> %USERPROFILE%/logs/bash.log
  )

  "C:\msys64\usr\bin\bash.exe" !arguments!
  
  if "%DEBUG_BASH%"=="true" (
    echo %DATE% %TIME% %DEBUG% Finished executing bash.exe with arguments !arguments! >> %USERPROFILE%/logs/bash.log
  )
)

exit
