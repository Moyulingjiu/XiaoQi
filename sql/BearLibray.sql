/*
SQLyog Community v13.1.7 (64 bit)
MySQL - 8.0.27-0ubuntu0.20.04.1 : Database - xiaoqi
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`xiaoqi` /*!40100 DEFAULT CHARACTER SET utf8 */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `xiaoqi`;

/*Table structure for table `abstract` */

DROP TABLE IF EXISTS `abstract`;

CREATE TABLE `abstract` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `text` varchar(500) DEFAULT NULL,
  `type` tinyint DEFAULT '0',
  `modified` datetime DEFAULT NULL,
  `modified_id` bigint DEFAULT NULL,
  `create` datetime DEFAULT NULL,
  `create_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

/*Table structure for table `blacklist` */

DROP TABLE IF EXISTS `blacklist`;

CREATE TABLE `blacklist` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `key` bigint DEFAULT NULL COMMENT 'QQ号或者群号',
  `type` tinyint DEFAULT '0' COMMENT '0：qq号；1：群号',
  `comment` varchar(200) DEFAULT NULL COMMENT '备注',
  `remind` bigint DEFAULT '0',
  `last_remind_time` datetime DEFAULT NULL,
  `valid` tinyint DEFAULT '1',
  `modified` datetime DEFAULT NULL,
  `modified_id` bigint DEFAULT NULL,
  `create` datetime DEFAULT NULL,
  `create_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

/*Table structure for table `bot` */

DROP TABLE IF EXISTS `bot`;

CREATE TABLE `bot` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `qq` bigint DEFAULT NULL COMMENT '机器人QQ号',
  `password` varchar(200) DEFAULT NULL COMMENT '机器人密码',
  `name` varchar(20) DEFAULT NULL COMMENT '机器人名字',
  `master_qq` bigint DEFAULT NULL,
  `key_id` bigint DEFAULT NULL COMMENT '授权码id',
  `allow_friend` int DEFAULT '0',
  `allow_group` int DEFAULT '0',
  `heart` int DEFAULT '1',
  `heart_interval` int DEFAULT '6',
  `remind_friend` int DEFAULT '1',
  `remind_group` int DEFAULT '0',
  `remind_mute` int DEFAULT '1',
  `remind_quit` int DEFAULT '1',
  `clear_blacklist` int DEFAULT '0',
  `modified` datetime DEFAULT NULL,
  `modified_id` bigint DEFAULT NULL,
  `create` datetime DEFAULT NULL,
  `create_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb3;

/*Table structure for table `drifting_bottle` */

DROP TABLE IF EXISTS `drifting_bottle`;

CREATE TABLE `drifting_bottle` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `text` varchar(200) DEFAULT NULL,
  `valid` tinyint DEFAULT '1',
  `permanment` tinyint DEFAULT '0',
  `modified` datetime DEFAULT NULL,
  `modified_id` bigint DEFAULT NULL,
  `create` datetime DEFAULT NULL,
  `create_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

/*Table structure for table `fortune` */

DROP TABLE IF EXISTS `fortune`;

CREATE TABLE `fortune` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `text` varchar(500) DEFAULT NULL,
  `modified` datetime DEFAULT NULL,
  `modified_id` bigint DEFAULT NULL,
  `create` datetime DEFAULT NULL,
  `create_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

/*Table structure for table `group` */

DROP TABLE IF EXISTS `group`;

CREATE TABLE `group` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `group_id` bigint DEFAULT NULL,
  `name` varchar(50) DEFAULT NULL,
  `modified` datetime DEFAULT NULL,
  `modified_id` bigint DEFAULT NULL,
  `create` datetime DEFAULT NULL,
  `create_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

/*Table structure for table `idiom` */

DROP TABLE IF EXISTS `idiom`;

CREATE TABLE `idiom` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `text` varchar(50) DEFAULT NULL,
  `modified` datetime DEFAULT NULL,
  `modified_id` bigint DEFAULT NULL,
  `create` datetime DEFAULT NULL,
  `create_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

/*Table structure for table `key` */

DROP TABLE IF EXISTS `key`;

CREATE TABLE `key` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `key` varchar(50) DEFAULT NULL,
  `valid_begin_date` datetime DEFAULT NULL,
  `valid_end_date` datetime DEFAULT NULL,
  `type` tinyint DEFAULT '0',
  `modified` datetime DEFAULT NULL,
  `modified_id` bigint DEFAULT NULL,
  `create` datetime DEFAULT NULL,
  `create_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb3;

/*Table structure for table `questions_answers` */

DROP TABLE IF EXISTS `questions_answers`;

CREATE TABLE `questions_answers` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `question` varchar(200) DEFAULT NULL,
  `additional` varchar(200) DEFAULT NULL,
  `anaswer` varchar(200) DEFAULT NULL,
  `modified` datetime DEFAULT NULL,
  `modified_id` bigint DEFAULT NULL,
  `create` datetime DEFAULT NULL,
  `create_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

/*Table structure for table `riddle` */

DROP TABLE IF EXISTS `riddle`;

CREATE TABLE `riddle` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `question` varchar(200) DEFAULT NULL,
  `anaswer` varchar(200) DEFAULT NULL,
  `modified` datetime DEFAULT NULL,
  `modified_id` bigint DEFAULT NULL,
  `create` datetime DEFAULT NULL,
  `create_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

/*Table structure for table `user` */

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `qq` bigint DEFAULT NULL,
  `password` varchar(50) DEFAULT NULL,
  `last_change_password` datetime DEFAULT NULL,
  `nickname` varchar(50) DEFAULT NULL,
  `use_nickname` int DEFAULT '0',
  `right` tinyint DEFAULT '2',
  `point` int DEFAULT '0',
  `luck` int DEFAULT '50',
  `last_luck` datetime DEFAULT NULL,
  `modified` datetime DEFAULT NULL,
  `modified_id` bigint DEFAULT NULL,
  `create` datetime DEFAULT NULL,
  `create_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb3;

/*Table structure for table `vocabulary` */

DROP TABLE IF EXISTS `vocabulary`;

CREATE TABLE `vocabulary` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `word` varchar(200) DEFAULT NULL,
  `part` varchar(20) DEFAULT NULL,
  `meaning` varchar(200) DEFAULT NULL,
  `type` tinyint DEFAULT '5',
  `modified` datetime DEFAULT NULL,
  `modified_id` bigint DEFAULT NULL,
  `create` datetime DEFAULT NULL,
  `create_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
