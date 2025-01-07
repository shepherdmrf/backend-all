/*
Navicat MySQL Data Transfer

Source Server         : yzy
Source Server Version : 50717
Source Host           : localhost:3306
Source Database       : xai1

Target Server Type    : MYSQL
Target Server Version : 50717
File Encoding         : 65001

Date: 2023-01-09 14:06:41
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of data_patent
-- ----------------------------

-- ----------------------------
-- Table structure for parameter_info
-- ----------------------------
DROP TABLE IF EXISTS `parameter_info`;
CREATE TABLE `parameter_info` (
  `para_type_en` varchar(255) DEFAULT NULL COMMENT '参数类型-英',
  `para_type_cn` varchar(255) DEFAULT NULL COMMENT '参数类型-中',
  `para_value` varchar(32) DEFAULT NULL COMMENT '参数值',
  `para_info` varchar(255) DEFAULT NULL COMMENT '参数信息'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='参数信息表';

-- ----------------------------
-- Records of parameter_info
-- ----------------------------

-- ----------------------------
-- Table structure for spring_session
-- ----------------------------
DROP TABLE IF EXISTS `spring_session`;
CREATE TABLE `spring_session` (
  `PRIMARY_ID` char(36) NOT NULL,
  `SESSION_ID` char(36) NOT NULL,
  `CREATION_TIME` bigint(20) NOT NULL,
  `LAST_ACCESS_TIME` bigint(20) NOT NULL,
  `MAX_INACTIVE_INTERVAL` int(11) NOT NULL,
  `EXPIRY_TIME` bigint(20) NOT NULL,
  `PRINCIPAL_NAME` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`PRIMARY_ID`),
  UNIQUE KEY `SPRING_SESSION_IX1` (`SESSION_ID`),
  KEY `SPRING_SESSION_IX2` (`EXPIRY_TIME`),
  KEY `SPRING_SESSION_IX3` (`PRINCIPAL_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of spring_session
-- ----------------------------

-- ----------------------------
-- Table structure for spring_session_attributes
-- ----------------------------
DROP TABLE IF EXISTS `spring_session_attributes`;
CREATE TABLE `spring_session_attributes` (
  `SESSION_PRIMARY_ID` char(36) NOT NULL,
  `ATTRIBUTE_NAME` varchar(200) NOT NULL,
  `ATTRIBUTE_BYTES` blob NOT NULL,
  PRIMARY KEY (`SESSION_PRIMARY_ID`,`ATTRIBUTE_NAME`),
  CONSTRAINT `SPRING_SESSION_ATTRIBUTES_FK` FOREIGN KEY (`SESSION_PRIMARY_ID`) REFERENCES `spring_session` (`PRIMARY_ID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of spring_session_attributes
-- ----------------------------

-- ----------------------------
-- Table structure for sys_log_info
-- ----------------------------
DROP TABLE IF EXISTS `sys_log_info`;
CREATE TABLE `sys_log_info` (
  `op_time` varchar(255) DEFAULT NULL COMMENT '操作时间',
  `op_user` varchar(255) DEFAULT NULL COMMENT '操作用户',
  `op_type` varchar(255) DEFAULT NULL COMMENT '操作种类',
  `op_describe` varchar(255) DEFAULT NULL COMMENT '操作描述',
  `op_platform_id` varchar(255) DEFAULT NULL COMMENT '操作所属平台编号',
  `user_id` varchar(32) DEFAULT NULL COMMENT '操作人编号',
  `op_url` varchar(255) DEFAULT NULL COMMENT '请求url'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sys_log_info
-- ----------------------------

-- ----------------------------
-- Table structure for sys_menu_info
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu_info`;
CREATE TABLE `sys_menu_info` (
  `menu_id` varchar(10) DEFAULT NULL COMMENT '菜单编号',
  `menu_level` varchar(2) DEFAULT NULL COMMENT '菜单层级',
  `menu_name` varchar(255) DEFAULT NULL COMMENT '菜单名',
  `menu_father_id` varchar(10) DEFAULT NULL COMMENT '父菜单ID，一级菜单为0',
  `menu_active` varchar(1) DEFAULT NULL COMMENT '1激活0停用',
  `menu_path` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='菜单信息表';

-- ----------------------------
-- Records of sys_menu_info
-- ----------------------------
INSERT INTO `sys_menu_info` VALUES ('1', '1', '系统管理', '0', '1', '');
INSERT INTO `sys_menu_info` VALUES ('11', '2', '平台管理', '1', '1', '/platformManage');
INSERT INTO `sys_menu_info` VALUES ('12', '2', '用户管理', '1', '1', '/userManage');
INSERT INTO `sys_menu_info` VALUES ('13', '2', '用户信息', '1', '1', '/userInfo');
INSERT INTO `sys_menu_info` VALUES ('14', '2', '系统日志', '1', '1', '/logInfo');
INSERT INTO `sys_menu_info` VALUES ('15', '2', '菜单管理', '1', '1', '/menuManage');
INSERT INTO `sys_menu_info` VALUES ('16', '2', '操作管理', '1', '1', '/opManage');
INSERT INTO `sys_menu_info` VALUES ('2', '1', '样例demo', '0', '1', '');
INSERT INTO `sys_menu_info` VALUES ('21', '2', '专利信息', '2', '1', '/patentInfo');
INSERT INTO `sys_menu_info` VALUES ('22', '2', '专利新增', '2', '1', '/addPatent');
INSERT INTO `sys_menu_info` VALUES ('23', '2', '文件上传', '2', '1', '/fileUpload');

-- ----------------------------
-- Table structure for sys_op_info
-- ----------------------------
DROP TABLE IF EXISTS `sys_op_info`;
CREATE TABLE `sys_op_info` (
  `op_url` varchar(255) DEFAULT NULL COMMENT '操作请求url',
  `op_type` varchar(255) DEFAULT NULL COMMENT '操作分属模块、操作类型',
  `op_describe` varchar(255) DEFAULT NULL COMMENT '操作简述'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='操作信息表';

-- ----------------------------
-- Records of sys_op_info
-- ----------------------------
INSERT INTO `sys_op_info` VALUES ('/user/login', 'user', '登录');
INSERT INTO `sys_op_info` VALUES ('/user/getMenuData', 'user', '获取目录信息');
INSERT INTO `sys_op_info` VALUES ('/user/getUserInfo', 'user', '查看用户信息');
INSERT INTO `sys_op_info` VALUES ('/user/changeUserInfo', 'user', '更改用户信息');
INSERT INTO `sys_op_info` VALUES ('/user/resetPassword', 'user', '重置密码');
INSERT INTO `sys_op_info` VALUES ('/user/getPlatformInfo', 'user', '查看平台信息');
INSERT INTO `sys_op_info` VALUES ('/user/platformManagers', 'user', '查看平台管理员');
INSERT INTO `sys_op_info` VALUES ('/user/changePlatformDetail', 'user', '更改平台信息');
INSERT INTO `sys_op_info` VALUES ('/user/changePlatformStatus', 'user', '更改用户状态');
INSERT INTO `sys_op_info` VALUES ('/user/addNewPlatform', 'user', '新增平台');
INSERT INTO `sys_op_info` VALUES ('/user/changeUserStatus', 'user', '更改用户状态');
INSERT INTO `sys_op_info` VALUES ('/user/addNewManager', 'user', '新增平台管理员');
INSERT INTO `sys_op_info` VALUES ('/user/getOpLogInfo', 'user', '查看用户操作日志');
INSERT INTO `sys_op_info` VALUES ('/user/signOut', 'user', '登出');
INSERT INTO `sys_op_info` VALUES ('/user/getMenuManageData', 'user', '查看菜单管理');
INSERT INTO `sys_op_info` VALUES ('/user/addNewMenuMeb', 'user', '新增子菜单');
INSERT INTO `sys_op_info` VALUES ('/user/changeMenuStatus', 'user', '更改菜单状态');
INSERT INTO `sys_op_info` VALUES ('/user/getOPInfo', 'user', '查看操作功能信息');
INSERT INTO `sys_op_info` VALUES ('/user/addNewOP', 'user', '新增操作功能信息');
INSERT INTO `sys_op_info` VALUES ('/user/getPlatformMenu', 'user', '查询平台可访问目录数据');
INSERT INTO `sys_op_info` VALUES ('/user/updatePlatformMenu', 'user', '更新平台可访问目录数据');
INSERT INTO `sys_op_info` VALUES ('/user/getUserList', 'user', '查询平台普通用户列表');
INSERT INTO `sys_op_info` VALUES ('/user/addNewUser', 'user', '新增平台普通用户');
INSERT INTO `sys_op_info` VALUES ('/user/getUserMenu', 'user', '查询平台普通用户可访问目录');
INSERT INTO `sys_op_info` VALUES ('/user/updateUserMenu', 'user', '更新平台普通用户可访问目录');
INSERT INTO `sys_op_info` VALUES ('/user/addNewFMenuMeb', 'user', '新增一级菜单');
INSERT INTO `sys_op_info` VALUES ('/demo/getPatentData', 'demo', '查看专利信息');
INSERT INTO `sys_op_info` VALUES ('/demo/addNewPatent', 'demo', '新增专利信息');
INSERT INTO `sys_op_info` VALUES ('/demo/selectFileUpload', 'demo', '文件上传');

-- ----------------------------
-- Table structure for sys_platform_info
-- ----------------------------
DROP TABLE IF EXISTS `sys_platform_info`;
CREATE TABLE `sys_platform_info` (
  `platform_id` varchar(32) DEFAULT NULL COMMENT '平台编号',
  `platform_name` varchar(255) DEFAULT NULL COMMENT '平台名称',
  `platform_detail` varchar(255) DEFAULT NULL COMMENT '平台详情',
  `platform_active` varchar(1) DEFAULT NULL COMMENT '0停1用',
  `platform_url` varchar(255) DEFAULT NULL COMMENT '平台链接',
  `platform_abbr` varchar(255) DEFAULT NULL COMMENT '平台英文简称'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='平台信息表';

-- ----------------------------
-- Records of sys_platform_info
-- ----------------------------
INSERT INTO `sys_platform_info` VALUES ('0', '开发机构（管理平台）', '上海大学刘悦老师课题组', '1', 'http://localhost:7081/', 'manage');
INSERT INTO `sys_platform_info` VALUES ('74564f1fc64845a0b155677308bda370', '平台1', '测试平台', '1', '', 'test');

-- ----------------------------
-- Table structure for sys_puser_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_puser_menu`;
CREATE TABLE `sys_puser_menu` (
  `user_id` varchar(32) DEFAULT NULL COMMENT '统一认证号',
  `menu_id` varchar(10) DEFAULT NULL COMMENT '菜单号'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户菜单对照表\r\n平台普通用户可访问模块菜单';

-- ----------------------------
-- Records of sys_puser_menu
-- ----------------------------
INSERT INTO `sys_puser_menu` VALUES ('a1f44f1b6c13473ba4375469fcef73d7', '1');
INSERT INTO `sys_puser_menu` VALUES ('a1f44f1b6c13473ba4375469fcef73d7', '13');
INSERT INTO `sys_puser_menu` VALUES ('a1f44f1b6c13473ba4375469fcef73d7', '2');
INSERT INTO `sys_puser_menu` VALUES ('a1f44f1b6c13473ba4375469fcef73d7', '21');
INSERT INTO `sys_puser_menu` VALUES ('0de7fed291bd4b969a16c8a096ab4dce', '1');
INSERT INTO `sys_puser_menu` VALUES ('0de7fed291bd4b969a16c8a096ab4dce', '13');
INSERT INTO `sys_puser_menu` VALUES ('0de7fed291bd4b969a16c8a096ab4dce', '2');
INSERT INTO `sys_puser_menu` VALUES ('0de7fed291bd4b969a16c8a096ab4dce', '21');

-- ----------------------------
-- Table structure for sys_role_info
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_info`;
CREATE TABLE `sys_role_info` (
  `role_id` varchar(32) DEFAULT NULL COMMENT '角色编号',
  `role_name` varchar(255) DEFAULT NULL COMMENT '角色名'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='角色信息表';

-- ----------------------------
-- Records of sys_role_info
-- ----------------------------
INSERT INTO `sys_role_info` VALUES ('1', '开发人员');
INSERT INTO `sys_role_info` VALUES ('2', '平台管理员');
INSERT INTO `sys_role_info` VALUES ('3', '平台普通用户');

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
  `role_id` varchar(2) DEFAULT NULL COMMENT '角色号',
  `menu_id` varchar(10) DEFAULT NULL COMMENT '菜单号',
  `role_platform_id` varchar(32) DEFAULT NULL COMMENT '平台编号'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='角色菜单对照表\r\n开发人员（课题组负责人） 可 平台管理-平台管理员管理-可访问模块（即修改平台可用模块）\r\n平台管理人员 可 用户管理-可访问模块（即修改普通用户可访问模块）';

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
INSERT INTO `sys_role_menu` VALUES ('1', '1', '0');
INSERT INTO `sys_role_menu` VALUES ('1', '11', '0');
INSERT INTO `sys_role_menu` VALUES ('1', '13', '0');
INSERT INTO `sys_role_menu` VALUES ('1', '14', '0');
INSERT INTO `sys_role_menu` VALUES ('1', '15', '0');
INSERT INTO `sys_role_menu` VALUES ('1', '16', '0');
INSERT INTO `sys_role_menu` VALUES ('1', '2', '0');
INSERT INTO `sys_role_menu` VALUES ('1', '21', '0');
INSERT INTO `sys_role_menu` VALUES ('1', '22', '0');
INSERT INTO `sys_role_menu` VALUES ('1', '23', '0');
INSERT INTO `sys_role_menu` VALUES ('2', '1', '74564f1fc64845a0b155677308bda370');
INSERT INTO `sys_role_menu` VALUES ('2', '12', '74564f1fc64845a0b155677308bda370');
INSERT INTO `sys_role_menu` VALUES ('2', '13', '74564f1fc64845a0b155677308bda370');
INSERT INTO `sys_role_menu` VALUES ('2', '14', '74564f1fc64845a0b155677308bda370');
INSERT INTO `sys_role_menu` VALUES ('2', '2', '74564f1fc64845a0b155677308bda370');
INSERT INTO `sys_role_menu` VALUES ('2', '21', '74564f1fc64845a0b155677308bda370');
INSERT INTO `sys_role_menu` VALUES ('2', '22', '74564f1fc64845a0b155677308bda370');
INSERT INTO `sys_role_menu` VALUES ('2', '23', '74564f1fc64845a0b155677308bda370');
INSERT INTO `sys_role_menu` VALUES ('3', '1', '74564f1fc64845a0b155677308bda370');
INSERT INTO `sys_role_menu` VALUES ('3', '13', '74564f1fc64845a0b155677308bda370');
INSERT INTO `sys_role_menu` VALUES ('3', '2', '74564f1fc64845a0b155677308bda370');
INSERT INTO `sys_role_menu` VALUES ('3', '21', '74564f1fc64845a0b155677308bda370');

-- ----------------------------
-- Table structure for sys_user_info
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_info`;
CREATE TABLE `sys_user_info` (
  `user_id` varchar(32) NOT NULL COMMENT '唯一标识',
  `login_no` varchar(20) DEFAULT NULL COMMENT '登录认证号(唯一)',
  `user_name` varchar(30) DEFAULT NULL COMMENT '用户名',
  `password` varchar(32) DEFAULT NULL COMMENT '密码',
  `role_id` varchar(32) DEFAULT NULL COMMENT '角色号',
  `user_info` varchar(255) DEFAULT NULL COMMENT '用户信息',
  `user_platform_id` varchar(32) DEFAULT NULL COMMENT '归属平台编号',
  `user_active` varchar(1) DEFAULT NULL COMMENT '1-激活，0-注销',
  `occupation` varchar(32) DEFAULT NULL COMMENT '职业',
  `email` varchar(255) DEFAULT NULL COMMENT '邮箱',
  `telephone` varchar(20) DEFAULT NULL COMMENT '电话',
  `research_domain` varchar(255) DEFAULT NULL COMMENT '研究领域',
  `research_direction` varchar(255) DEFAULT NULL COMMENT '研究方向',
  `work_organization` varchar(255) DEFAULT NULL COMMENT '工作单位',
  `create_time` varchar(255) DEFAULT NULL COMMENT '创建时间',
  `country_id` varchar(255) DEFAULT NULL COMMENT '国家编号',
  `province_id` varchar(255) DEFAULT NULL COMMENT '二级行政区-州-省等编号',
  `city_id` varchar(255) DEFAULT NULL COMMENT '城市编号',
  `login_ip` varchar(255) DEFAULT NULL COMMENT '登录时的ip',
  `ip_region` varchar(255) DEFAULT NULL COMMENT 'ip对应的地区',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户信息表';

-- ----------------------------
-- Records of sys_user_info
-- ----------------------------
INSERT INTO `sys_user_info` VALUES ('0f85e390adaa40d8b0b0679987adfbc4', '111', 'test manager', 'e10adc3949ba59abbe56e057f20f883e', '2', '测试平台管理员', '74564f1fc64845a0b155677308bda370', '1', '', '123@shu.com', '', '', '', '上海大学', '2022-07-05 22:02:41', '156', '310000', '310100', null, null);
INSERT INTO `sys_user_info` VALUES ('a1f44f1b6c13473ba4375469fcef73d7', '110', 'test user', '698d51a19d8a121ce581499d7b701668', '3', '', '74564f1fc64845a0b155677308bda370', '1', '', 'yuziyip@shu.edu.cn', '', '', '', '上海大学', '2022-07-05 22:02:41', '156', '310000', '310100', null, null);
INSERT INTO `sys_user_info` VALUES ('faaf39f8c3c846898f52fcda1b97a535', '123', 'yuziyi', '698d51a19d8a121ce581499d7b701668', '1', '开发者', '0', '1', '', 'yuziyishu@163.com', '18717781630', '', '', '上海大学', '2022-07-05 22:02:41', '156', '310000', '310100', null, null);
