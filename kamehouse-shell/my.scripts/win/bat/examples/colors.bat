@echo off
set /p letra=Color de letra?
if %letra%==negro (color 00 && set var=0)
if %letra%==rojo (color 04 && set var=4)
if %letra%==azul (color 01 && set var=1)
if %letra%==verde (color 02 && set var=2)
if %letra%==purpura (color 05 && set var=5)
if %letra%==amarillo (color 06 && set var=6)
if %letra%==blanco (color 07 && set var=7)
set /p fondo=Color de fondo?
if %fondo%==negro (color 0%var%)
if %fondo%==rojo (color 4%var%)
if %fondo%==azul (color 1%var%)
if %fondo%==verde (color 2%var%)
if %fondo%==purpura (color 5%var%)
if %fondo%==amarillo (color 6%var%)
if %fondo%==blanco (color 7%var%)

pause