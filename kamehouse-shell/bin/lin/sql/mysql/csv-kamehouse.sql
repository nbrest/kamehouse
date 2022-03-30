use kameHouse;

SELECT * FROM booking_request INTO OUTFILE '/tmp/booking_request.tmpcsv' FIELDS TERMINATED BY ',';
SELECT * FROM booking_response INTO OUTFILE '/tmp/booking_response.tmpcsv' FIELDS TERMINATED BY ',';
SELECT * FROM booking_schedule_config INTO OUTFILE '/tmp/booking_schedule_config.tmpcsv' FIELDS TERMINATED BY ',';

SELECT * FROM dragonball_user INTO OUTFILE '/tmp/dragonball_user.tmpcsv' FIELDS TERMINATED BY ',';

SELECT * FROM hibernate_sequence INTO OUTFILE '/tmp/hibernate_sequence.tmpcsv' FIELDS TERMINATED BY ',';

SELECT * FROM kamehouse_role INTO OUTFILE '/tmp/kamehouse_role.tmpcsv' FIELDS TERMINATED BY ',';
SELECT * FROM kamehouse_user INTO OUTFILE '/tmp/kamehouse_user.tmpcsv' FIELDS TERMINATED BY ','; 

SELECT * FROM vlc_player INTO OUTFILE '/tmp/vlc_player.tmpcsv' FIELDS TERMINATED BY ',';