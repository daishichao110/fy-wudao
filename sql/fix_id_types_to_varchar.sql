USE wudao_db;

-- 1. sys_user
ALTER TABLE sys_user MODIFY COLUMN user_id VARCHAR(64);

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

-- 13. student_profile
ALTER TABLE student_profile MODIFY COLUMN profile_id VARCHAR(64);
ALTER TABLE student_profile MODIFY COLUMN student_id VARCHAR(64);

-- 14. sys_work_group
ALTER TABLE sys_work_group MODIFY COLUMN group_id VARCHAR(64);

-- 15. sys_banner
ALTER TABLE sys_banner MODIFY COLUMN banner_id VARCHAR(64);

-- 16. sys_item_demand
ALTER TABLE sys_item_demand MODIFY COLUMN item_id VARCHAR(64);

-- 17. sys_thought
ALTER TABLE sys_thought MODIFY COLUMN thought_id VARCHAR(64);
ALTER TABLE sys_thought MODIFY COLUMN user_id VARCHAR(64);
