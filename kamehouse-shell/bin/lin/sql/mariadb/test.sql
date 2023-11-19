-- execute using: mariadb> \. test.sql
-- or: shell> mariadb db_name < text_file (if use db_name; not 1st line in sql file)
-- or: shell> mariadb < text_file

use test;

DROP PROCEDURE IF EXISTS test;

DELIMITER //
CREATE PROCEDURE test()

   BEGIN
-- DECLARE VARIABLES --------------------------------------
-- parece que en mariadb no puedo setear variables con @ adentro de un SP

      DECLARE goku INT;
      DECLARE x  INT;
      DECLARE str  VARCHAR(255);

-- SET VARIABLES ------------------------------------------
      SELECT '-----------------------------------------------';

      SET x = 1;
      SET str =  '';

-- WHILE --------------------------------------------------

      WHILE x  <= 5 DO
                  SET  str = CONCAT(str,x,',');
                  SET  x = x + 1;
      END WHILE;

-- SELECT TO SHOW OUTPUT ----------------------------------

      SELECT str;
      
      -- describe movies;
      
      select goku;

      set goku = 10;
      
      select goku;
      
-- IF ------------------------------------------------------ 
      SELECT '-----------------------------------------------';

      IF  goku = 10 THEN 
            select 'IF1: goku = 10', goku;
      ELSE
            select 'IF1: goku <> 10', goku;
      END IF;

      SET goku = -3;
      
      SELECT '-----------------------------------------------';

      IF  goku = 10 THEN 
            select 'IF2: goku = 10', goku;
      ELSE
            select 'IF2: goku <> 10', goku;
      END IF;
      
      SELECT '-----------------------------------------------';
      
   END //
DELIMITER ;

 CALL test();

-- exit;
