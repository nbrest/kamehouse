use kamehouse;

SET @tableName := 'booking_request';
SET @outputFile := CONCAT(@outFileBase, @tableName, '.tmpcsv');
SET @SQL = CONCAT("SELECT * FROM ", UPPER(@tableName), " INTO OUTFILE '", @outputFile, "' FIELDS TERMINATED BY ','"); 
PREPARE stmt FROM @SQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @tableName := 'booking_response';
SET @outputFile := CONCAT(@outFileBase, @tableName, '.tmpcsv');
SET @SQL = CONCAT("SELECT * FROM ", UPPER(@tableName), " INTO OUTFILE '", @outputFile, "' FIELDS TERMINATED BY ','"); 
PREPARE stmt FROM @SQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;


SET @tableName := 'booking_schedule_config';
SET @outputFile := CONCAT(@outFileBase, @tableName, '.tmpcsv');
SET @SQL = CONCAT("SELECT * FROM ", UPPER(@tableName), " INTO OUTFILE '", @outputFile, "' FIELDS TERMINATED BY ','"); 
PREPARE stmt FROM @SQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @tableName := 'dragonball_user';
SET @outputFile := CONCAT(@outFileBase, @tableName, '.tmpcsv');
SET @SQL = CONCAT("SELECT * FROM ", UPPER(@tableName), " INTO OUTFILE '", @outputFile, "' FIELDS TERMINATED BY ','"); 
PREPARE stmt FROM @SQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @tableName := 'hibernate_sequence';
SET @outputFile := CONCAT(@outFileBase, @tableName, '.tmpcsv');
SET @SQL = CONCAT("SELECT * FROM ", UPPER(@tableName), " INTO OUTFILE '", @outputFile, "' FIELDS TERMINATED BY ','"); 
PREPARE stmt FROM @SQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @tableName := 'kamehouse_role';
SET @outputFile := CONCAT(@outFileBase, @tableName, '.tmpcsv');
SET @SQL = CONCAT("SELECT * FROM ", UPPER(@tableName), " INTO OUTFILE '", @outputFile, "' FIELDS TERMINATED BY ','"); 
PREPARE stmt FROM @SQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @tableName := 'kamehouse_user';
SET @outputFile := CONCAT(@outFileBase, @tableName, '.tmpcsv');
SET @SQL = CONCAT("SELECT * FROM ", UPPER(@tableName), " INTO OUTFILE '", @outputFile, "' FIELDS TERMINATED BY ','"); 
PREPARE stmt FROM @SQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @tableName := 'vlc_player';
SET @outputFile := CONCAT(@outFileBase, @tableName, '.tmpcsv');
SET @SQL = CONCAT("SELECT * FROM ", UPPER(@tableName), " INTO OUTFILE '", @outputFile, "' FIELDS TERMINATED BY ','"); 
PREPARE stmt FROM @SQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;