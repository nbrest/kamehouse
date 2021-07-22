@ECHO OFF

cecho --04

cecho     Restarting all network connections ...

ipconfig /release

sleep 3

ipconfig /renew

cecho --03

cecho     done ...

cecho --07
