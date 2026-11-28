use kamehouse;

SET @tableName := 'tennisworld_booking_request';
SET @sourceTable := 'kamehouse_tennisworld.booking_request';
SET @outputFile := CONCAT(@outFileBase, @tableName, '.tmpcsv');
SET @SQL = CONCAT("SELECT * FROM ", @sourceTable, " INTO OUTFILE '", @outputFile, "' FIELDS TERMINATED BY ','");
PREPARE stmt FROM @SQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @tableName := 'tennisworld_booking_response';
SET @sourceTable := 'kamehouse_tennisworld.booking_response';
SET @outputFile := CONCAT(@outFileBase, @tableName, '.tmpcsv');
SET @SQL = CONCAT("SELECT * FROM ", @sourceTable, " INTO OUTFILE '", @outputFile, "' FIELDS TERMINATED BY ','");
PREPARE stmt FROM @SQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @tableName := 'tennisworld_booking_schedule_config';
SET @sourceTable := 'kamehouse_tennisworld.booking_schedule_config';
SET @outputFile := CONCAT(@outFileBase, @tableName, '.tmpcsv');
SET @SQL = CONCAT("SELECT * FROM ", @sourceTable, " INTO OUTFILE '", @outputFile, "' FIELDS TERMINATED BY ','");
PREPARE stmt FROM @SQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @tableName := 'tennisworld_user';
SET @sourceTable := 'kamehouse_tennisworld.tennisworld_user';
SET @outputFile := CONCAT(@outFileBase, @tableName, '.tmpcsv');
SET @SQL = CONCAT("SELECT * FROM ", @sourceTable, " INTO OUTFILE '", @outputFile, "' FIELDS TERMINATED BY ','");
PREPARE stmt FROM @SQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @tableName := 'testmodule_dragonball_user';
SET @sourceTable := 'kamehouse_testmodule.dragonball_user';
SET @outputFile := CONCAT(@outFileBase, @tableName, '.tmpcsv');
SET @SQL = CONCAT("SELECT * FROM ", @sourceTable, " INTO OUTFILE '", @outputFile, "' FIELDS TERMINATED BY ','");
PREPARE stmt FROM @SQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @tableName := 'kamehouse_role';
SET @sourceTable := 'kamehouse.kamehouse_role';
SET @outputFile := CONCAT(@outFileBase, @tableName, '.tmpcsv');
SET @SQL = CONCAT("SELECT * FROM ", @sourceTable, " INTO OUTFILE '", @outputFile, "' FIELDS TERMINATED BY ','");
PREPARE stmt FROM @SQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @tableName := 'kamehouse_user';
SET @sourceTable := 'kamehouse.kamehouse_user';
SET @outputFile := CONCAT(@outFileBase, @tableName, '.tmpcsv');
SET @SQL = CONCAT("SELECT * FROM ", @sourceTable, " INTO OUTFILE '", @outputFile, "' FIELDS TERMINATED BY ','");
PREPARE stmt FROM @SQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @tableName := 'ztvrc_vlc_player';
SET @sourceTable := 'kamehouse_ztvrc.vlc_player';
SET @outputFile := CONCAT(@outFileBase, @tableName, '.tmpcsv');
SET @SQL = CONCAT("SELECT * FROM ", @sourceTable, " INTO OUTFILE '", @outputFile, "' FIELDS TERMINATED BY ','");
PREPARE stmt FROM @SQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
