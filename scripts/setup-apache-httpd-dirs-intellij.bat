@echo off

mkdir "%USERPROFILE%\programs\apache-httpd\www\www-intellij"

rmdir "%USERPROFILE%\programs\apache-httpd\www\www-intellij\kame-house"
mklink /D "%USERPROFILE%\programs\apache-httpd\www\www-intellij\kame-house" "%USERPROFILE%\workspace-intellij\kamehouse\kamehouse-ui\src\main\webapp"

rmdir "%USERPROFILE%\programs\apache-httpd\www\www-intellij\kame-house-groot"
mklink /D "%USERPROFILE%\programs\apache-httpd\www\www-intellij\kame-house-groot" "%USERPROFILE%\workspace-intellij\kamehouse\kamehouse-groot\public\kame-house-groot"

pause