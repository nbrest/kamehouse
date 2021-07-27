@echo off

set zero=0
set count=0
set maxcount=24

D:
cd \Niko9enzo\my.scripts\dependencies

:LOOP
	REM cmdow: aca hace visible y minimiza el cmd window actual
REM cmdow @ /MIN /VIS

cecho #Bn      Executing keep.server.online script: %time% %date% 

cecho --03
ping -n 1 www.google.com 
	REM // guardo el return value de ping en pingret para compararlo con zero en el if, el if compara 2 strings
set pingret=%ERRORLEVEL%
cecho --07
echo.

IF %pingret%==%zero% (

	cecho #Gn     %time% : SERVER ONLINE 
	echo %date% %time% : Server Online > keep.server.online.on.dep.txt 

) ELSE (

		REM : GEQ = greater or equal than
	IF %count% GEQ %maxcount% (
		
		echo 0 > keep.server.online.count.dep.txt
		REM reboot
		echo SHOULD REBOOT HERE!!!
		exit 0
	
	) ELSE (

		echo %count%
		set /A count=count+1
		echo %count%
		echo %count% > keep.server.online.count.dep.txt
		
	)

	cecho #Rn     %time% : WARNING: SERVER OFFLINE : restarting WIFI
	echo %date% %time% : Server OFFLINE  > keep.server.online.off.dep.txt 
	cecho --03
	REM // modificar @PCI de acuerdo a la placa que quiero reinicicar en cada computadora
    REM devcon restart =NET @PCI\VEN_14E4*
	REM sleep 5
	REM ipconfig /release *WIFI*
	REM sleep 3
	REM ipconfig /renew *WIFI*
	cecho --07

)

echo.
cecho #Rn done...
sleep 10
echo.

	REM cmdow: aca esconde el cmd window actual, y con sleep lo dejo escondido por 5 minutos
REM cmdow @ /HID
REM sleep 285
 
GOTO LOOP
