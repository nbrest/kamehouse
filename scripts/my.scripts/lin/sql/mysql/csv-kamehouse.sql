use kameHouse;

SELECT * FROM kamehouse_role INTO OUTFILE '/tmp/kamehouse_role.tmpcsv' FIELDS TERMINATED BY ',';
SELECT * FROM kamehouse_user INTO OUTFILE '/tmp/kamehouse_user.tmpcsv' FIELDS TERMINATED BY ','; 
SELECT * FROM dragonball_user INTO OUTFILE '/tmp/dragonball_user.tmpcsv' FIELDS TERMINATED BY ',';
SELECT * FROM hibernate_sequence INTO OUTFILE '/tmp/hibernate_sequence.tmpcsv' FIELDS TERMINATED BY ',';
SELECT * FROM vlc_player INTO OUTFILE '/tmp/vlc_player.tmpcsv' FIELDS TERMINATED BY ',';