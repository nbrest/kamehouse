SELECT 'Begin executing sql script dump-kamehouse-ztvrc.sql' as '';

DROP TABLE IF EXISTS `vlc_player`;
CREATE TABLE `vlc_player` (
  `ID` bigint(20) NOT NULL,
  `HOSTNAME` varchar(255) NOT NULL,
  `PASSWORD` varchar(255) DEFAULT NULL,
  `PORT` int(11) DEFAULT NULL,
  `USERNAME` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UK_i5fi662e7geiplqi5dr86xk45` (`HOSTNAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `vlc_player` VALUES (1000001,'localhost','1',8080,NULL);

SELECT 'Finished executing sql script dump-kamehouse-ztvrc.sql' as '';
