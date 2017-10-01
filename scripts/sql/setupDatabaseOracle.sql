-- ********************************
-- *** Last updated: 2017/09/28 ***
-- ********************************

-- Create user:
CREATE USER kameHouseUser IDENTIFIED BY kameHousePwd;

-- Grant permissions:
GRANT connect, resource TO kameHouseUser;

-- Use hibernate to automatically generate the tables.