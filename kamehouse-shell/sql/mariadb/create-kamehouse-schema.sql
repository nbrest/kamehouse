-- ********************************
-- *** Last updated: 2023/12/19 ***
-- ********************************

SELECT 'Begin executing sql script create-kamehouse-schema.sql' as '';

-- Create schema:
DROP SCHEMA IF EXISTS kamehouse;
CREATE SCHEMA kamehouse;

-- Use schema:
USE kamehouse;

-- Export table creation script in mariadb:
-- show create table dragonball_user;
-- show create table hibernate_sequence;

-- Use hibernate to automatically generate the tables.

SELECT 'Finished executing sql script create-kamehouse-schema.sql' as '';