@echo off

mkdir "%USERPROFILE%\programs\apache-httpd\www\www-intellij"

rmdir "%USERPROFILE%\programs\apache-httpd\www\www-intellij\kame-house"
mklink /D "%USERPROFILE%\programs\apache-httpd\www\www-intellij\kame-house" "%USERPROFILE%\workspace-intellij\kamehouse\kamehouse-ui\src\main\webapp"

rmdir "%USERPROFILE%\programs\apache-httpd\www\www-intellij\kame-house-groot"
mklink /D "%USERPROFILE%\programs\apache-httpd\www\www-intellij\kame-house-groot" "%USERPROFILE%\workspace-intellij\kamehouse\kamehouse-groot\src\main\webapp\kame-house-groot"

rmdir "%USERPROFILE%\programs\apache-httpd\www\www-intellij\kame-house-mobile"
mklink /D "%USERPROFILE%\programs\apache-httpd\www\www-intellij\kame-house-mobile" "%USERPROFILE%\workspace-intellij\kamehouse\kamehouse-mobile\www\kame-house-mobile"

pause