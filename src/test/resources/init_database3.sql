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