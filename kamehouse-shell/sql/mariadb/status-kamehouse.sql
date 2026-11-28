use kamehouse;

select 'KameHouse users' as '';
select id, username, first_name, last_name, email from kamehouse.kamehouse_user order by id desc limit 30;
select count(*) as total from kamehouse.kamehouse_user;

select 'KameHouse roles' as '';
select distinct(name) from kamehouse.kamehouse_role;

select 'ZTV Players' as '';
select id, hostname, port, username from kamehouse_ztvrc.vlc_player order by id desc limit 30;
select count(*) as total from kamehouse_ztvrc.vlc_player;

select 'DragonBall users' as '';
select id, username, email, age, power_level, stamina from kamehouse_testmodule.dragonball_user order by id desc limit 30;
select count(*) as total from kamehouse_testmodule.dragonball_user;

select 'TennisWorld users' as '';
select id, email from kamehouse_tennisworld.tennisworld_user order by id desc limit 30;
select count(*) as total from kamehouse_tennisworld.tennisworld_user;

select 'Booking Schedule Configs' as '';
select id, tennisworld_user_id, enabled, site, session_type, booking_date, day, time, duration from kamehouse_tennisworld.booking_schedule_config order by id desc limit 30;
select count(*) as total from kamehouse_tennisworld.booking_schedule_config;

select 'Booking Requests' as '';
select id, username, site, session_type, date, time, dry_run, duration, scheduled, creation_date from kamehouse_tennisworld.booking_request order by id desc limit 30;
select count(*) as total from kamehouse_tennisworld.booking_request;

select 'Booking Responses' as '';
select id, booking_request_id, status, message from kamehouse_tennisworld.booking_response order by id desc limit 30;
select count(*) as total from kamehouse_tennisworld.booking_response;

select 'Booking Requests Archive' as '';
select id, username, site, session_type, date, time, dry_run, duration, scheduled, creation_date from kamehouse_tennisworld.booking_request_archive order by id desc limit 30;
select count(*) as total from kamehouse_tennisworld.booking_request_archive;

select 'Booking Responses Archive' as '';
select id, booking_request_id, status, message from kamehouse_tennisworld.booking_response_archive order by id desc limit 30;
select count(*) as total from kamehouse_tennisworld.booking_response_archive;

select 'Spring Sessions' as '';
select count(*) as total from kamehouse.SPRING_SESSION;
