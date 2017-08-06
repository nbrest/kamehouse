-- ********************************
-- *** Last updated: 2017/07/02 ***
-- ********************************

-- Create user:
CREATE USER kameHouseUser IDENTIFIED BY kameHousePwd;

-- Grant permissions:
GRANT connect, resource TO kameHouseUser;
