-- MySQL dump 10.13  Distrib 5.1.47, for unknown-linux-gnu (x86_64)
--
-- Host: localhost    Database: parcels_development
-- ------------------------------------------------------
-- Server version	5.1.47

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `annotations`
--

DROP TABLE IF EXISTS `annotations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `annotations` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `page_id` int(11) DEFAULT NULL,
  `label` varchar(255) DEFAULT NULL,
  `xpath` varchar(1024) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=123 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `annotations`
--

LOCK TABLES `annotations` WRITE;
/*!40000 ALTER TABLE `annotations` DISABLE KEYS */;
INSERT INTO `annotations` VALUES (5,1,'1','/html/body/table[1]/tbody[1]/tr[1]/td[1]/ol[1]/li','2010-07-07 16:41:38','2010-07-07 16:41:38'),(8,6,'Test 3','/html/body/table[1]/tbody[1]/tr[1]/td[1]/ol[1]/li','2010-07-07 21:35:52','2010-07-07 21:35:52'),(9,6,'Test 5','/html/body/table[1]/tbody[1]/tr[1]/td[1]/ol[1]/li','2010-07-07 21:36:02','2010-07-07 21:36:02'),(26,6,'Test 6 (ULs only)','/html/body/table[1]/tbody[1]/tr[1]/td/ul[1]/li','2010-07-09 15:42:20','2010-07-09 15:42:20'),(27,6,'TDs (I think)','/html/body/table[1]/tbody[1]/tr[1]/td','2010-07-09 15:56:22','2010-07-09 15:56:22'),(28,6,'TDs (I think) so','/html/body/table[1]/tbody[1]/tr[1]/td','2010-07-09 15:56:51','2010-07-09 15:56:51'),(30,6,'test','/html/body/table[1]/tbody[1]/tr[1]/td/ol[1]/li','2010-07-09 16:03:50','2010-07-09 16:03:50'),(31,6,'test','/html/body/table[1]/tbody[1]/tr[1]/td/ol[1]/li','2010-07-09 16:04:09','2010-07-09 16:04:09'),(36,6,'LAST ITEM','/html/body/table[1]/tbody[1]/tr[1]/td//li[last()]','2010-07-09 16:08:07','2010-07-09 16:08:07'),(38,6,'Test ','/html/body/table[1]/tbody[1]/tr[1]/td/ul[1]/li','2010-07-10 07:21:47','2010-07-10 07:21:47'),(63,6,'More TESTS','/html/body/table[1]/tbody[1]/tr[1]/td','2010-07-12 07:11:46','2010-07-12 07:11:46'),(65,11,'Label Name','//div[@id=\'ires\']/ol[1]/li[contains(concat(\' \',@class,\' \'),\' g \')]/h3[contains(concat(\' \',@class,\' \'),\' r \')]/a[contains(concat(\' \',@class,\' \'),\' l \')]','2010-07-12 07:44:07','2010-07-12 07:44:07'),(66,11,'search words','//div[@id=\'ires\']/ol[1]/li[contains(concat(\' \',@class,\' \'),\' g \')]/h3[contains(concat(\' \',@class,\' \'),\' r \')]/a[contains(concat(\' \',@class,\' \'),\' l \')]/em','2010-07-12 07:44:50','2010-07-12 07:44:50'),(67,12,'News Links','//ul[@id=\'lev2Menu\']/li/a[1]','2010-07-12 07:48:27','2010-07-12 07:48:27'),(68,12,'Headlines','//div[@id=\'bodyContent\']/div[contains(concat(\' \',@class,\' \'),\' row \')]/div[contains(concat(\' \',@class,\' \'),\' grid_4 \')]/div[contains(concat(\' \',@class,\' \'),\'  \')]/div[contains(concat(\' \',@class,\' \'),\' bC \')]/div[contains(concat(\' \',@class,\' \'),\'  \')]/div[','2010-07-12 07:49:09','2010-07-12 07:49:09'),(69,12,'Label Name','//div[@id=\'bodyContent\']/div[contains(concat(\' \',@class,\' \'),\' row \')]/div[contains(concat(\' \',@class,\' \'),\' grid_4 \')]/div[contains(concat(\' \',@class,\' \'),\'  \')]/div[contains(concat(\' \',@class,\' \'),\' bH \')]/a[1]/h2[1]','2010-07-12 07:54:09','2010-07-12 07:54:09'),(71,12,'Featured Pictures','//div[@id=\'bodyContent\']/div[contains(concat(\' \',@class,\' \'),\' row \')]/div[contains(concat(\' \',@class,\' \'),\' grid_4 \')]/div[contains(concat(\' \',@class,\' \'),\'  \')]/div[contains(concat(\' \',@class,\' \'),\' bC \')]/div[contains(concat(\' \',@class,\' \'),\'  \')]/div[1]/ul[contains(concat(\' \',@class,\' \'),\' newslTop \')]/li[1]/a[1]/img[1]','2010-07-12 08:00:27','2010-07-12 08:00:27'),(72,13,'Headlines','//div[@id=\'main\']/div[1]/div[1]/div[1]/div[1]/div[contains(concat(\' \',@class,\' \'),\' columnGroup \')]/div[contains(concat(\' \',@class,\' \'),\' story \')]/h3[1]/a[1]','2010-07-12 10:46:16','2010-07-12 10:46:16'),(73,14,'Titles','//div[@id=\'wrapper\']/div[1]/div[contains(concat(\' \',@class,\' \'),\' news-summary \')]/div[contains(concat(\' \',@class,\' \'),\' news-body \')]/h3[1]/a[contains(concat(\' \',@class,\' \'),\' offsite \')]','2010-07-12 11:29:57','2010-07-12 11:29:57'),(75,14,'Top In All Topics','//div[@id=\'topten-list\']/div[contains(concat(\' \',@class,\' \'),\' news-summary \')]/h3[1]/a[1]','2010-07-12 12:12:53','2010-07-12 12:12:53'),(76,14,'Sections','//div[@id=\'h-pri\']/ul[1]/li[contains(concat(\' \',@class,\' \'),\' h-drop \')]/a[1]/strong[1]','2010-07-12 12:14:36','2010-07-12 12:14:36'),(77,15,'Latest Headlines','//div[@id=\'firehoselist\']/div[contains(concat(\' \',@class,\' \'),\' fhitem \')]/h3[contains(concat(\' \',@class,\' \'),\' story \')]/span[contains(concat(\' \',@class,\' \'),\' cnin \')]/a[contains(concat(\' \',@class,\' \'),\' datitle \')]','2010-07-12 12:15:34','2010-07-12 12:15:34'),(78,15,'Label Name','//div[@id=\'firehoselist\']/div[contains(concat(\' \',@class,\' \'),\' fhitem \')]/div[contains(concat(\' \',@class,\' \'),\' details \')]/small[1]/a[1]','2010-07-12 12:16:51','2010-07-12 12:16:51'),(79,12,'Headlines','//div[@id=\'bodyContent\']/div[contains(concat(\' \',@class,\' \'),\' row \')]/div[contains(concat(\' \',@class,\' \'),\' grid_4 \')]/div[contains(concat(\' \',@class,\' \'),\'  \')]/div[contains(concat(\' \',@class,\' \'),\' bC \')]/div[contains(concat(\' \',@class,\' \'),\'  \')]/div[1]/ul/li/h3[1]/a[1]','2010-07-12 12:20:20','2010-07-12 12:20:20'),(80,16,'Score','//div[@id=\'fwcMatchHeader\']/div[2]/div[2]','2010-07-12 12:21:34','2010-07-12 12:21:34'),(81,13,'Headlines Test','/html/body/div[2]/div/div/div[1]/div[1]/div[1]/div[1]/div/div//a[1]','2010-07-13 02:42:37','2010-07-13 02:42:37'),(82,13,'Images','//div[@id=\'main\']/div[1]/div[1]/div[1]/div[1]/div[2]/div[contains(concat(\' \',@class,\' \'),\' story \')]/div[contains(concat(\' \',@class,\' \'),\' thumbnail \')]/a[1]/img[1]','2010-07-13 02:47:28','2010-07-13 02:47:28'),(84,17,'Headlines','//div[@id=\'bodyContent\']/div[1]/div[2]/div[1]/ul[1]/li[contains(concat(\' \',@class,\' \'),\'  \')]/h3[1]/a[1]','2010-07-13 03:11:02','2010-07-13 03:11:02'),(85,17,'Summary','//div[@id=\'bodyContent\']/div[1]/div[2]/div[1]/ul[1]/li[contains(concat(\' \',@class,\' \'),\'  \')]/p[3]','2010-07-13 03:12:21','2010-07-13 03:12:21'),(86,18,'Title','//div[@id=\'title\']/h1[1]/span[1]','2010-07-13 05:22:20','2010-07-13 05:22:20'),(87,18,'Author','//p[@id=\'byline\']/a[1]','2010-07-13 05:22:28','2010-07-13 05:22:28'),(88,13,'Label Name','//div[@id=\'main\']/div[1]/div[1]/div[1]/div[1]/div[contains(concat(\' \',@class,\' \'),\' columnGroup \')]/div/p[contains(concat(\' \',@class,\' \'),\' summary \')]','2010-07-13 05:48:48','2010-07-13 05:48:48'),(94,20,'Test','/html/body/table[2]/tbody[1]/tr[1]/td[2]/table[1]/tbody[1]/tr[1]/td[1]/table[3]/tbody[1]/tr[1]/td[1]/table[2]/tbody[1]/tr[1]/td[1]/table[1]/tbody[1]/tr[1]/td[1]/pre[1]','2010-07-13 06:21:46','2010-07-13 06:21:46'),(98,13,'HEadlines with Rejection','/html/body/div[2]/div/div/div[1]/div[1]/div[1]/div[1]/div/div//a[1][not(.=//table[@id=\'TwoWeekCalendar-ab\']/tbody[1]/tr/td/a[1])][not(.=//div[@id=\'main\']/div[1]/div[1]/div[1]/div[1]/div[contains(concat(\' \',@class,\' \'),\' columnGroup \')]/div/ul[contains(concat(\' \',@class,\' \'),\' refer \')]/li[last()]/span[contains(concat(\' \',@class,\' \'),\' commentCountLink \')]/a[1])][not(.=//div[@id=\'main\']/div[1]/div[1]/div[1]/div[1]/div[2]/div[contains(concat(\' \',@class,\' \'),\' story \')]/ul[contains(concat(\' \',@class,\' \'),\' refer \')]/li/a[1])]','2010-07-13 08:01:44','2010-07-13 08:05:59'),(100,13,'Headlines Improved','/html/body/div[2]/div/div/div[1]/div[1]/div[1]/div[1]/div/div//a[1])[not(.=(//div[@id=\'main\']/div[1]/div[1]/div[1]/div[1]/div[2]/div[6]/div[1]/a[1] | //div[@id=\'main\']/div[1]/div[1]/div[1]/div[1]/div[contains(concat(\' \',@class,\' \'),\' columnGroup \')]/div/ul[1]/li/a[1] | //table[@id=\'TwoWeekCalendar-ab\']/tbody[1]/tr/td/a[1]))]','2010-07-14 13:13:27','2010-07-14 13:31:14'),(102,13,'Headlines Improved III','(/html/body/div[2]/div/div/div[1]/div[1]/div[1]/div[1]/div/div//a[1])[not(.=(//div[@id=\'main\']/div[1]/div[1]/div[1]/div[1]/div[contains(concat(\' \',@class,\' \'),\' columnGroup \')]/div/ul[1]/li/a[1] | //table[@id=\'TwoWeekCalendar-ab\']/tbody[1]/tr/td/a[1]))]','2010-07-14 13:44:57','2010-07-14 13:44:57'),(103,26,'Top Stories Headlines','//div[@id=\'basecolour_bn\']/table[1]/tbody[1]/tr[1]/td[2]/div[1]/div[3]/div[2]/h1/a[1]','2010-07-14 13:55:42','2010-07-14 13:55:42'),(104,26,'Top Stories Summary','//div[@id=\'basecolour_bn\']/table[1]/tbody[1]/tr[1]/td[2]/div[1]/div[3]/div[2]/p','2010-07-14 13:56:09','2010-07-14 13:56:09'),(105,26,'Top Stories Headlines','//div[@id=\'basecolour_bn\']/table[1]/tbody[1]/tr[1]/td[2]/div[1]/div[3]/div[2]//a[1]','2010-07-14 13:56:30','2010-07-14 13:56:30'),(106,13,'Headlines WITH REJECTION!!!!! 4','(/html/body/div[2]/div/div/div[1]/div[1]/div[1]/div[1]/div/div//a[1])[not(.=(//div[@id=\'main\']/div[1]/div[1]/div[1]/div[1]/div[2]/div[contains(concat(\' \',@class,\' \'),\' story \')]/ul[1]/li/a[1] | //div[@id=\'main\']/div[1]/div[1]/div[1]/div[1]/div[2]/div[8]/ul[1]/li[1]/span[1]/a[1] | //table[@id=\'TwoWeekCalendar-ab\']/tbody[1]/tr/td/a[1]))]','2010-07-15 02:29:52','2010-07-15 02:29:52'),(107,30,'Top Stories','//div[@id=\'basecolour_bn\']/table[1]/tbody[1]/tr[1]/td[2]/div[1]/div[3]/div[2]//a[1]','2010-07-15 02:34:58','2010-07-15 02:34:58'),(108,6,'Test','/html/body/table[1]/tbody[1]/tr[1]/td[2]/ul[1]/li','2010-07-15 04:07:11','2010-07-15 04:07:11'),(109,32,'Add','(//div[@id=\'sidebar\']/div[5]/div[1]/div[contains(concat(\' \',@class,\' \'),\' spacer \')]/a[1])[not(.=(//div[@id=\'sidebar\']/div[5]/div[1]/div[19]/a[1]))]','2010-07-15 06:29:17','2010-07-15 06:29:17'),(110,NULL,'test','//div[@id=\'content\']/div[1]/h2','2010-07-18 14:40:35','2010-07-18 14:40:35'),(111,36,'Test','//div[@id=\'content\']/div[1]/h2','2010-07-18 14:44:20','2010-07-18 14:44:20'),(112,38,'Music News headlines','(/html/body/center[1]/div[1]/table[3]/tbody[1]/tr[1]/td[1]/table[1]/tbody[1]/tr[1]/td[2]/table/tbody[1]/tr[last()]/td/table[1]/tbody[1]/tr/td[1]/a[1])[not(.=(/html/body/center[1]/div[1]/table[3]/tbody[1]/tr[1]/td[1]/table[1]/tbody[1]/tr[1]/td[2]/table[1]/tbody[1]/tr[1]/td[1]/table[1]/tbody[1]/tr[6]/td[1]/a[1]))]','2010-07-19 06:37:56','2010-07-19 06:37:56'),(113,39,'Posters','//form[@id=\'quickModForm\']/div[contains(concat(\' \',@class,\' \'),\' bordercolor \')]/div[1]/div[1]/h4[1]/a[1]','2010-07-19 07:55:30','2010-07-19 07:55:30'),(114,41,'Last Items','/html/body/table[1]/tbody[1]/tr[1]/td//li[last()]','2010-07-19 07:55:51','2010-07-19 07:55:51'),(115,46,'Shirt Number','//div[@id=\'playerProfile\']/div[2]/div[1]/ul[1]/li[3]','2010-07-29 05:15:53','2010-07-29 05:15:53'),(116,47,'Digg Headlines','//div[@id=\'wrapper\']/div[1]/div[contains(concat(\' \',@class,\' \'),\' news-summary \')]/div[1]/h3[1]/a[1]','2010-07-30 02:17:42','2010-07-30 02:17:42'),(117,48,'Label Name','','2010-07-30 03:04:01','2010-07-30 03:04:01'),(118,48,'OldSkool digg','//div[@id=\'wrapper\']/div[4]/div[contains(concat(\' \',@class,\' \'),\' news-summary \')]/div[1]/h3[1]/a[1]','2010-07-30 03:04:09','2010-07-30 03:04:09'),(119,49,'Another oldskool digg','//div[@id=\'contents\']/div[contains(concat(\' \',@class,\' \'),\' news-summary \')]/div[1]/h3[1]/a[1]','2010-07-30 03:06:58','2010-07-30 03:06:58'),(120,49,'Quite oldskool digg','//div[@id=\'contents\']/div[contains(concat(\' \',@class,\' \'),\' news-summary \')]/div[1]/h3[1]/a[1]','2010-07-30 03:31:11','2010-07-30 03:31:11'),(121,50,'test','//div[@id=\'contents\']/div[contains(concat(\' \',@class,\' \'),\' news-summary \')]/div[1]/h3[1]/a[1]','2010-07-30 03:32:59','2010-07-30 03:32:59'),(122,52,'Shirt Number','//div[@id=\'playerProfile\']/div[2]/div[1]/div[2]/div[1]/ul[1]/li[3]','2010-07-30 07:20:45','2010-07-30 07:20:45');
/*!40000 ALTER TABLE `annotations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pages`
--

DROP TABLE IF EXISTS `pages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pages` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=55 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pages`
--

LOCK TABLES `pages` WRITE;
/*!40000 ALTER TABLE `pages` DISABLE KEYS */;
INSERT INTO `pages` VALUES (35,'Hello','http://www.google.com','2010-07-18 13:39:54','2010-08-05 17:46:01',1),(36,'Class: ActiveRecord::Base','http://api.rubyonrails.org/classes/ActiveRecord/Base.html','2010-07-18 14:43:57','2010-08-05 17:42:35',1),(37,'International News - The New York Times','http://www.nytimes.com/pages/world/index.html','2010-07-19 03:51:40','2010-08-05 17:46:55',1),(38,'Music News @ Ultimate-Guitar.Com','http://www.ultimate-guitar.com/news/','2010-07-19 06:37:24','2010-08-05 17:47:28',1),(39,'May Green tileset','http://www.bay12forums.com/smf/index.php?topic=48165.0','2010-07-19 07:45:29','2010-08-05 17:46:15',1),(40,'Tutorials - Dwarf Fortress Wiki','http://df.magmawiki.com/index.php/Tutorials','2010-07-19 07:49:06','2010-08-05 17:45:09',1),(41,'Title','file:///home/shawn/Desktop/Title.html','2010-07-19 07:54:10','2010-08-05 17:45:05',1),(42,'Jot Thought » Blog Archive » HTTP Basic Authentication using restful_authentication with Rails 1.2','http://jotthought.com/articles/2007/09/27/http-basic-authentication-using-restful_authentication-with-rails-12/','2010-07-19 08:27:57','2010-08-05 17:47:25',1),(46,'FIFA.com - 2010 FIFA World Cup™ - Thomas MUELLER','http://www.fifa.com/worldcup/players/player=321722/profile.html','2010-07-29 05:15:45','2010-08-05 17:47:02',1),(47,'Digg - The Latest News Headlines, Videos and Images','http://digg.com/','2010-07-30 02:17:12','2010-08-05 17:43:04',1),(48,'digg / News','http://web.archive.org/web/20061230142750/http://digg.com/','2010-07-30 03:03:16','2010-08-05 17:45:22',1),(49,'digg','http://web.archive.org/web/20060206024847/digg.com/','2010-07-30 03:06:48','2010-08-05 17:47:05',1),(51,'Digg - The Latest News Headlines, Videos and Images','http://digg.com/page2','2010-07-30 06:59:27','2010-08-05 17:43:05',1),(52,'FIFA.com - 2010 FIFA World Cup™ - AHN Jung Hwan','http://www.fifa.com/worldcup/players/player=156216/index.html','2010-07-30 07:20:32','2010-08-05 17:45:12',1),(53,'FIFA.com - 2010 FIFA World Cup™ - Thomas MUELLER','http://www.fifa.com/worldcup/players/player=321722/index.html','2010-07-30 07:21:20','2010-08-05 17:45:10',1),(54,'FIFA.com - 2010 FIFA World Cup™ - Andres INIESTA','http://www.fifa.com/worldcup/players/player=183857/index.html','2010-07-30 07:21:26','2010-08-05 17:45:11',1);
/*!40000 ALTER TABLE `pages` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `revisions`
--

DROP TABLE IF EXISTS `revisions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `revisions` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `html` longtext,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `page_id` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1670 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `revisions`
--

LOCK TABLES `revisions` WRITE;
/*!40000 ALTER TABLE `revisions` DISABLE KEYS */;
/*!40000 ALTER TABLE `revisions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `schema_migrations`
--

DROP TABLE IF EXISTS `schema_migrations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `schema_migrations` (
  `version` varchar(255) NOT NULL,
  UNIQUE KEY `unique_schema_migrations` (`version`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `schema_migrations`
--

LOCK TABLES `schema_migrations` WRITE;
/*!40000 ALTER TABLE `schema_migrations` DISABLE KEYS */;
INSERT INTO `schema_migrations` VALUES ('20100513134504'),('20100513134621'),('20100513135119'),('20100514113256'),('20100514113346'),('20100518144453'),('20100518144647'),('20100518144936'),('20100518145027'),('20100718120041'),('20100718122212'),('20100722035134');
/*!40000 ALTER TABLE `schema_migrations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `login` varchar(40) DEFAULT NULL,
  `name` varchar(100) DEFAULT '',
  `email` varchar(100) DEFAULT NULL,
  `crypted_password` varchar(40) DEFAULT NULL,
  `salt` varchar(40) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `remember_token` varchar(40) DEFAULT NULL,
  `remember_token_expires_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_users_on_login` (`login`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'shawntan','','shawn@wtf.sg','158df267942db4393ad2cdf8f1f101ec810a3245','fca7c2814f7888214cf7413df54778334a131b7a','2010-07-18 12:15:47','2010-07-18 12:15:47',NULL,NULL);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2010-08-05 22:08:47
