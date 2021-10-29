@echo off
setlocal EnableDelayedExpansion
set "FILE_TO_PLAY=%*"
rem echo !FILE_TO_PLAY!

FOR /F "tokens=* USEBACKQ" %%F IN (`dir "C:\Program Files (x86)\VideoLAN" /s /b ^| find "vlc.exe"`) DO (
  SET VLC_EXEC=%%F
)

if "%VLC_EXEC%" == "" ( 
  FOR /F "tokens=* USEBACKQ" %%F IN (`dir "C:\Program Files\VideoLAN" /s /b ^| find "vlc.exe"`) DO (
    SET VLC_EXEC=%%F
  )
)

if "%VLC_EXEC%" == "" ( 
  echo "vlc.exe not found"
  exit 1
) 

schtasks /delete /TN vlc-scheduled-run
schtasks /create /SC ONCE /TN vlc-scheduled-run /TR "%VLC_EXEC% !FILE_TO_PLAY!" /ST 23:59
schtasks /run /TN vlc-scheduled-run
schtasks /delete /TN vlc-scheduled-run /F



