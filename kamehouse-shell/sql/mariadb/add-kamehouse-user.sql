SELECT 'Begin executing sql script add-kamehouse-user.sql' as '';

SELECT 'Dropping user kamehouse' as '';
DROP USER IF EXISTS kamehouse;

SELECT 'Creating user kamehouse' as '';
SET @SQL = CONCAT("CREATE USER kamehouse@'%' identified by '", @kameHousePass, "'"); 
PREPARE stmt FROM @SQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- REVOKE ALL PRIVILEGES ON *.* FROM 'kamehouse';
SELECT 'Granting permissions to user kamehouse' as '';
GRANT ALL PRIVILEGES ON kamehouse.* TO 'kamehouse'@'%';

SELECT 'Finished executing sql script add-kamehouse-user.sql' as '';
