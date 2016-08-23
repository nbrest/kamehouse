-- ********************************
-- *** Last updated: 2016/08/21 ***
-- ********************************

-- Create user:
CREATE USER mobileInspUser IDENTIFIED BY mobileInspPwd;

-- Grant permissions:
GRANT connect, resource TO mobileInspUser;
