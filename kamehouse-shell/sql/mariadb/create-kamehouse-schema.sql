-- ********************************
-- *** Last updated: 2023/12/19 ***
-- ********************************

SELECT 'Begin executing sql script create-kamehouse-schema.sql' as '';

-- Create schemas:
DROP SCHEMA IF EXISTS kamehouse;
DROP SCHEMA IF EXISTS kamehouse_tennisworld;
DROP SCHEMA IF EXISTS kamehouse_ztvrc;
DROP SCHEMA IF EXISTS kamehouse_testmodule;
CREATE SCHEMA kamehouse;
CREATE SCHEMA kamehouse_tennisworld;
CREATE SCHEMA kamehouse_ztvrc;
CREATE SCHEMA kamehouse_testmodule;

-- Use the identity schema for scripts that don't fully qualify their tables:
USE kamehouse;

-- Export table creation script in mariadb:
-- show create table dragonball_user;
-- show create table hibernate_sequence;

-- Use hibernate to automatically generate the tables.

SELECT 'Finished executing sql script create-kamehouse-schema.sql' as '';
