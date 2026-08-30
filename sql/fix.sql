-- 舞蹈学校数字化管理系统 - 增量数据库修复与扩展脚本 (fix.sql)
SET NAMES utf8mb4;

-- 1. 为 sys_user 系统用户表增加 微信 OpenID 字段
ALTER TABLE sys_user ADD COLUMN open_id VARCHAR(128) DEFAULT '' COMMENT '微信OpenID' AFTER username;

-- 2. 补全 sys_user 表 open_id 的索引（优化查询效率）
ALTER TABLE sys_user ADD INDEX idx_open_id (open_id);

-- 3. 修复中文编码与乱码兼容
ALTER TABLE sys_user CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
