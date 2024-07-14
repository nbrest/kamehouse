@echo off

mkdir "%USERPROFILE%\programs\apache-httpd\www\www-eclipse"

rmdir "%USERPROFILE%\programs\apache-httpd\www\www-eclipse\kame-house-mobile"
mklink /D "%USERPROFILE%\programs\apache-httpd\www\www-eclipse\kame-house-mobile" "%USERPROFILE%\workspace-eclipse\kamehouse\kamehouse-eclipse\www\kame-house-mobile"

pause