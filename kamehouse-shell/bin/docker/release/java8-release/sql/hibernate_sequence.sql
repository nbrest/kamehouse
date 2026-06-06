DROP TABLE IF EXISTS `HIBERNATE_SEQUENCE`;
CREATE TABLE `HIBERNATE_SEQUENCE` (
  `next_val` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
INSERT INTO `HIBERNATE_SEQUENCE` (next_val) VALUES (0);

-- lowercase:
DROP TABLE IF EXISTS `hibernate_sequence`;
CREATE TABLE `hibernate_sequence` (
  `next_val` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
INSERT INTO `hibernate_sequence` (next_val) VALUES (0);
