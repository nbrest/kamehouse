@echo off

mkdir "%USERPROFILE%\programs\apache-httpd\www\kamehouse-webserver"

rmdir "%USERPROFILE%\programs\apache-httpd\www\kamehouse-webserver\kame-house"
mklink /D "%USERPROFILE%\programs\apache-httpd\www\kamehouse-webserver\kame-house" "%USERPROFILE%\git\kamehouse\kamehouse-ui\src\main\webapp"

rmdir "%USERPROFILE%\programs\apache-httpd\www\kamehouse-webserver\kame-house-groot"
mklink /D "%USERPROFILE%\programs\apache-httpd\www\kamehouse-webserver\kame-house-groot" "%USERPROFILE%\git\kamehouse\kamehouse-groot\public\kame-house-groot"

pause