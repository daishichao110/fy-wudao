SET NAMES utf8mb4;
USE wudao_db;

UPDATE sys_user 
SET real_name = '林依依老师', 
    student_name = '专业舞蹈导师', 
    relationship = '教师' 
WHERE user_id = 1787400000000000002;

UPDATE sys_teacher 
SET name = '林依依老师', 
    title = '芭蕾舞首席导师', 
    dance_type = '古典芭蕾 / 现代舞', 
    experience_years = '10年教龄', 
    bio = '毕业于北京舞蹈学院芭蕾舞系，曾任国家级舞蹈团首席剧目演员，具备丰富的小学及青少年考级剧目排演经验。' 
WHERE teacher_id = 2001;

UPDATE sys_user 
SET real_name = '杨老师', 
    student_name = '教务教师', 
    relationship = '教师' 
WHERE user_id = 4;
