SET NAMES utf8mb4;

-- 舞团数字化综合管理系统 —— 全量数据库 Schema 定义
-- 包含全表雪花算法主键、多租户年级隔离字段与最新模版表结构

CREATE DATABASE IF NOT EXISTS wudao_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE wudao_db;

-- 1. 系统用户表 (sys_user)
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    user_id VARCHAR(64) PRIMARY KEY COMMENT '用户ID (字符串雪花算法唯一标识)',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '账号名/手机号/标识',
    open_id VARCHAR(128) DEFAULT '' COMMENT '微信OpenID',
    real_name VARCHAR(50) NOT NULL COMMENT '真实姓名/家长姓名',
    student_name VARCHAR(50) DEFAULT '' COMMENT '关联学生姓名',
    relationship VARCHAR(30) DEFAULT '' COMMENT '与学生关系: 爸爸/妈妈/其他监护人',
    phone VARCHAR(20) DEFAULT '' COMMENT '授权手机号',
    avatar_url VARCHAR(255) DEFAULT '/image/teacher1.jpg' COMMENT '相对头像路径',
    role_type VARCHAR(30) NOT NULL COMMENT 'SUPER_ADMIN(管理员)/TEACHER(专业老师)/COMMITTEE(班委/家委干部)/STUDENT(学员及家长)',
    dance_class_name VARCHAR(50) DEFAULT 'GRADE_2' COMMENT '所在班级代号/名称 (租户隔离)',
    enrollment_year INT DEFAULT 2025 COMMENT '入学年份(届别)',
    remaining_hours INT DEFAULT 20 COMMENT '剩余课时',
    volunteer_points INT DEFAULT 0 COMMENT '爱心志愿积分',
    status TINYINT DEFAULT 1 COMMENT '1-正常/已审批通过 0-待管理员审批 2-已驳回',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='系统用户表';

-- 2. 课程排期与装备规范表 (dance_schedule)
DROP TABLE IF EXISTS dance_schedule;
CREATE TABLE dance_schedule (
    schedule_id VARCHAR(64) PRIMARY KEY COMMENT '排期ID (字符串雪花算法 ID)',
    course_name VARCHAR(100) NOT NULL COMMENT '课程名称',
    dance_type VARCHAR(50) NOT NULL COMMENT '舞种:中国舞/芭蕾/拉丁/现代舞等',
    teacher_id VARCHAR(64) NOT NULL COMMENT '任课教师ID',
    teacher_name VARCHAR(50) NOT NULL COMMENT '任课教师姓名',
    classroom_name VARCHAR(50) NOT NULL COMMENT '教室名称',
    class_date DATE NOT NULL COMMENT '上课日期',
    start_time VARCHAR(20) NOT NULL COMMENT '开始时间 14:00',
    end_time VARCHAR(20) NOT NULL COMMENT '结束时间 16:00',
    tops_req VARCHAR(100) NOT NULL COMMENT '上身着装',
    bottoms_req VARCHAR(100) NOT NULL COMMENT '下身着装',
    skirt_req VARCHAR(100) DEFAULT '粉色雪纺一片裙/无需短裙' COMMENT '裙子要求',
    shoes_req VARCHAR(100) NOT NULL COMMENT '鞋履要求',
    hair_req VARCHAR(100) NOT NULL COMMENT '发型要求',
    props_req VARCHAR(100) NOT NULL COMMENT '携带教具',
    other_req VARCHAR(255) DEFAULT '' COMMENT '水壶毛巾等其他要求',
    remark VARCHAR(255) DEFAULT '' COMMENT '课前提醒与备注',
    participant_names TEXT COMMENT '参与人员名单与考勤状态',
    capacity INT DEFAULT 15 COMMENT '班级容量',
    booked_count INT DEFAULT 0 COMMENT '已预约/在读人数',
    dance_class_name VARCHAR(50) DEFAULT '二年级' COMMENT '所在班级',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='课程排期表';

-- 3. 请假与补课核销表 (leave_make_up)
DROP TABLE IF EXISTS leave_make_up;
CREATE TABLE leave_make_up (
    record_id VARCHAR(64) PRIMARY KEY COMMENT '记录ID (字符串雪花算法 ID)',
    student_id VARCHAR(64) NOT NULL COMMENT '学员ID',
    student_name VARCHAR(50) NOT NULL COMMENT '学员姓名',
    schedule_id VARCHAR(64) NOT NULL COMMENT '关联排期ID',
    course_name VARCHAR(100) NOT NULL COMMENT '课程名称',
    record_type VARCHAR(20) NOT NULL COMMENT 'LEAVE-请假 / MAKE_UP-补课',
    reason VARCHAR(255) DEFAULT '' COMMENT '请假/补课说明',
    status VARCHAR(20) DEFAULT 'EFFECTIVE' COMMENT 'EFFECTIVE-生效中 / CANCELLED-已撤销',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='请假与补课核销表';

-- 4. 身材量体档案表 (student_body_metric)
DROP TABLE IF EXISTS student_body_metric;
CREATE TABLE student_body_metric (
    metric_id VARCHAR(64) PRIMARY KEY COMMENT '量体记录ID (字符串雪花算法 ID)',
    student_id VARCHAR(64) NOT NULL COMMENT '学员ID',
    student_name VARCHAR(50) NOT NULL COMMENT '学员姓名',
    height_cm DECIMAL(5,2) NOT NULL COMMENT '身高(cm)',
    weight_kg DECIMAL(5,2) NOT NULL COMMENT '体重(kg)',
    bust_cm DECIMAL(5,2) NOT NULL COMMENT '胸围(cm)',
    waist_cm DECIMAL(5,2) NOT NULL COMMENT '腰围(cm)',
    hip_cm DECIMAL(5,2) NOT NULL COMMENT '臀围(cm)',
    torso_length_cm DECIMAL(5,2) NOT NULL COMMENT '胴长(cm)',
    shoe_size DECIMAL(4,1) NOT NULL COMMENT '舞鞋码数(欧码)',
    measured_date DATE NOT NULL COMMENT '测量日期',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='学员身材量体档案表';

-- 5. 家委志愿任务招募表 (volunteer_task)
DROP TABLE IF EXISTS volunteer_task;
CREATE TABLE volunteer_task (
    task_id VARCHAR(64) PRIMARY KEY COMMENT '任务ID (字符串雪花算法 ID)',
    task_name VARCHAR(100) NOT NULL COMMENT '任务名称',
    group_type VARCHAR(50) NOT NULL COMMENT '后勤保障/化妆道具/安全看护/跟拍宣传',
    activity_name VARCHAR(100) NOT NULL COMMENT '关联演出活动名称',
    task_date VARCHAR(50) DEFAULT '' COMMENT '服务日期 (例如 2026-08-30)',
    service_time VARCHAR(100) DEFAULT '' COMMENT '服务时间段说明 (例如 13:30 - 17:30)',
    location VARCHAR(100) DEFAULT '大剧院/排练厅' COMMENT '活动地点',
    quota_count INT NOT NULL DEFAULT 3 COMMENT '总名额',
    enrolled_count INT NOT NULL DEFAULT 0 COMMENT '已认领人数',
    status VARCHAR(20) DEFAULT 'RECRUITING' COMMENT 'RECRUITING-招募中 / FULL-已满额',
    description TEXT COMMENT '岗位说明与要求',
    dance_class_name VARCHAR(50) DEFAULT '二年级' COMMENT '所属班级/年级 (租户隔离)',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='家委志愿任务表';

-- 6. 家委志愿认领记录表 (volunteer_enrollment)
DROP TABLE IF EXISTS volunteer_enrollment;
CREATE TABLE volunteer_enrollment (
    enrollment_id VARCHAR(64) PRIMARY KEY COMMENT '认领记录ID (字符串雪花算法 ID)',
    task_id VARCHAR(64) NOT NULL COMMENT '志愿任务ID',
    user_id VARCHAR(64) NOT NULL COMMENT '家委/家长用户ID',
    user_name VARCHAR(50) NOT NULL COMMENT '家长姓名/称谓 (如 李小桐的爸爸)',
    status VARCHAR(20) DEFAULT 'COMPLETED' COMMENT 'COMPLETED-已自动核销确认',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '认领时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='家委志愿认领记录表';

-- 7. 高低年级结对子互助表 (student_mentorship)
DROP TABLE IF EXISTS student_mentorship;
CREATE TABLE student_mentorship (
    pair_id VARCHAR(64) PRIMARY KEY COMMENT '结对ID (字符串雪花算法 ID)',
    senior_student_id VARCHAR(64) NOT NULL COMMENT '高年级学姐ID',
    senior_student_name VARCHAR(50) NOT NULL COMMENT '学姐姓名',
    junior_student_id VARCHAR(64) NOT NULL COMMENT '低年级学妹ID',
    junior_student_name VARCHAR(50) NOT NULL COMMENT '学妹姓名',
    dance_class_name VARCHAR(50) NOT NULL COMMENT '关联班级',
    star_points INT DEFAULT 0 COMMENT '姐妹星积分',
    checkin_count INT DEFAULT 0 COMMENT '打卡次数',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '结对时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='高低年级结对子互助表';

-- 8. 1对1私信与知识库表 (qa_message)
DROP TABLE IF EXISTS qa_message;
CREATE TABLE qa_message (
    msg_id VARCHAR(64) PRIMARY KEY COMMENT '消息ID (字符串雪花算法 ID)',
    student_id VARCHAR(64) NOT NULL COMMENT '学员/家长ID',
    student_name VARCHAR(50) NOT NULL COMMENT '学员姓名',
    teacher_id VARCHAR(64) NOT NULL COMMENT '导师ID',
    teacher_name VARCHAR(50) NOT NULL COMMENT '导师姓名',
    question_content TEXT NOT NULL COMMENT '咨询问题',
    reply_content TEXT COMMENT '导师专业解答',
    is_featured TINYINT DEFAULT 0 COMMENT '1-设为精选公开知识 0-私信',
    featured_title VARCHAR(150) DEFAULT '' COMMENT '精选知识标题',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '提问时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='私信与精选知识库表';

-- 9. 集中采购与费用公示表 (purchase_record)
DROP TABLE IF EXISTS purchase_record;
CREATE TABLE purchase_record (
    purchase_id VARCHAR(64) PRIMARY KEY COMMENT '采购ID (字符串雪花算法 ID)',
    item_name VARCHAR(100) NOT NULL COMMENT '采购物品名称',
    category VARCHAR(50) NOT NULL COMMENT '道具/演出服/剧场租用/跟拍费',
    unit_price DECIMAL(10,2) NOT NULL COMMENT '单价',
    quantity INT NOT NULL COMMENT '数量',
    total_amount DECIMAL(10,2) NOT NULL COMMENT '总金额',
    proof_url VARCHAR(255) DEFAULT '/image/purchase_proof.jpg' COMMENT '发票凭证相对路径',
    remark VARCHAR(255) DEFAULT '' COMMENT '采购说明',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '采购公示时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='集中采购公示表';

-- 10. 官方通知公告表 (sys_notice)
DROP TABLE IF EXISTS sys_notice;
CREATE TABLE sys_notice (
    notice_id VARCHAR(64) PRIMARY KEY COMMENT '公告ID (字符串雪花算法 ID)',
    tag VARCHAR(30) DEFAULT '【通知】' COMMENT '标签',
    title VARCHAR(200) NOT NULL COMMENT '标题',
    content TEXT COMMENT '公告内容',
    publisher VARCHAR(50) DEFAULT '舞团教务处' COMMENT '发布单位',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='官方通知公告表';

-- 11. 名师团队档案表 (sys_teacher)
DROP TABLE IF EXISTS sys_teacher;
CREATE TABLE sys_teacher (
    teacher_id VARCHAR(64) PRIMARY KEY COMMENT '教师ID (字符串雪花算法 ID)',
    name VARCHAR(50) NOT NULL COMMENT '教师姓名',
    title VARCHAR(100) NOT NULL COMMENT '职称头衔',
    dance_type VARCHAR(50) NOT NULL COMMENT '专业舞种',
    experience_years VARCHAR(30) DEFAULT '8年教龄' COMMENT '教龄经验',
    avatar_url VARCHAR(255) DEFAULT '/image/teacher1.jpg' COMMENT '肖像照片相对路径',
    bio TEXT COMMENT '导师履历介绍',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='名师团队档案表';

-- 12. 未来7天家委轮值看护排班表 (duty_schedule)
DROP TABLE IF EXISTS duty_schedule;
CREATE TABLE duty_schedule (
    duty_id VARCHAR(64) PRIMARY KEY COMMENT '轮值看护ID (字符串雪花算法 ID)',
    duty_date DATE NOT NULL COMMENT '轮值日期',
    assignee_name VARCHAR(64) NOT NULL COMMENT '认领家委称谓 (如 李小桐的爸爸)',
    user_id VARCHAR(64) DEFAULT NULL COMMENT '关联用户ID',
    dance_class_name VARCHAR(50) NOT NULL DEFAULT '二年级' COMMENT '关联班级 (租户隔离)',
    status VARCHAR(20) DEFAULT 'SCHEDULED' COMMENT '状态',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
    UNIQUE KEY uk_duty_date_class (duty_date, dance_class_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='家委7天轮值看护表';

-- 13. 学员全量综合档案与成绩表 (student_profile)
DROP TABLE IF EXISTS student_profile;
CREATE TABLE student_profile (
    profile_id VARCHAR(64) PRIMARY KEY COMMENT '档案ID (字符串雪花算法 ID)',
    student_id VARCHAR(64) NOT NULL UNIQUE COMMENT '学员用户ID',
    student_name VARCHAR(50) NOT NULL COMMENT '学员姓名',
    grade_level VARCHAR(50) DEFAULT 'GRADE_2' COMMENT '年级/班级 (租户隔离)',
    enrollment_year INT DEFAULT 2025 COMMENT '入学年份(届别)',
    chinese_score DECIMAL(5,2) DEFAULT 0 COMMENT '语文成绩',
    math_score DECIMAL(5,2) DEFAULT 0 COMMENT '数学成绩',
    english_score DECIMAL(5,2) DEFAULT 0 COMMENT '英语成绩',
    height_cm DECIMAL(5,2) DEFAULT 0 COMMENT '身高(cm)',
    weight_kg DECIMAL(5,2) DEFAULT 0 COMMENT '体重(kg)',
    bust_cm DECIMAL(5,2) DEFAULT 0 COMMENT '胸围(cm)',
    waist_cm DECIMAL(5,2) DEFAULT 0 COMMENT '腰围(cm)',
    hip_cm DECIMAL(5,2) DEFAULT 0 COMMENT '臀围(cm)',
    shoe_size DECIMAL(4,1) DEFAULT 0 COMMENT '舞鞋码数(欧码)',
    parent_name VARCHAR(50) DEFAULT '' COMMENT '家长姓名',
    parent_phone VARCHAR(20) DEFAULT '' COMMENT '家长手机号',
    resume_bio TEXT COMMENT '艺术简历与获奖履历',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='学员全量综合档案与成绩表';

-- 14. 舞团工作小组风采表 (sys_work_group)
DROP TABLE IF EXISTS sys_work_group;
CREATE TABLE sys_work_group (
    group_id VARCHAR(64) PRIMARY KEY COMMENT '小组ID (字符串雪花算法 ID)',
    group_name VARCHAR(100) NOT NULL COMMENT '小组名称',
    icon VARCHAR(20) DEFAULT '💄' COMMENT '小组图标Emoji',
    leader_user_id VARCHAR(64) DEFAULT '' COMMENT '组长用户ID',
    member_user_ids TEXT COMMENT '组员用户ID列表',
    dance_class_name VARCHAR(50) DEFAULT 'GRADE_2' COMMENT '所属班级/年级 (租户隔离)',
    leader_name VARCHAR(50) DEFAULT '' COMMENT '组长姓名/家委称谓',
    member_names TEXT COMMENT '组员姓名列表(逗号分隔)',
    duty_desc TEXT COMMENT '工作职责与分工',
    sort_order INT DEFAULT 0 COMMENT '排序权重',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='舞团工作小组风采表';

-- 15. 大型演出与风采展播表 (sys_banner)
DROP TABLE IF EXISTS sys_banner;
CREATE TABLE sys_banner (
    banner_id VARCHAR(64) PRIMARY KEY COMMENT '展播ID (字符串雪花算法 ID)',
    title VARCHAR(100) NOT NULL COMMENT '展播标题',
    subtitle VARCHAR(100) DEFAULT '' COMMENT '副标题',
    badge VARCHAR(30) DEFAULT '🎪 大型演出' COMMENT '标签类型',
    image_url VARCHAR(255) DEFAULT '/image/banner1.jpg' COMMENT '相对图片路径',
    content TEXT COMMENT '展播图文详情',
    event_date VARCHAR(50) DEFAULT '' COMMENT '活动/演出日期',
    location VARCHAR(100) DEFAULT '' COMMENT '演出场地地点',
    creator_name VARCHAR(50) DEFAULT '' COMMENT '发布人姓名',
    creator_role VARCHAR(30) DEFAULT '' COMMENT '发布人角色',
    status TINYINT DEFAULT 1 COMMENT '1-展示中 0-下架',
    sort_order INT DEFAULT 0 COMMENT '排序权重',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='大型演出与风采展播表';

-- 16. 物品选购与集中采购需求表 (sys_item_demand)
DROP TABLE IF EXISTS sys_item_demand;
CREATE TABLE sys_item_demand (
    item_id VARCHAR(64) PRIMARY KEY COMMENT '选购物品ID (字符串雪花算法 ID)',
    item_name VARCHAR(100) NOT NULL COMMENT '物品选购名称',
    spec VARCHAR(100) DEFAULT '' COMMENT '规格要求说明',
    unit_price VARCHAR(20) DEFAULT '￥0.00' COMMENT '预估单价',
    deadline VARCHAR(30) DEFAULT '' COMMENT '报名截止日期',
    expected_arrival_date VARCHAR(30) DEFAULT '' COMMENT '预计到货日期',
    arrival_status VARCHAR(20) DEFAULT '未到货' COMMENT '到货状态: 未到货/部分到货/已全到货',
    dance_class_name VARCHAR(50) DEFAULT 'GRADE_2' COMMENT '所属班级/年级 (租户隔离)',
    size_summary_str VARCHAR(255) DEFAULT '' COMMENT '各尺码报名统计',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='物品选购与集中采购需求表';

-- 17. 物品选购家长个数登记表 (item_demand_enrollment)
DROP TABLE IF EXISTS item_demand_enrollment;
CREATE TABLE item_demand_enrollment (
    enrollment_id VARCHAR(64) PRIMARY KEY COMMENT '登记ID (字符串雪花算法 ID)',
    item_id VARCHAR(64) NOT NULL COMMENT '物品ID',
    parent_name VARCHAR(50) NOT NULL COMMENT '家长/填报人姓名',
    quantity INT NOT NULL DEFAULT 1 COMMENT '购买个数',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '登记时间',
    UNIQUE KEY uk_item_parent (item_id, parent_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='物品选购家长个数登记表';

-- 17. 随感与心里话交流表 (sys_thought)
DROP TABLE IF EXISTS sys_thought;
CREATE TABLE sys_thought (
    thought_id VARCHAR(64) PRIMARY KEY COMMENT '随感ID (字符串雪花算法 ID)',
    type VARCHAR(20) NOT NULL COMMENT 'THOUGHT(有感而发)/HEART(说说心里话)',
    author_name VARCHAR(50) NOT NULL COMMENT '发布人姓名/称谓',
    user_id VARCHAR(64) NOT NULL COMMENT '发布人用户ID',
    dance_class_name VARCHAR(50) DEFAULT '二年级' COMMENT '所在年级/班级 (租户隔离)',
    content TEXT NOT NULL COMMENT '发布内容',
    likes_count INT DEFAULT 0 COMMENT '获赞数',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='随感与心里话交流表';

-- =========================================================================
-- 基础测试数据初始化
-- =========================================================================

-- 1. 初始系统账号 (超级管理员 & 专业老师)
INSERT INTO sys_user (user_id, username, real_name, student_name, relationship, phone, avatar_url, role_type, dance_class_name, remaining_hours, volunteer_points, status) VALUES
(1787400000000000001, 'admin', '系统管理员', '全校学生', '管理员', '18911800655', '/image/teacher1.jpg', 'SUPER_ADMIN', '全校全局管理', 1000, 100, 1),
(1787400000000000002, 'teacher', '林依依老师', '专业舞蹈导师', '教师', '18618486266', '/image/teacher1.jpg', 'TEACHER', '全校全局管理', 1000, 100, 1);

-- 2. 初始家委工作小组
INSERT INTO sys_work_group (group_id, group_name, icon, dance_class_name, leader_name, member_names, duty_desc) VALUES
(1001, '后勤化妆组', '💄', '二年级', '张妈妈 (家委)', '李妈妈, 王妈妈, 赵妈妈', '负责舞团大型演出、舞台试妆、发型打造与彩排检录后勤工作'),
(1002, '道具与服装组', '👗', '二年级', '陈妈妈 (家委)', '周妈妈, 吴妈妈', '负责演出服试穿度量、演出道具保管维护及剧场打点');

-- 3. 初始活动展播 Banner
INSERT INTO sys_banner (banner_id, title, subtitle, badge, image_url, content, event_date, location, creator_name, creator_role, status, sort_order) VALUES
(1, '2026金帆舞团大剧院年度展演', '劲松金帆舞团 · 华彩盛典', '🎪 大型演出', '/image/banner1.jpg', '舞团年度大展演即将在国家大剧院精彩亮相，全团学员紧密排练中。', '2026-09-15', '国家大剧院歌剧院', '教务管理处', '👑 管理员', 1, 1),
(2, '舞团少儿芭蕾剧目全区金奖风采', '捷报频传 · 优雅绽放', '🏆 风采展示', '/image/banner2.jpg', '祝贺我校二年级与三年级学员在全区少儿舞蹈展演中喜斩获一等奖。', '2026-08-20', '区文化馆大剧场', '教务管理处', '👑 管理员', 1, 2);

-- 4. 初始名师团队档案
INSERT INTO sys_teacher (teacher_id, name, title, dance_type, experience_years, avatar_url, bio) VALUES
(2001, '林依依老师', '芭蕾舞首席导师', '古典芭蕾 / 现代舞', '10年教龄', '/image/teacher1.jpg', '毕业于北京舞蹈学院芭蕾舞系，曾任国家级舞蹈团首席剧目演员，具备丰富的小学及青少年考级剧目排演经验。');
