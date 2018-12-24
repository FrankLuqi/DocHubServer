-- 设置部门角色id的目的，一个用户可能同时具有多个部门角色，如生产部员工 开发部员工
-- 文件同理 可能会同时允许多个部门角色查看
-- 而在返回用户信息或者文件信息时为了减少数据库的读取，会将用户当前的用户所有部门角色放置于用户表中（反规范化）
-- 所以要设置部门角色id
-- 创建角色表，方便对角色进行管理（如添加角色及删除角色）
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) DEFAULT NULL COMMENT '角色名称',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 创建用户表
Drop Table IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) DEFAULT NULL COMMENT '用户名',
  `password` varchar(255) DEFAULT NULL COMMENT '密码',
  `status` int(3) DEFAULT 1 COMMENT '账号状态',
  `userface` varchar(255) DEFAULT NULL COMMENT '用户头像',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `change_date` DATETIME COMMENT '最近修改时间时间',
  PRIMARY KEY(`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 用户部门角色表 用户在什么部门是什么角色
DROP TABLE IF EXISTS `owned_role`;
CREATE TABLE `owned_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL COMMENT'用户id',
  `department_role_id` varchar(50) NOT NULL COMMENT'用户部门角色id',
  `remark` varchar(255) COMMENT'备注',
  PRIMARY KEY(`id`),
  INDEX `user_id_index` (`user_id`),
  INDEX `role_id_index` (`department_role_id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 部门表
DROP TABLE IF EXISTS `departments`;
CREATE TABLE `departments` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) DEFAULT NULL COMMENT '部门名称',
  `code` varchar(64) DEFAULT NULL COMMENT '部门层级代码',
  `remark` varchar(255) COMMENT '备注',
  PRIMARY KEY(`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 功能表 包含网站页面、页面中的菜单项、按钮
DROP TABLE IF EXISTS `function`;
CREATE TABLE `function` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `category` int(3) NOT NULL COMMENT '功能类型 1是网站页面 2是具体功能 如删除用户',
  `father_node` int(11) COMMENT '父节点id',
  `name` varchar(50) COMMENT '功能名称',
  `url` varchar(255) COMMENT '功能连接地址',
  `identity` varchar(255) COMMENT '标识该功能对应的component或按钮的名称',
  `status` int(2) COMMENT '功能当前状态 1代表可用 0代表不可用或正在维护',
  PRIMARY KEY(`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 功能权限表
DROP TABLE IF EXISTS `function_permission`;
CREATE TABLE `function_permission` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `function_id` int(11) NOT NULL COMMENT '功能id',
  `department_role_id` varchar(50) NOT NULL COMMENT'用户部门角色id',
  `permission` int(2) COMMENT '是否具有权限 1代表有 0代表没有',
  `remark` varchar(255) COMMENT '备注',
  PRIMARY KEY(`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;
