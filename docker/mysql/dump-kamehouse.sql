DROP TABLE IF EXISTS `kamehouse_user`;
CREATE TABLE `kamehouse_user` (
  `ID` bigint(20) NOT NULL,
  `ACCOUNT_NON_EXPIRED` bit(1) DEFAULT NULL,
  `ACCOUNT_NON_LOCKED` bit(1) DEFAULT NULL,
  `CREDENTIALS_NON_EXPIRED` bit(1) DEFAULT NULL,
  `EMAIL` varchar(255) NOT NULL,
  `ENABLED` bit(1) DEFAULT NULL,
  `FIRST_NAME` varchar(255) DEFAULT NULL,
  `LAST_LOGIN` datetime DEFAULT NULL,
  `LAST_NAME` varchar(255) DEFAULT NULL,
  `PASSWORD` varchar(255) DEFAULT NULL,
  `USERNAME` varchar(255) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UK_qs207y3hr8dp1w83npduvo8v3` (`EMAIL`),
  UNIQUE KEY `UK_g1flpfg6b2v863t5535afr494` (`USERNAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `kamehouse_user` VALUES (26,_binary '',_binary '',_binary '','admin@dbz.com',_binary '','admin',NULL,'admin','$2a$12$hppAW21JM7b7AvmFBfBcw.ZkGkVQAmRhRYDkDF0oPe/.NH0qtm97O','admin'),(30,_binary '',_binary '',_binary '','user@dbz.com',_binary '','user',NULL,'user','$2a$12$wsdCKJ8B4hzd37aXaEd9RO.hYn3gntndLiYs9NJ4hCpNEfUOYFtu2','user'),(36,_binary '',_binary '',_binary '','guest@dbz.com',_binary '','guest',NULL,'guest','$2a$12$MN00LJ9tvWNaehwWuPDur.UUwz.OtB2L6X.kS2S8.pMrpwV7kmfQq','guest');

DROP TABLE IF EXISTS `kamehouse_role`;
CREATE TABLE `kamehouse_role` (
  `ID` bigint(20) NOT NULL,
  `NAME` varchar(255) NOT NULL,
  `kamehouse_user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK80l3gq9u4ggdd2qqoyl8ll1c4` (`kamehouse_user_id`),
  CONSTRAINT `FK1aug04ikis5or4g40omwb5586` FOREIGN KEY (`kamehouse_user_id`) REFERENCES `kamehouse_user` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `kamehouse_role` VALUES (27,'ROLE_KAMISAMA',26),(28,'ROLE_SAIYAJIN',26),(31,'ROLE_SAIYAJIN',30),(37,'ROLE_NAMEKIAN',36);
