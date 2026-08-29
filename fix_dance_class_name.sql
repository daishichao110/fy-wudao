SET NAMES utf8mb4;
USE wudao_db;

-- 修复 sys_user 表中的 dance_class_name 乱码与映射
UPDATE sys_user SET dance_class_name = '全校全局管理' WHERE user_id = 1787400000000000002;
UPDATE sys_user SET dance_class_name = '全校全局管理' WHERE user_id = 1 OR username = 'admin';
UPDATE sys_user SET dance_class_name = '全校全局管理' WHERE role_type = 'TEACHER';
UPDATE sys_user SET dance_class_name = '三年级' WHERE user_id = 3;
UPDATE sys_user SET dance_class_name = '二年级' WHERE dance_class_name LIKE '%ä%' OR dance_class_name LIKE '%å%';

-- 修复 duty_schedule 表中的 dance_class_name 乱码
UPDATE duty_schedule SET dance_class_name = '二年级' WHERE dance_class_name LIKE '%ä%' OR dance_class_name LIKE '%å%';

-- 修复 sys_work_group 表中的 dance_class_name 乱码
UPDATE sys_work_group SET dance_class_name = '二年级' WHERE dance_class_name LIKE '%ä%' OR dance_class_name LIKE '%å%';

-- 修复 volunteer_task 表中的 dance_class_name 乱码
UPDATE volunteer_task SET dance_class_name = '二年级' WHERE dance_class_name LIKE '%ä%' OR dance_class_name LIKE '%å%';
