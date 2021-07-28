-- ********************************
-- *** Last updated: 2017/09/28 ***
-- ********************************

-- Create user:
CREATE USER kameHouseUser IDENTIFIED BY kameHousePwd;

-- Grant permissions:
GRANT connect, resource TO kameHouseUser;

-- Check my oracle ddl_commands.sql to reset passwords, disable expiry for passwords.

-- Use hibernate to automatically generate the tables.