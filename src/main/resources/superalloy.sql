/*
Navicat MySQL Data Transfer

Source Server         : yzy
Source Server Version : 50717
Source Host           : localhost:3306
Source Database       : sp1

Target Server Type    : MYSQL
Target Server Version : 50717
File Encoding         : 65001

Date: 2023-01-09 15:02:39
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for data_patent
-- ----------------------------
DROP TABLE IF EXISTS `data_patent`;
CREATE TABLE `data_patent` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL,
  `titleE` varchar(255) DEFAULT NULL,
  `author` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL,
  `authorE` varchar(255) DEFAULT NULL,
  `publish_date` varchar(255) DEFAULT NULL,
  `keywords` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL,
  `keywordsE` varchar(255) DEFAULT NULL,
  `l_abstract` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL,
  `l_abstractE` varchar(255) DEFAULT NULL,
  `patent_type` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL,
  `patent_institute` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL,
  `patent_number` varchar(255) DEFAULT NULL,
  `patent_region` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL,
  `file_attachment` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of data_patent
-- ----------------------------
