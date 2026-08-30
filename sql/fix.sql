-- 舞蹈学校数字化管理系统 - 全量数据库 ID 字符化及增量迁移脚本 (fix.sql)
SET NAMES utf8mb4;
USE wudao_db;

-- 1. sys_user
ALTER TABLE sys_user MODIFY COLUMN user_id VARCHAR(64) COMMENT '用户ID';

-- 2. dance_schedule
ALTER TABLE dance_schedule MODIFY COLUMN schedule_id VARCHAR(64);
ALTER TABLE dance_schedule MODIFY COLUMN teacher_id VARCHAR(64);

-- 3. leave_make_up
ALTER TABLE leave_make_up MODIFY COLUMN record_id VARCHAR(64);
ALTER TABLE leave_make_up MODIFY COLUMN student_id VARCHAR(64);
ALTER TABLE leave_make_up MODIFY COLUMN schedule_id VARCHAR(64);

-- 4. student_body_metric
ALTER TABLE student_body_metric MODIFY COLUMN metric_id VARCHAR(64);
ALTER TABLE student_body_metric MODIFY COLUMN student_id VARCHAR(64);

-- 5. volunteer_task
ALTER TABLE volunteer_task MODIFY COLUMN task_id VARCHAR(64);

-- 6. volunteer_enrollment
ALTER TABLE volunteer_enrollment MODIFY COLUMN enrollment_id VARCHAR(64);
ALTER TABLE volunteer_enrollment MODIFY COLUMN task_id VARCHAR(64);
ALTER TABLE volunteer_enrollment MODIFY COLUMN user_id VARCHAR(64);

-- 7. student_mentorship
ALTER TABLE student_mentorship MODIFY COLUMN pair_id VARCHAR(64);
ALTER TABLE student_mentorship MODIFY COLUMN senior_student_id VARCHAR(64);
ALTER TABLE student_mentorship MODIFY COLUMN junior_student_id VARCHAR(64);

-- 8. qa_message
ALTER TABLE qa_message MODIFY COLUMN msg_id VARCHAR(64);
ALTER TABLE qa_message MODIFY COLUMN student_id VARCHAR(64);
ALTER TABLE qa_message MODIFY COLUMN teacher_id VARCHAR(64);

-- 9. purchase_record
ALTER TABLE purchase_record MODIFY COLUMN purchase_id VARCHAR(64);

-- 10. sys_notice
ALTER TABLE sys_notice MODIFY COLUMN notice_id VARCHAR(64);

-- 11. sys_teacher
ALTER TABLE sys_teacher MODIFY COLUMN teacher_id VARCHAR(64);

-- 12. duty_schedule
ALTER TABLE duty_schedule MODIFY COLUMN duty_id VARCHAR(64);
ALTER TABLE duty_schedule MODIFY COLUMN user_id VARCHAR(64);

-- 13. sys_banner
ALTER TABLE sys_banner MODIFY COLUMN banner_id VARCHAR(64);

-- 14. student_profile (若不存在补建)
CREATE TABLE IF NOT EXISTS student_profile (
    profile_id VARCHAR(64) PRIMARY KEY COMMENT '档案ID',
    student_id VARCHAR(64) NOT NULL UNIQUE COMMENT '学员用户ID',
    student_name VARCHAR(50) NOT NULL COMMENT '学员姓名',
    grade_level VARCHAR(50) DEFAULT '二年级' COMMENT '年级/班级',
    chinese_score DECIMAL(5,2) DEFAULT 0 COMMENT '语文成绩',
    math_score DECIMAL(5,2) DEFAULT 0 COMMENT '数学成绩',
    english_score DECIMAL(5,2) DEFAULT 0 COMMENT '英语成绩',
    height_cm DECIMAL(5,2) DEFAULT 0 COMMENT '身高(cm)',
    weight_kg DECIMAL(5,2) DEFAULT 0 COMMENT '体重(kg)',
    bust_cm DECIMAL(5,2) DEFAULT 0 COMMENT '胸围(cm)',
    waist_cm DECIMAL(5,2) DEFAULT 0 COMMENT '腰围(cm)',
    hip_cm DECIMAL(5,2) DEFAULT 0 COMMENT '臀围(cm)',
    shoe_size DECIMAL(4,1) DEFAULT 0 COMMENT '舞鞋码数',
    parent_name VARCHAR(50) DEFAULT '' COMMENT '家长姓名',
    parent_phone VARCHAR(20) DEFAULT '' COMMENT '家长手机号',
    resume_bio TEXT COMMENT '艺术简历与获奖履历',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='学员全量综合档案与成绩表';


-- ============================================================================
-- 增量更新记录列表 (按时间倒序追加于末尾)
-- ============================================================================

-- [更新时间: 2026-08-30 19:53:35]
-- 说明: sys_work_group (工作小组表) 字符化与补齐 leader_user_id, member_user_ids, sort_order 字段
ALTER TABLE sys_work_group MODIFY COLUMN group_id VARCHAR(64);
ALTER TABLE sys_work_group ADD COLUMN leader_user_id VARCHAR(64) DEFAULT '' COMMENT '组长用户ID';
ALTER TABLE sys_work_group ADD COLUMN member_user_ids TEXT COMMENT '组员用户ID列表';
ALTER TABLE sys_work_group ADD COLUMN sort_order INT DEFAULT 0 COMMENT '排序权重';
