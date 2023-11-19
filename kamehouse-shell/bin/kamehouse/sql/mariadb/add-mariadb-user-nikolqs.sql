SELECT 'Begin executing sql script' as '';

SELECT 'Dropping user nikolqs' as '';
DROP USER IF EXISTS nikolqs;

SELECT 'Creating user nikolqs' as '';
SET @SQL = CONCAT("CREATE USER nikolqs@'%' identified by '", @nikoLqsPass, "'"); 
PREPARE stmt FROM @SQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SELECT 'Granting permissions to user nikolqs' as '';
GRANT ALL PRIVILEGES ON *.* TO 'nikolqs'@'%';

SELECT 'Finished executing sql script' as '';