@echo off

mkdir "%USERPROFILE%\programs\apache-httpd\www\www-eclipse"

rmdir "%USERPROFILE%\programs\apache-httpd\www\www-eclipse\kame-house"
mklink /D "%USERPROFILE%\programs\apache-httpd\www\www-eclipse\kame-house" "%USERPROFILE%\workspace-eclipse\kamehouse\kamehouse-ui\dist"

rmdir "%USERPROFILE%\programs\apache-httpd\www\www-eclipse\kame-house-groot"
mklink /D "%USERPROFILE%\programs\apache-httpd\www\www-eclipse\kame-house-groot" "%USERPROFILE%\workspace-eclipse\kamehouse\kamehouse-groot\src\main\webapp\kame-house-groot"

rmdir "%USERPROFILE%\programs\apache-httpd\www\www-eclipse\kame-house-mobile"
mklink /D "%USERPROFILE%\programs\apache-httpd\www\www-eclipse\kame-house-mobile" "%USERPROFILE%\workspace-eclipse\kamehouse\kamehouse-eclipse\www\kame-house-mobile"

pause