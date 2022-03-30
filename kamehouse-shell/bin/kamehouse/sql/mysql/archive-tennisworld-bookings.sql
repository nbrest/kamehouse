-- ********************************
-- *** Last updated: 2022/03/05 ***
-- ********************************

-- Start transaction
SELECT 'Starting transaction' as '';
SET autocommit = OFF;
START TRANSACTION;

-- Show initial state
SELECT '----------- Initial state -----------' as '';
SELECT COUNT(*) as booking_response_count FROM booking_response;
SELECT '-------------------------------' as '';
SELECT COUNT(*) as booking_response_archive_count FROM booking_response_archive;
SELECT '-------------------------------' as '';
SELECT COUNT(*) as booking_request_count FROM booking_request;
SELECT '-------------------------------' as '';
SELECT COUNT(*) as booking_request_archive_count FROM booking_request_archive;
SELECT '-------------------------------' as '';

-- Archive tennisworld booking responses
CREATE TABLE IF NOT EXISTS booking_response_archive LIKE booking_response;

INSERT INTO booking_response_archive
SELECT * FROM booking_response where booking_request_id in (SELECT id FROM booking_request WHERE date < NOW() - INTERVAL 5 DAY);

DELETE FROM booking_response where booking_request_id in (SELECT id FROM booking_request WHERE date < NOW() - INTERVAL 5 DAY);

-- Archive tennisworld booking requests
CREATE TABLE IF NOT EXISTS booking_request_archive LIKE booking_request;

INSERT INTO booking_request_archive
SELECT * FROM booking_request WHERE date < NOW() - INTERVAL 5 DAY;

DELETE FROM booking_request WHERE date < NOW() - INTERVAL 5 DAY;

-- Show final state
SELECT '----------- Final state -----------' as '';
SELECT COUNT(*) as booking_response_count FROM booking_response;
SELECT '-------------------------------' as '';
SELECT COUNT(*) as booking_response_archive_count FROM booking_response_archive;
SELECT '-------------------------------' as '';
SELECT COUNT(*) as booking_request_count FROM booking_request;
SELECT '-------------------------------' as '';
SELECT COUNT(*) as booking_request_archive_count FROM booking_request_archive;
SELECT '-------------------------------' as '';

-- Commit changes
SELECT 'Committing changes' as '';
SELECT '' as '';
COMMIT;
SET autocommit = ON;