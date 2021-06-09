-- ********************************
-- *** Last updated: 2017/09/28 ***
-- ********************************

-- Create user:
-- Use '%' instead of 'localhost' to allow to connect remotely with this user
DROP USER IF EXISTS 'kameHouseUser'@'localhost';
CREATE USER 'kameHouseUser'@'localhost' identified by 'kameHousePwd';

-- Grant privileges:
GRANT ALL PRIVILEGES ON kameHouse.* TO 'kameHouseUser'@'localhost';

-- Export table creation script in mysql:
-- show create table dragonball_user;
-- show create table hibernate_sequence;

-- Use hibernate to automatically generate the tables.

