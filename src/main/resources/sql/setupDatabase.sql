-- Create schema:
CREATE SCHEMA mobileInsp;
-- Use schema:
USE mobileInsp;

-- Create user:
CREATE USER mobileInsp@localhost identified by 'mobileInspPwd';

-- Grant privileges:
GRANT ALL PRIVILEGES ON mobileInsp.* TO 'mobileInsp'@'localhost' IDENTIFIED BY 'mobileInspPwd';

-- Drop user:
DROP USER mobileInsp@localhost;
-- Drop schema:
DROP SCHEMA mobileInsp;

-- Query user permissions:
SELECT * FROM mysql.user;
