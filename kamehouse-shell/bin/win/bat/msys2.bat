@echo off
rem Set DEBUG_MSYS2 env variable to true to log debug
setlocal EnableDelayedExpansion
set INFO=- [INFO] - msys2.bat -
set DEBUG=- [DEBUG] - msys2.bat -
echo "" > %USERPROFILE%/logs/msys2.log
echo %DATE% %TIME% %INFO% Started executing msys2.bat >> %USERPROFILE%/logs/msys2.log
if "%DEBUG_MSYS2%"=="true" (
  echo %DATE% %TIME% %DEBUG% Arguments: %* >> %USERPROFILE%/logs/msys2.log
)
set "arguments=%*"

rem Execute bash.exe without arguments
if "!arguments!"=="" ( 
  echo %DATE% %TIME% %INFO% Started executing bash.exe without arguments >> %USERPROFILE%/logs/msys2.log
  
  "C:\msys64\usr\bin\bash.exe" 
  
  echo %DATE% %TIME% %INFO% Finished executing bash.exe without arguments >> %USERPROFILE%/logs/msys2.log
)

rem Execute bash.exe with arguments
if defined arguments (
  if "%DEBUG_MSYS2%"=="true" (
    echo %DATE% %TIME% %DEBUG% Started executing bash.exe with arguments !arguments! >> %USERPROFILE%/logs/msys2.log
  )

  "C:\msys64\usr\bin\bash.exe" !arguments!
  
  if "%DEBUG_MSYS2%"=="true" (
    echo %DATE% %TIME% %DEBUG% Finished executing bash.exe with arguments !arguments! >> %USERPROFILE%/logs/msys2.log
  )
)

exit
