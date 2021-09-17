LOCK TABLES `dragonball_user` WRITE;
/*!40000 ALTER TABLE `dragonball_user` DISABLE KEYS */;
INSERT INTO `dragonball_user` VALUES (15,213,'goku@dbz.com',12,12554,'goku'),(76,2,'gohan@dbz.com',234,232,'gohan'),(77,12111,'vegeta@dbz.com',122,12233,'vegeta'),(393,1121,'bulma@dbz.com',11111,130,'bulma');
/*!40000 ALTER TABLE `dragonball_user` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `kamehouse_role` WRITE;
/*!40000 ALTER TABLE `kamehouse_role` DISABLE KEYS */;
INSERT INTO `kamehouse_role` VALUES (27,'ROLE_KAMISAMA',26),(31,'ROLE_SAIYAJIN',30),(37,'ROLE_NAMEKIAN',36);
/*!40000 ALTER TABLE `kamehouse_role` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `kamehouse_user` WRITE;
/*!40000 ALTER TABLE `kamehouse_user` DISABLE KEYS */;
INSERT INTO `kamehouse_user` VALUES (26,_binary '',_binary '',_binary '','admin@dbz.com',_binary '','admin',NULL,'admin','$2a$12$hppAW21JM7b7AvmFBfBcw.ZkGkVQAmRhRYDkDF0oPe/.NH0qtm97O','admin'),(30,_binary '',_binary '',_binary '','user@dbz.com',_binary '','user',NULL,'user','$2a$12$wsdCKJ8B4hzd37aXaEd9RO.hYn3gntndLiYs9NJ4hCpNEfUOYFtu2','user'),(36,_binary '',_binary '',_binary '','guest@dbz.com',_binary '','guest',NULL,'guest','$2a$12$MN00LJ9tvWNaehwWuPDur.UUwz.OtB2L6X.kS2S8.pMrpwV7kmfQq','guest');
/*!40000 ALTER TABLE `kamehouse_user` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `vlc_player` WRITE;
/*!40000 ALTER TABLE `vlc_player` DISABLE KEYS */;
INSERT INTO `vlc_player` VALUES (10000,'localhost','1',8080,NULL);
/*!40000 ALTER TABLE `vlc_player` ENABLE KEYS */;
UNLOCK TABLES;