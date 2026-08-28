# 舞蹈学校数字化综合管理小程序 —— 架构设计与 API 契约文档 (Architecture)

| 文档版本 | V1.0 | 编制日期 | 2026-08-21 |
| :--- | :--- | :--- | :--- |
| 后端技术栈 | Java 17 / 8 + Spring Boot 3.x / 2.7.x + MyBatis (XML) | 数据库 | MySQL 8.0 (root / 12345678) |
| 前端技术栈 | Vue 3 + Vite + Pinia + Vue Router + Axios + Lucide Icons | 架构模式 | SSM 经典三层 (Controller - Service - DAO/Mapper) |

---

## 1. 系统总体架构设计

系统采用前后端分离 (B/S & Miniprogram API) 模式：

```
+-----------------------------------------------------------------------------------+
|                              前端 UI 层 (Vue 3 Single Page Application)            |
|  [首页/装备指南]  [教务课表/请假补课]  [家委协同/志愿组]  [量体档案]  [1对1私信/精选库] |
+-----------------------------------------------------------------------------------+
                                         |  Axios HTTP / JSON
                                         v
+-----------------------------------------------------------------------------------+
|                        后端 API 层 (Spring Boot MVC + SSM 架构)                    |
|                                                                                   |
|  [Controller 层] --> 接收 Request / 校验参数 / 详细 SLF4J 日志                         |
|         |                                                                         |
|  [Service 层]    --> 极简业务逻辑 (零审批流) / 事务管理                                 |
|         |                                                                         |
|  [DAO/Mapper 层] --> Java Mapper 接口定义                                         |
|         |                                                                         |
|  [MyBatis XML]   --> SQL 映射 (resources/mapper/*.xml)                                |
+-----------------------------------------------------------------------------------+
                                         |  JDBC / HikariCP
                                         v
+-----------------------------------------------------------------------------------+
|                          数据库持久层 (MySQL 8.0 数据库)                          |
|  [sys_user]  [dance_schedule]  [student_body_metric]  [volunteer_task]            |
|  [volunteer_enrollment]  [student_mentorship]  [qa_message]  [purchase_record]    |
+-----------------------------------------------------------------------------------+
```

---

## 2. 数据库设计 (MySQL DDL & 种子数据)

数据库名：`wudao_db`
本地 MySQL 配置：主机 `localhost:3306`，账号 `root`，密码 `12345678`

### 2.1 表结构设计 (Schema DDL)

```sql
CREATE DATABASE IF NOT EXISTS wudao_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE wudao_db;

-- 1. 用户表 (sys_user)
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '登录账号',
    real_name VARCHAR(50) NOT NULL COMMENT '真实姓名',
    phone VARCHAR(20) NOT NULL COMMENT '联系电话',
    avatar_url VARCHAR(255) DEFAULT '' COMMENT '头像URL',
    role_type VARCHAR(30) NOT NULL COMMENT 'SUPER_ADMIN/ADMIN/TEACHER/COMMITTEE/STUDENT',
    dance_class_name VARCHAR(50) DEFAULT '' COMMENT '所在班级名称',
    remaining_hours INT DEFAULT 20 COMMENT '剩余课时',
    volunteer_points INT DEFAULT 0 COMMENT '爱心志愿积分',
    status TINYINT DEFAULT 1 COMMENT '1-正常 0-禁用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- 2. 课程排期与装备规范表 (dance_schedule)
DROP TABLE IF EXISTS dance_schedule;
CREATE TABLE dance_schedule (
    schedule_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '排期ID',
    course_name VARCHAR(100) NOT NULL COMMENT '课程名称',
    dance_type VARCHAR(50) NOT NULL COMMENT '舞种:中国舞/芭蕾/拉丁/现代舞等',
    teacher_id BIGINT NOT NULL COMMENT '任课教师ID',
    teacher_name VARCHAR(50) NOT NULL COMMENT '任课教师姓名',
    classroom_name VARCHAR(50) NOT NULL COMMENT '教室名称',
    class_date DATE NOT NULL COMMENT '上课日期',
    start_time VARCHAR(20) NOT NULL COMMENT '开始时间 14:00',
    end_time VARCHAR(20) NOT NULL COMMENT '结束时间 16:00',
    tops_req VARCHAR(100) NOT NULL COMMENT '上身着装',
    bottoms_req VARCHAR(100) NOT NULL COMMENT '下身着装',
    shoes_req VARCHAR(100) NOT NULL COMMENT '鞋履要求',
    hair_req VARCHAR(100) NOT NULL COMMENT '发型要求',
    props_req VARCHAR(100) NOT NULL COMMENT '携带教具',
    capacity INT DEFAULT 15 COMMENT '班级容量',
    booked_count INT DEFAULT 0 COMMENT '已约人数',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程排期与着装要求表';

-- 3. 请假与补课记录表 (leave_make_up)
DROP TABLE IF EXISTS leave_make_up;
CREATE TABLE leave_make_up (
    record_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    student_name VARCHAR(50) NOT NULL COMMENT '学生姓名',
    schedule_id BIGINT NOT NULL COMMENT '关联课程排期ID',
    course_name VARCHAR(100) NOT NULL COMMENT '课程名称',
    record_type VARCHAR(20) NOT NULL COMMENT 'LEAVE(请假) / MAKE_UP(补课)',
    reason VARCHAR(255) DEFAULT '' COMMENT '请假原因',
    status VARCHAR(20) DEFAULT 'EFFECTIVE' COMMENT 'EFFECTIVE(生效中)/CANCELLED(已取消)',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='请假与补课记录表';

-- 4. 学员身材量体档案表 (student_body_metric)
DROP TABLE IF EXISTS student_body_metric;
CREATE TABLE student_body_metric (
    metric_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '量体ID',
    student_id BIGINT NOT NULL COMMENT '学员ID',
    student_name VARCHAR(50) NOT NULL COMMENT '学员姓名',
    height_cm DECIMAL(5,2) NOT NULL COMMENT '身高cm',
    weight_kg DECIMAL(5,2) NOT NULL COMMENT '体重kg',
    bust_cm DECIMAL(5,2) NOT NULL COMMENT '胸围cm',
    waist_cm DECIMAL(5,2) NOT NULL COMMENT '腰围cm',
    hip_cm DECIMAL(5,2) NOT NULL COMMENT '臀围cm',
    torso_length_cm DECIMAL(5,2) NOT NULL COMMENT '胴长cm',
    shoe_size DECIMAL(3,1) NOT NULL COMMENT '鞋码(欧码)',
    measured_date DATE NOT NULL COMMENT '测量日期',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学员身材量体档案表';

-- 5. 志愿分工任务表 (volunteer_task)
DROP TABLE IF EXISTS volunteer_task;
CREATE TABLE volunteer_task (
    task_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '任务ID',
    activity_name VARCHAR(100) NOT NULL COMMENT '活动名称',
    group_type VARCHAR(30) NOT NULL COMMENT 'COSTUME(服装)/MAKEUP(化妆)/CATERING(后勤)/PHOTO(摄影)',
    task_name VARCHAR(100) NOT NULL COMMENT '任务具体名称',
    quota_count INT NOT NULL COMMENT '名额上限',
    enrolled_count INT DEFAULT 0 COMMENT '已认领人数',
    status VARCHAR(20) DEFAULT 'RECRUITING' COMMENT 'RECRUITING(招募中)/FULL(已满)',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='志愿分工任务表';

-- 6. 志愿任务认领表 (volunteer_enrollment)
DROP TABLE IF EXISTS volunteer_enrollment;
CREATE TABLE volunteer_enrollment (
    enrollment_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '认领ID',
    task_id BIGINT NOT NULL COMMENT '任务ID',
    user_id BIGINT NOT NULL COMMENT '家委/家长ID',
    user_name VARCHAR(50) NOT NULL COMMENT '家长姓名',
    status VARCHAR(20) DEFAULT 'COMPLETED' COMMENT 'COMPLETED(已认领生效)',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='志愿任务认领表';

-- 7. 高低年级“结对子”记录表 (student_mentorship)
DROP TABLE IF EXISTS student_mentorship;
CREATE TABLE student_mentorship (
    pair_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '结对ID',
    senior_student_id BIGINT NOT NULL COMMENT '高年级学姐/学长ID',
    senior_student_name VARCHAR(50) NOT NULL COMMENT '高年级姓名',
    junior_student_id BIGINT NOT NULL COMMENT '低年级学员ID',
    junior_student_name VARCHAR(50) NOT NULL COMMENT '低年级姓名',
    term_name VARCHAR(50) NOT NULL COMMENT '学期名称',
    star_points INT DEFAULT 0 COMMENT '获得的姐妹星/勋章点数',
    checkin_count INT DEFAULT 0 COMMENT '已完成互动打卡数',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='高低年级结对子记录表';

-- 8. 师生1对1私信与精选知识库表 (qa_message)
DROP TABLE IF EXISTS qa_message;
CREATE TABLE qa_message (
    msg_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '消息ID',
    student_id BIGINT NOT NULL COMMENT '提问学员ID',
    student_name VARCHAR(50) NOT NULL COMMENT '学员姓名',
    teacher_id BIGINT NOT NULL COMMENT '任课教师ID',
    teacher_name VARCHAR(50) NOT NULL COMMENT '教师姓名',
    question_content TEXT NOT NULL COMMENT '提问内容',
    reply_content TEXT COMMENT '教师回复内容',
    is_featured TINYINT DEFAULT 0 COMMENT '0-私密 1-精选公开',
    featured_title VARCHAR(150) DEFAULT '' COMMENT '精选展示标题',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='师生私信与精选问答表';

-- 9. 集中采购公示表 (purchase_record)
DROP TABLE IF EXISTS purchase_record;
CREATE TABLE purchase_record (
    purchase_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '采购ID',
    item_name VARCHAR(100) NOT NULL COMMENT '采购物品名称',
    category VARCHAR(50) NOT NULL COMMENT '服装/道具/保险/大巴',
    total_amount DECIMAL(10,2) NOT NULL COMMENT '总金额(元)',
    unit_price DECIMAL(8,2) NOT NULL COMMENT '单价(元)',
    quantity INT NOT NULL COMMENT '数量',
    proof_url VARCHAR(255) DEFAULT '' COMMENT '发票/收据凭证图片',
    remark VARCHAR(255) DEFAULT '' COMMENT '备注',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='集中采购公示表';
```

---

## 3. 后端代码包结构与分层架构

```
backend/
├── src/main/java/com/wudao/
│   ├── DanceSchoolApplication.java             # Spring Boot 启动主类
│   ├── controller/                             # REST API 控制层 (加日志)
│   │   ├── UserController.java
│   │   ├── ScheduleController.java
│   │   ├── BodyMetricController.java
│   │   ├── VolunteerController.java
│   │   ├── MentorshipController.java
│   │   ├── QaMessageController.java
│   │   └── PurchaseController.java
│   ├── service/                                # 业务接口层
│   │   ├── UserService.java
│   │   ├── ScheduleService.java
│   │   ├── BodyMetricService.java
│   │   ├── VolunteerService.java
│   │   ├── MentorshipService.java
│   │   ├── QaMessageService.java
│   │   └── PurchaseService.java
│   ├── service/impl/                           # 业务实现层 (零审批流)
│   │   └── ...
│   ├── mapper/                                 # MyBatis Mapper 接口层
│   │   ├── UserMapper.java
│   │   ├── ScheduleMapper.java
│   │   ├── BodyMetricMapper.java
│   │   ├── VolunteerMapper.java
│   │   ├── MentorshipMapper.java
│   │   ├── QaMessageMapper.java
│   │   └── PurchaseMapper.java
│   ├── entity/                                 # 数据库 POJO 实体类
│   │   └── ...
│   └── common/                                 # 通用 Response / Config / Context
│       └── Result.java
└── src/main/resources/
    ├── application.yml                         # 数据库与日志配置
    └── mapper/                                 # MyBatis XML 映射文件
        ├── UserMapper.xml
        ├── ScheduleMapper.xml
        ├── BodyMetricMapper.xml
        ├── VolunteerMapper.xml
        ├── MentorshipMapper.xml
        ├── QaMessageMapper.xml
        └── PurchaseMapper.xml
```

---

## 4. RESTful API 规范契约

所有 API 均统一返回结构：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": { ... }
}
```

### 4.1 核心 API 清单

| 模块 | 机制 | HTTP 方法 | Endpoint 接口路径 | 描述 |
| :--- | :--- | :--- | :--- | :--- |
| **用户** | GET | `/api/user/list` | 查询所有用户列表 |
| **用户** | GET | `/api/user/info/{id}` | 查询单用户详情 |
| **排课着装** | GET | `/api/schedule/list` | 查询所有课程排期及着装要求 |
| **排课着装** | POST | `/api/schedule/create` | 教师/管理员发布新排课 |
| **请假补课** | POST | `/api/schedule/leave` | 学员一键请假 (无审批，直接生效) |
| **请假补课** | POST | `/api/schedule/makeup` | 学员一键预约补课 (无审批) |
| **量体档案** | GET | `/api/metric/student/{studentId}` | 查询学员历史量体数据 |
| **量体档案** | POST | `/api/metric/save` | 录入/更新学员量体维度 |
| **家委志愿** | GET | `/api/volunteer/tasks` | 查询所有志愿分工任务 |
| **家委志愿** | POST | `/api/volunteer/enroll` | 家长一键认领志愿任务 (无审批) |
| **结对子** | GET | `/api/mentorship/list` | 查询所有结对子互助列表 |
| **结对子** | POST | `/api/mentorship/checkin` | 结对学员完成一次打卡 |
| **私信答疑** | GET | `/api/qa/my-messages` | 查询我的 1 对 1 私信记录 |
| **私信答疑** | POST | `/api/qa/ask` | 学员向老师发起提问 |
| **私信答疑** | POST | `/api/qa/reply` | 老师回复学员提问 |
| **私信答疑** | POST | `/api/qa/feature` | 老师将问答设为精选公开 |
| **私信答疑** | GET | `/api/qa/featured-list` | 浏览全局舞蹈知识精选库 |
| **采购公示** | GET | `/api/purchase/list` | 查看集中采购账目公示 |

---

## 5. 测试保障策略

1. **后端单元与接口测试**：为每个 Controller 建立单元测试类（`UserControllerTest`, `ScheduleControllerTest`, `VolunteerControllerTest` 等），包含 100% 接口路径覆盖，自动断言响应 200 和 Result 结果。
2. **前端组件与 API 交互测试**：使用 Vitest 针对 API 交互与 Pinia 状态树编写测试规范，验证前后端交互契约。
