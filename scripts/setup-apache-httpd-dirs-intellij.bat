@echo off

mkdir "%USERPROFILE%\programs\apache-httpd\www\www-intellij"

rmdir "%USERPROFILE%\programs\apache-httpd\www\www-intellij\kame-house-mobile"
mklink /D "%USERPROFILE%\programs\apache-httpd\www\www-intellij\kame-house-mobile" "%USERPROFILE%\workspace-intellij\kamehouse\kamehouse-mobile\www\kame-house-mobile"

pause