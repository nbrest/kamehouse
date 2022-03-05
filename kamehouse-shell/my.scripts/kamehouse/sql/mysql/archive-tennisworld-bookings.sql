-- ********************************
-- *** Last updated: 2022/03/05 ***
-- ********************************

-- Archive tennisworld booking responses

CREATE TABLE IF NOT EXISTS booking_response_archive LIKE booking_response;

SELECT COUNT(*) FROM booking_response;
SELECT COUNT(*) FROM booking_response_archive;

INSERT INTO booking_response_archive
SELECT * FROM booking_response where booking_request_id in (SELECT id FROM booking_request WHERE date < NOW() - INTERVAL 15 DAY);
DELETE FROM booking_response where booking_request_id in (SELECT id FROM booking_request WHERE date < NOW() - INTERVAL 15 DAY);

SELECT COUNT(*) FROM booking_response;
SELECT COUNT(*) FROM booking_response_archive;

-- Archive tennisworld booking requests

CREATE TABLE IF NOT EXISTS booking_request_archive LIKE booking_request;

SELECT COUNT(*) FROM booking_request;
SELECT COUNT(*) FROM booking_request_archive;

INSERT INTO booking_request_archive
SELECT * FROM booking_request WHERE date < NOW() - INTERVAL 15 DAY;

DELETE FROM booking_request WHERE date < NOW() - INTERVAL 15 DAY;

SELECT COUNT(*) FROM booking_request;
SELECT COUNT(*) FROM booking_request_archive;
