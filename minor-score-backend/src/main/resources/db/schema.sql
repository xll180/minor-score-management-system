-- =============================================
-- 辅修专业成绩管理系统 - 数据库表结构
-- MySQL 8.0
-- =============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS minor_score DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE minor_score;

-- =============================================
-- 1. 学院表 (college)
-- =============================================
DROP TABLE IF EXISTS `college`;
CREATE TABLE `college` (
    `id`            BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '学院ID',
    `college_name`  VARCHAR(100) NOT NULL            COMMENT '学院名称',
    `description`   VARCHAR(500) DEFAULT NULL        COMMENT '学院描述',
    `dean`          VARCHAR(50)  DEFAULT NULL        COMMENT '院长姓名',
    `create_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`       TINYINT(1)   DEFAULT 0           COMMENT '逻辑删除 0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_college_name` (`college_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学院表';

-- =============================================
-- 2. 管理员表 (admin)
-- =============================================
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin` (
    `id`            BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '管理员ID',
    `username`      VARCHAR(50)  NOT NULL            COMMENT '用户名',
    `password`      VARCHAR(255) NOT NULL            COMMENT '密码(BCrypt加密)',
    `real_name`     VARCHAR(50)  DEFAULT NULL        COMMENT '真实姓名',
    `phone`         VARCHAR(20)  DEFAULT NULL        COMMENT '手机号',
    `email`         VARCHAR(100) DEFAULT NULL        COMMENT '邮箱',
    `status`        TINYINT(1)   DEFAULT 1           COMMENT '状态 0-禁用 1-启用',
    `create_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_admin_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员表';

-- =============================================
-- 3. 教师表 (teacher)
-- =============================================
DROP TABLE IF EXISTS `teacher`;
CREATE TABLE `teacher` (
    `id`            BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '教师ID',
    `teacher_no`    VARCHAR(20)  NOT NULL            COMMENT '教师工号',
    `username`      VARCHAR(50)  NOT NULL            COMMENT '用户名(登录用)',
    `password`      VARCHAR(255) NOT NULL            COMMENT '密码(BCrypt加密)',
    `real_name`     VARCHAR(50)  NOT NULL            COMMENT '真实姓名',
    `gender`        TINYINT(1)   DEFAULT 1           COMMENT '性别 1-男 2-女',
    `phone`         VARCHAR(20)  DEFAULT NULL        COMMENT '手机号',
    `email`         VARCHAR(100) DEFAULT NULL        COMMENT '邮箱',
    `college_id`    BIGINT(20)   DEFAULT NULL        COMMENT '所属学院ID',
    `status`        TINYINT(1)   DEFAULT 1           COMMENT '状态 0-禁用 1-启用',
    `create_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`       TINYINT(1)   DEFAULT 0           COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_teacher_no` (`teacher_no`),
    UNIQUE KEY `uk_teacher_username` (`username`),
    KEY `idx_teacher_college` (`college_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教师表';

-- =============================================
-- 4. 学生表 (student)
-- =============================================
DROP TABLE IF EXISTS `student`;
CREATE TABLE `student` (
    `id`            BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '学生ID',
    `student_no`    VARCHAR(20)  NOT NULL            COMMENT '学号',
    `username`      VARCHAR(50)  NOT NULL            COMMENT '用户名(登录用)',
    `password`      VARCHAR(255) NOT NULL            COMMENT '密码(BCrypt加密)',
    `real_name`     VARCHAR(50)  NOT NULL            COMMENT '真实姓名',
    `gender`        TINYINT(1)   DEFAULT 1           COMMENT '性别 1-男 2-女',
    `phone`         VARCHAR(20)  DEFAULT NULL        COMMENT '手机号',
    `email`         VARCHAR(100) DEFAULT NULL        COMMENT '邮箱',
    `college_id`    BIGINT(20)   DEFAULT NULL        COMMENT '所属学院ID',
    `grade`         VARCHAR(10)  DEFAULT NULL        COMMENT '年级 如:2024',
    `status`        TINYINT(1)   DEFAULT 1           COMMENT '状态 0-禁用 1-启用',
    `create_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`       TINYINT(1)   DEFAULT 0           COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_student_no` (`student_no`),
    UNIQUE KEY `uk_student_username` (`username`),
    KEY `idx_student_college` (`college_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生表';

-- =============================================
-- 5. 课程表 (course)
-- =============================================
DROP TABLE IF EXISTS `course`;
CREATE TABLE `course` (
    `id`            BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '课程ID',
    `course_no`     VARCHAR(20)  NOT NULL            COMMENT '课程编号',
    `course_name`   VARCHAR(100) NOT NULL            COMMENT '课程名称',
    `description`   VARCHAR(500) DEFAULT NULL        COMMENT '课程描述',
    `credit`        DECIMAL(3,1) DEFAULT 2.0         COMMENT '学分',
    `teacher_id`    BIGINT(20)   NOT NULL            COMMENT '授课教师ID',
    `college_id`    BIGINT(20)   DEFAULT NULL        COMMENT '开课学院ID',
    `max_students`  INT(11)      DEFAULT 50          COMMENT '最大选课人数',
    `current_count` INT(11)      DEFAULT 0           COMMENT '当前选课人数',
    `semester`      VARCHAR(20)  DEFAULT NULL        COMMENT '学期 如:2024-2025-1',
    `classroom`     VARCHAR(50)  DEFAULT NULL        COMMENT '上课教室',
    `schedule`      VARCHAR(100) DEFAULT NULL        COMMENT '上课时间 如:周一1-2节',
    `status`        TINYINT(1)   DEFAULT 1           COMMENT '状态 0-停课 1-开课',
    `create_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`       TINYINT(1)   DEFAULT 0           COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_course_no` (`course_no`),
    KEY `idx_course_teacher` (`teacher_id`),
    KEY `idx_course_college` (`college_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程表';

-- =============================================
-- 6. 选课记录表 (course_selection)
-- =============================================
DROP TABLE IF EXISTS `course_selection`;
CREATE TABLE `course_selection` (
    `id`            BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '选课记录ID',
    `student_id`    BIGINT(20)   NOT NULL            COMMENT '学生ID',
    `course_id`     BIGINT(20)   NOT NULL            COMMENT '课程ID',
    `select_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '选课时间',
    `status`        TINYINT(1)   DEFAULT 1           COMMENT '状态 0-退课 1-在读',
    `create_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_student_course` (`student_id`, `course_id`),
    KEY `idx_selection_student` (`student_id`),
    KEY `idx_selection_course` (`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='选课记录表';

-- =============================================
-- 7. 成绩表 (score)
-- =============================================
DROP TABLE IF EXISTS `score`;
CREATE TABLE `score` (
    `id`              BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '成绩ID',
    `student_id`      BIGINT(20)   NOT NULL            COMMENT '学生ID',
    `course_id`       BIGINT(20)   NOT NULL            COMMENT '课程ID',
    `score`           DECIMAL(5,1) DEFAULT NULL        COMMENT '成绩分数',
    `grade_level`     VARCHAR(10)  DEFAULT NULL        COMMENT '等级 优/良/中/及格/不及格',
    `exam_type`       VARCHAR(20)  DEFAULT '期末考试'  COMMENT '考试类型',
    `import_batch_id` BIGINT(20)   DEFAULT NULL        COMMENT '导入批次ID',
    `remark`          VARCHAR(200) DEFAULT NULL        COMMENT '备注',
    `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_student_course_exam` (`student_id`, `course_id`, `exam_type`),
    KEY `idx_score_student` (`student_id`),
    KEY `idx_score_course` (`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='成绩表';

-- =============================================
-- 8. 成绩导入批次表 (import_batch)
-- =============================================
DROP TABLE IF EXISTS `import_batch`;
CREATE TABLE `import_batch` (
    `id`              BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '批次ID',
    `course_id`       BIGINT(20)   NOT NULL            COMMENT '课程ID',
    `teacher_id`      BIGINT(20)   NOT NULL            COMMENT '导入教师ID',
    `batch_name`      VARCHAR(100) DEFAULT NULL        COMMENT '批次名称',
    `total_count`     INT(11)      DEFAULT 0           COMMENT '导入总数',
    `success_count`   INT(11)      DEFAULT 0           COMMENT '成功导入数',
    `fail_count`      INT(11)      DEFAULT 0           COMMENT '失败数',
    -- 以下为成绩统计数据(JSON格式存储在数据库中)
    `avg_score`       DECIMAL(5,1) DEFAULT NULL        COMMENT '平均分',
    `max_score`       DECIMAL(5,1) DEFAULT NULL        COMMENT '最高分',
    `min_score`       DECIMAL(5,1) DEFAULT NULL        COMMENT '最低分',
    `pass_rate`       DECIMAL(5,2) DEFAULT NULL        COMMENT '及格率(%)',
    `excellent_rate`  DECIMAL(5,2) DEFAULT NULL        COMMENT '优秀率(% 分数>=90)',
    `score_distribution` TEXT      DEFAULT NULL        COMMENT '分数段分布 JSON格式 [{"range":"0-59","count":5},...]',
    `ai_report`       LONGTEXT    DEFAULT NULL        COMMENT 'AI分析报告内容',
    `report_status`   TINYINT(1)  DEFAULT 0           COMMENT '报告状态 0-未生成 1-已生成',
    `create_time`     DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_batch_course` (`course_id`),
    KEY `idx_batch_teacher` (`teacher_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='成绩导入批次表';

-- =============================================
-- 插入初始化数据
-- =============================================
-- 默认管理员账号: admin / admin123 (BCrypt加密)
INSERT INTO `admin` (`username`, `password`, `real_name`, `phone`, `email`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '系统管理员', '13800000000', 'admin@minor.edu.cn');

-- 默认学院
INSERT INTO `college` (`college_name`, `description`, `dean`) VALUES
('计算机科学与技术学院', '负责计算机科学、软件工程等专业的教学与科研', '张教授'),
('数学与统计学院', '负责数学与应用数学、统计学等专业的教学与科研', '李教授'),
('外国语学院', '负责英语、日语、法语等外语专业的教学与科研', '王教授'),
('经济管理学院', '负责经济学、管理学等专业的教学与科研', '赵教授');

-- 默认教师 (密码均为 123456, BCrypt加密)
INSERT INTO `teacher` (`teacher_no`, `username`, `password`, `real_name`, `gender`, `phone`, `email`, `college_id`) VALUES
('T001', 'teacher1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKtv5Q', '张老师', 1, '13800000001', 'teacher1@minor.edu.cn', 1),
('T002', 'teacher2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKtv5Q', '李老师', 2, '13800000002', 'teacher2@minor.edu.cn', 2),
('T003', 'teacher3', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKtv5Q', '王老师', 1, '13800000003', 'teacher3@minor.edu.cn', 3);

-- 默认学生 (密码均为 123456, BCrypt加密)
INSERT INTO `student` (`student_no`, `username`, `password`, `real_name`, `gender`, `phone`, `email`, `college_id`, `grade`) VALUES
('S2024001', 'student1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKtv5Q', '张三', 1, '13900000001', 'student1@minor.edu.cn', 1, '2024'),
('S2024002', 'student2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKtv5Q', '李四', 2, '13900000002', 'student2@minor.edu.cn', 1, '2024'),
('S2024003', 'student3', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKtv5Q', '王五', 1, '13900000003', 'student3@minor.edu.cn', 2, '2024'),
('S2024004', 'student4', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKtv5Q', '赵六', 2, '13900000004', 'student4@minor.edu.cn', 2, '2024'),
('S2024005', 'student5', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKtv5Q', '陈七', 1, '13900000005', 'student5@minor.edu.cn', 3, '2024');