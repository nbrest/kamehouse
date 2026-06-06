DROP TABLE IF EXISTS `DRAGONBALL_USER`;
CREATE TABLE `DRAGONBALL_USER` (
  `ID` bigint(20) NOT NULL,
  `AGE` int(11) DEFAULT NULL,
  `EMAIL` varchar(255) NOT NULL,
  `POWER_LEVEL` int(11) DEFAULT NULL,
  `STAMINA` int(11) DEFAULT NULL,
  `USERNAME` varchar(255) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UK_3n24f6slvjyd3fuejj5b8xs6g` (`EMAIL`),
  UNIQUE KEY `UK_adhkulyb7ai6pugddwlgnc0ei` (`USERNAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `DRAGONBALLUSER`;
CREATE TABLE `DRAGONBALLUSER` (
  `ID` bigint(20) NOT NULL,
  `AGE` int(11) DEFAULT NULL,
  `EMAIL` varchar(255) NOT NULL,
  `POWERLEVEL` int(11) DEFAULT NULL,
  `STAMINA` int(11) DEFAULT NULL,
  `USERNAME` varchar(255) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UK_3n24f6slvjyd3fuejj5b8xsbg` (`EMAIL`),
  UNIQUE KEY `UK_adhkulyb7ai6pugddwlgnc0bi` (`USERNAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- lowercase:
DROP TABLE IF EXISTS `dragonball_user`;
CREATE TABLE `dragonball_user` (
  `ID` bigint(20) NOT NULL,
  `AGE` int(11) DEFAULT NULL,
  `EMAIL` varchar(255) NOT NULL,
  `POWER_LEVEL` int(11) DEFAULT NULL,
  `STAMINA` int(11) DEFAULT NULL,
  `USERNAME` varchar(255) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UK_3n24f6slvjyd3fuejj5b8xszg` (`EMAIL`),
  UNIQUE KEY `UK_adhkulyb7ai6pugddwlgnc0zi` (`USERNAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `dragonballuser`;
CREATE TABLE `dragonballuser` (
  `id` bigint(20) NOT NULL,
  `age` int(11) DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `powerLevel` int(11) DEFAULT NULL,
  `stamina` int(11) DEFAULT NULL,
  `username` varchar(255) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UK_3n24f6slvjyd3fuejj5b8xsxg` (`EMAIL`),
  UNIQUE KEY `UK_adhkulyb7ai6pugddwlgnc0xi` (`USERNAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
