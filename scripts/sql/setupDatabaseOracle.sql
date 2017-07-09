-- ********************************
-- *** Last updated: 2017/07/02 ***
-- ********************************

-- Create user:
CREATE USER baseAppUser IDENTIFIED BY baseAppPwd;

-- Grant permissions:
GRANT connect, resource TO baseAppUser;
