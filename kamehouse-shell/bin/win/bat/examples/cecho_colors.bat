@echo OFF

REM *** cecho colors begin **********************************************

REM cecho #Bn Blue #Rn Red #Gn Green #Yn Yellow #Cn Cyan #Mn Magenta #Wn White #Nn Normal
REM cecho #bn Blue #rn Red #gn Green #yn Yellow #cn Cyan #mn Magenta #wn Gray

REM #~ : Restore original color
REM #; : Don´t end with new line character

REM *** cecho colors end **********************************************

cecho #Bn Blue #Rn Red #Gn Green #Yn Yellow #Cn Cyan #Mn Magenta #Wn White #Nn Normal

cecho #bn Blue #rn Red #gn Green #yn Yellow #cn Cyan #mn Magenta #wn Gray

cecho normal #Bn blue goku #~ normal goku
cecho normal #Bn blue goku #; 
echo NO new-line goku

pause