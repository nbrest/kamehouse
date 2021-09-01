use kamehouse;

SELECT * FROM BOOKING_REQUEST INTO OUTFILE 'C:\\Users\\nbrest\\home-synced\\mysql\\csv\\booking_request.tmpcsv' FIELDS TERMINATED BY ',';
SELECT * FROM BOOKING_RESPONSE INTO OUTFILE 'C:\\Users\\nbrest\\home-synced\\mysql\\csv\\booking_response.tmpcsv' FIELDS TERMINATED BY ',';
SELECT * FROM BOOKING_SCHEDULE_CONFIG INTO OUTFILE 'C:\\Users\\nbrest\\home-synced\\mysql\\csv\\booking_schedule_config.tmpcsv' FIELDS TERMINATED BY ',';

SELECT * FROM DRAGONBALL_USER INTO OUTFILE 'C:\\Users\\nbrest\\home-synced\\mysql\\csv\\dragonball_user.tmpcsv' FIELDS TERMINATED BY ',';

SELECT * FROM HIBERNATE_SEQUENCE INTO OUTFILE 'C:\\Users\\nbrest\\home-synced\\mysql\\csv\\hibernate_sequence.tmpcsv' FIELDS TERMINATED BY ',';

SELECT * FROM KAMEHOUSE_ROLE INTO OUTFILE 'C:\\Users\\nbrest\\home-synced\\mysql\\csv\\kamehouse_role.tmpcsv' FIELDS TERMINATED BY ',';
SELECT * FROM KAMEHOUSE_USER INTO OUTFILE 'C:\\Users\\nbrest\\home-synced\\mysql\\csv\\kamehouse_user.tmpcsv' FIELDS TERMINATED BY ','; 

SELECT * FROM VLC_PLAYER INTO OUTFILE 'C:\\Users\\nbrest\\home-synced\\mysql\\csv\\vlc_player.tmpcsv' FIELDS TERMINATED BY ','; 