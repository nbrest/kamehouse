-- ********************************
-- *** Initial schema split migration ***
-- ********************************

SELECT 'Begin executing sql script migrate-kamehouse-schemas.sql' as '';

SET FOREIGN_KEY_CHECKS=0;

CREATE SCHEMA IF NOT EXISTS kamehouse_tennisworld;
CREATE SCHEMA IF NOT EXISTS kamehouse_ztvrc;
CREATE SCHEMA IF NOT EXISTS kamehouse_testmodule;

GRANT ALL PRIVILEGES ON kamehouse_tennisworld.* TO 'kamehouse'@'%';
GRANT ALL PRIVILEGES ON kamehouse_ztvrc.* TO 'kamehouse'@'%';
GRANT ALL PRIVILEGES ON kamehouse_testmodule.* TO 'kamehouse'@'%';

-- TennisWorld domain data.
CREATE TABLE IF NOT EXISTS kamehouse_tennisworld.tennisworld_user LIKE kamehouse.tennisworld_user;
INSERT IGNORE INTO kamehouse_tennisworld.tennisworld_user SELECT * FROM kamehouse.tennisworld_user;
CREATE TABLE IF NOT EXISTS kamehouse_tennisworld.tennisworld_user_SEQ LIKE kamehouse.tennisworld_user_SEQ;
INSERT IGNORE INTO kamehouse_tennisworld.tennisworld_user_SEQ SELECT * FROM kamehouse.tennisworld_user_SEQ;
CREATE TABLE IF NOT EXISTS kamehouse_tennisworld.booking_request LIKE kamehouse.booking_request;
INSERT IGNORE INTO kamehouse_tennisworld.booking_request SELECT * FROM kamehouse.booking_request;
CREATE TABLE IF NOT EXISTS kamehouse_tennisworld.booking_request_SEQ LIKE kamehouse.booking_request_SEQ;
INSERT IGNORE INTO kamehouse_tennisworld.booking_request_SEQ SELECT * FROM kamehouse.booking_request_SEQ;
CREATE TABLE IF NOT EXISTS kamehouse_tennisworld.booking_response LIKE kamehouse.booking_response;
INSERT IGNORE INTO kamehouse_tennisworld.booking_response SELECT * FROM kamehouse.booking_response;
CREATE TABLE IF NOT EXISTS kamehouse_tennisworld.booking_response_SEQ LIKE kamehouse.booking_response_SEQ;
INSERT IGNORE INTO kamehouse_tennisworld.booking_response_SEQ SELECT * FROM kamehouse.booking_response_SEQ;
CREATE TABLE IF NOT EXISTS kamehouse_tennisworld.booking_schedule_config LIKE kamehouse.booking_schedule_config;
INSERT IGNORE INTO kamehouse_tennisworld.booking_schedule_config SELECT * FROM kamehouse.booking_schedule_config;
CREATE TABLE IF NOT EXISTS kamehouse_tennisworld.booking_schedule_config_SEQ LIKE kamehouse.booking_schedule_config_SEQ;
INSERT IGNORE INTO kamehouse_tennisworld.booking_schedule_config_SEQ SELECT * FROM kamehouse.booking_schedule_config_SEQ;

-- ZtvRc domain data.
CREATE TABLE IF NOT EXISTS kamehouse_ztvrc.vlc_player LIKE kamehouse.vlc_player;
INSERT IGNORE INTO kamehouse_ztvrc.vlc_player SELECT * FROM kamehouse.vlc_player;
CREATE TABLE IF NOT EXISTS kamehouse_ztvrc.vlc_player_SEQ LIKE kamehouse.vlc_player_SEQ;
INSERT IGNORE INTO kamehouse_ztvrc.vlc_player_SEQ SELECT * FROM kamehouse.vlc_player_SEQ;
CREATE TABLE IF NOT EXISTS kamehouse_ztvrc.ztv_player_SEQ LIKE kamehouse.ztv_player_SEQ;
INSERT IGNORE INTO kamehouse_ztvrc.ztv_player_SEQ SELECT * FROM kamehouse.ztv_player_SEQ;

-- TestModule domain data.
CREATE TABLE IF NOT EXISTS kamehouse_testmodule.dragonball_user LIKE kamehouse.dragonball_user;
INSERT IGNORE INTO kamehouse_testmodule.dragonball_user SELECT * FROM kamehouse.dragonball_user;
CREATE TABLE IF NOT EXISTS kamehouse_testmodule.dragonball_user_SEQ LIKE kamehouse.dragonball_user_SEQ;
INSERT IGNORE INTO kamehouse_testmodule.dragonball_user_SEQ SELECT * FROM kamehouse.dragonball_user_SEQ;

SET FOREIGN_KEY_CHECKS=1;

SELECT 'kamehouse_tennisworld' schema_name, COUNT(*) table_count
FROM information_schema.tables WHERE table_schema = 'kamehouse_tennisworld'
UNION ALL
SELECT 'kamehouse_ztvrc', COUNT(*) FROM information_schema.tables
WHERE table_schema = 'kamehouse_ztvrc'
UNION ALL
SELECT 'kamehouse_testmodule', COUNT(*) FROM information_schema.tables
WHERE table_schema = 'kamehouse_testmodule';

SELECT 'Finished executing sql script migrate-kamehouse-schemas.sql' as '';
