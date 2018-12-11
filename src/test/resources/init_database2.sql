-- 创建用户表
Drop Table IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) DEFAULT NULL COMMENT '用户名',
  `password` varchar(255) DEFAULT NULL COMMENT '密码',
  `status` int(3) DEFAULT 1 COMMENT '账号状态',
  `userface` varchar(255) DEFAULT NULL COMMENT '用户头像',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY(`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 创建文件类别表
Drop Table IF EXISTS `doc_category`;
CREATE TABLE `doc_category` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL COMMENT '文件类别名称',
  `code` varchar(64) COMMENT '文件类别代码',
  PRIMARY KEY(`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 创建文件表
Drop Table IF EXISTS `doc`;
CREATE TABLE `doc` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `docname` varchar(255) NOT NULL COMMENT '文件名',
  `download_url` varchar(500) NOT NULL COMMENT '下载地址',
  `preview_url` varchar(500) COMMENT '预览地址',
  `upload_userid` int(11) NOT NULL COMMENT '上传者id',
  `date` DATETIME NOT NULL COMMENT '上传时间',
  `downloads` int(11) DEFAULT 0 COMMENT '下载次数',
  `category_id` int(11) NOT NULL COMMENT '文件所属类别',
  `type` varchar(255) NOT NULL COMMENT '文件类型',
  `open` int(2) NOT NULL COMMENT '该文件是否公开，1代表公开，0代表需要特定用户访问，需要查找文件权限表',
  INDEX `upload_index` (`upload_userid`),
  INDEX `category_index` (`category_id`),
  PRIMARY KEY(`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- 创建文件权限表
-- 查询时，查询所有公开文件加入到返回队列，之后再查询所有需要权限的文件，对于每个需要权限的文件在文件权限表中获得其具体需要的权限，
-- 如 文件1 销售部 经理， 文件1 研发部，即销售部经理以及研发部所有员工能查看，如果当前用户的权限满足就将该文件加入返回队列
Drop Table IF EXISTS `doc_permission`;
CREATE TABLE `doc_permission` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `doc_id` int(11) NOT NULL COMMENT '文件id',
  `departmentrole_id` varchar(255) NOT NULL COMMENT '部门角色id',
  PRIMARY KEY(`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;
