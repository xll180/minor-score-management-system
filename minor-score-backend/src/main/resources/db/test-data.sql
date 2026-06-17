-- =============================================
-- 辅修专业成绩管理系统 - 测试数据
-- 包含完整的学院、教师、学生、课程、选课、成绩数据
-- 可用于测试所有功能模块
-- =============================================

USE minor_score;

-- =============================================
-- 清空已有数据（按外键依赖顺序，TRUNCATE同时重置自增ID）
-- =============================================
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE import_batch;
TRUNCATE TABLE score;
TRUNCATE TABLE course_selection;
TRUNCATE TABLE course;
TRUNCATE TABLE student;
TRUNCATE TABLE teacher;
TRUNCATE TABLE college;
TRUNCATE TABLE admin;
SET FOREIGN_KEY_CHECKS = 1;

-- =============================================
-- 1. 管理员 (密码: admin123)
-- =============================================
INSERT INTO `admin` (`username`, `password`, `real_name`, `phone`, `email`, `status`) VALUES
('admin', '$2a$10$5bDLhTf2XnBqroMw2KSMguG9UJ8F4ZEP6CGdPztK3IfP8ON.DrsMy', '系统管理员', '13800000000', 'admin@minor.edu.cn', 1);

-- =============================================
-- 2. 学院 (6个学院)
-- =============================================
INSERT INTO `college` (`college_name`, `description`, `dean`) VALUES
('计算机科学与技术学院', '负责计算机科学、软件工程、人工智能等专业的教学与科研', '张明教授'),
('数学与统计学院', '负责数学与应用数学、统计学、数据科学等专业的教学与科研', '李华教授'),
('外国语学院', '负责英语、日语、法语、翻译等外语专业的教学与科研', '王丽教授'),
('经济管理学院', '负责经济学、工商管理、会计学等专业的教学与科研', '赵强教授'),
('电子信息工程学院', '负责电子信息工程、通信工程、自动化等专业的教学与科研', '刘伟教授'),
('艺术设计学院', '负责视觉传达、环境设计、数字媒体等专业的教学与科研', '陈芳教授');

-- =============================================
-- 3. 教师 (8位教师, 密码均为 123456)
-- =============================================
INSERT INTO `teacher` (`teacher_no`, `username`, `password`, `real_name`, `gender`, `phone`, `email`, `college_id`, `status`) VALUES
-- 计算机学院
('T001', 'teacher1', '$2a$10$Ef47dBhtP3xJF4xsaDyUBefhzFfjb9ikm3UpCrMmjdPEBBGNkYgRm', '张明', 1, '13800000001', 'zhangming@minor.edu.cn', 1, 1),
('T002', 'teacher2', '$2a$10$Ef47dBhtP3xJF4xsaDyUBefhzFfjb9ikm3UpCrMmjdPEBBGNkYgRm', '李华', 2, '13800000002', 'lihua@minor.edu.cn', 1, 1),
-- 数学学院
('T003', 'teacher3', '$2a$10$Ef47dBhtP3xJF4xsaDyUBefhzFfjb9ikm3UpCrMmjdPEBBGNkYgRm', '王丽', 2, '13800000003', 'wangli@minor.edu.cn', 2, 1),
('T004', 'teacher4', '$2a$10$Ef47dBhtP3xJF4xsaDyUBefhzFfjb9ikm3UpCrMmjdPEBBGNkYgRm', '赵强', 1, '13800000004', 'zhaoqiang@minor.edu.cn', 2, 1),
-- 外国语学院
('T005', 'teacher5', '$2a$10$Ef47dBhtP3xJF4xsaDyUBefhzFfjb9ikm3UpCrMmjdPEBBGNkYgRm', '刘伟', 1, '13800000005', 'liuwei@minor.edu.cn', 3, 1),
-- 经管学院
('T006', 'teacher6', '$2a$10$Ef47dBhtP3xJF4xsaDyUBefhzFfjb9ikm3UpCrMmjdPEBBGNkYgRm', '陈芳', 2, '13800000006', 'chenfang@minor.edu.cn', 4, 1),
-- 电子信息学院
('T007', 'teacher7', '$2a$10$Ef47dBhtP3xJF4xsaDyUBefhzFfjb9ikm3UpCrMmjdPEBBGNkYgRm', '孙磊', 1, '13800000007', 'sunlei@minor.edu.cn', 5, 1),
-- 艺术学院
('T008', 'teacher8', '$2a$10$Ef47dBhtP3xJF4xsaDyUBefhzFfjb9ikm3UpCrMmjdPEBBGNkYgRm', '周婷', 2, '13800000008', 'zhouting@minor.edu.cn', 6, 1);

-- =============================================
-- 4. 学生 (20位学生, 密码均为 123456)
-- =============================================
INSERT INTO `student` (`student_no`, `username`, `password`, `real_name`, `gender`, `phone`, `email`, `college_id`, `grade`, `status`) VALUES
-- 计算机学院 (5人)
('S2024001', 'student1',  '$2a$10$Ef47dBhtP3xJF4xsaDyUBefhzFfjb9ikm3UpCrMmjdPEBBGNkYgRm', '张三', 1, '13900000001', 's001@minor.edu.cn', 1, '2024', 1),
('S2024002', 'student2',  '$2a$10$Ef47dBhtP3xJF4xsaDyUBefhzFfjb9ikm3UpCrMmjdPEBBGNkYgRm', '李四', 2, '13900000002', 's002@minor.edu.cn', 1, '2024', 1),
('S2024003', 'student3',  '$2a$10$Ef47dBhtP3xJF4xsaDyUBefhzFfjb9ikm3UpCrMmjdPEBBGNkYgRm', '王五', 1, '13900000003', 's003@minor.edu.cn', 1, '2024', 1),
('S2024004', 'student4',  '$2a$10$Ef47dBhtP3xJF4xsaDyUBefhzFfjb9ikm3UpCrMmjdPEBBGNkYgRm', '赵六', 2, '13900000004', 's004@minor.edu.cn', 1, '2023', 1),
('S2024005', 'student5',  '$2a$10$Ef47dBhtP3xJF4xsaDyUBefhzFfjb9ikm3UpCrMmjdPEBBGNkYgRm', '陈七', 1, '13900000005', 's005@minor.edu.cn', 1, '2023', 1),
-- 数学学院 (4人)
('S2024006', 'student6',  '$2a$10$Ef47dBhtP3xJF4xsaDyUBefhzFfjb9ikm3UpCrMmjdPEBBGNkYgRm', '周杰', 1, '13900000006', 's006@minor.edu.cn', 2, '2024', 1),
('S2024007', 'student7',  '$2a$10$Ef47dBhtP3xJF4xsaDyUBefhzFfjb9ikm3UpCrMmjdPEBBGNkYgRm', '吴娜', 2, '13900000007', 's007@minor.edu.cn', 2, '2024', 1),
('S2024008', 'student8',  '$2a$10$Ef47dBhtP3xJF4xsaDyUBefhzFfjb9ikm3UpCrMmjdPEBBGNkYgRm', '郑浩', 1, '13900000008', 's008@minor.edu.cn', 2, '2023', 1),
('S2024009', 'student9',  '$2a$10$Ef47dBhtP3xJF4xsaDyUBefhzFfjb9ikm3UpCrMmjdPEBBGNkYgRm', '冯雪', 2, '13900000009', 's009@minor.edu.cn', 2, '2024', 1),
-- 外国语学院 (3人)
('S2024010', 'student10', '$2a$10$Ef47dBhtP3xJF4xsaDyUBefhzFfjb9ikm3UpCrMmjdPEBBGNkYgRm', '韩梅', 2, '13900000010', 's010@minor.edu.cn', 3, '2024', 1),
('S2024011', 'student11', '$2a$10$Ef47dBhtP3xJF4xsaDyUBefhzFfjb9ikm3UpCrMmjdPEBBGNkYgRm', '杨帆', 1, '13900000011', 's011@minor.edu.cn', 3, '2024', 1),
('S2024012', 'student12', '$2a$10$Ef47dBhtP3xJF4xsaDyUBefhzFfjb9ikm3UpCrMmjdPEBBGNkYgRm', '朱琳', 2, '13900000012', 's012@minor.edu.cn', 3, '2023', 1),
-- 经管学院 (3人)
('S2024013', 'student13', '$2a$10$Ef47dBhtP3xJF4xsaDyUBefhzFfjb9ikm3UpCrMmjdPEBBGNkYgRm', '胡涛', 1, '13900000013', 's013@minor.edu.cn', 4, '2024', 1),
('S2024014', 'student14', '$2a$10$Ef47dBhtP3xJF4xsaDyUBefhzFfjb9ikm3UpCrMmjdPEBBGNkYgRm', '林静', 2, '13900000014', 's014@minor.edu.cn', 4, '2024', 1),
('S2024015', 'student15', '$2a$10$Ef47dBhtP3xJF4xsaDyUBefhzFfjb9ikm3UpCrMmjdPEBBGNkYgRm', '黄磊', 1, '13900000015', 's015@minor.edu.cn', 4, '2023', 1),
-- 电子信息学院 (3人)
('S2024016', 'student16', '$2a$10$Ef47dBhtP3xJF4xsaDyUBefhzFfjb9ikm3UpCrMmjdPEBBGNkYgRm', '徐超', 1, '13900000016', 's016@minor.edu.cn', 5, '2024', 1),
('S2024017', 'student17', '$2a$10$Ef47dBhtP3xJF4xsaDyUBefhzFfjb9ikm3UpCrMmjdPEBBGNkYgRm', '马丽', 2, '13900000017', 's017@minor.edu.cn', 5, '2024', 1),
('S2024018', 'student18', '$2a$10$Ef47dBhtP3xJF4xsaDyUBefhzFfjb9ikm3UpCrMmjdPEBBGNkYgRm', '高飞', 1, '13900000018', 's018@minor.edu.cn', 5, '2023', 1),
-- 艺术学院 (2人)
('S2024019', 'student19', '$2a$10$Ef47dBhtP3xJF4xsaDyUBefhzFfjb9ikm3UpCrMmjdPEBBGNkYgRm', '罗丹', 2, '13900000019', 's019@minor.edu.cn', 6, '2024', 1),
('S2024020', 'student20', '$2a$10$Ef47dBhtP3xJF4xsaDyUBefhzFfjb9ikm3UpCrMmjdPEBBGNkYgRm', '梁博', 1, '13900000020', 's020@minor.edu.cn', 6, '2024', 1);

-- =============================================
-- 5. 课程 (10门课程, 覆盖不同学院和教师)
-- =============================================
INSERT INTO `course` (`course_no`, `course_name`, `description`, `credit`, `teacher_id`, `college_id`, `max_students`, `current_count`, `semester`, `classroom`, `schedule`, `status`) VALUES
-- 计算机学院课程
('CS101', 'Python程序设计', 'Python基础语法、面向对象编程、文件处理、常用库', 3.0, 1, 1, 60, 0, '2025-2026-1', 'A301', '周一3-4节', 1),
('CS102', '数据结构与算法', '线性表、树、图、排序算法、查找算法', 4.0, 2, 1, 50, 0, '2025-2026-1', 'A302', '周二5-6节', 1),
-- 数学学院课程
('MA101', '高等数学A', '极限、导数、积分、级数', 5.0, 3, 2, 80, 0, '2025-2026-1', 'B201', '周三1-2节', 1),
('MA102', '线性代数', '行列式、矩阵、向量空间、特征值', 3.0, 4, 2, 70, 0, '2025-2026-1', 'B202', '周四3-4节', 1),
-- 外国语学院课程
('FL101', '大学英语四级', '听力、阅读、写作、翻译', 2.0, 5, 3, 100, 0, '2025-2026-1', 'C101', '周五1-2节', 1),
('FL102', '日语入门', '日语五十音、基础语法、日常会话', 2.0, 5, 3, 40, 0, '2025-2026-1', 'C102', '周六1-2节', 1),
-- 经管学院课程
('EM101', '管理学原理', '管理理论、计划、组织、领导、控制', 3.0, 6, 4, 90, 0, '2025-2026-1', 'D101', '周一5-6节', 1),
('EM102', '会计学基础', '会计要素、借贷记账、财务报表', 3.0, 6, 4, 60, 0, '2025-2026-1', 'D102', '周三5-6节', 1),
-- 电子信息学院课程
('EE101', '电路分析基础', '电路基本定律、直流电路、交流电路', 4.0, 7, 5, 50, 0, '2025-2026-1', 'E201', '周二1-2节', 1),
-- 艺术学院课程
('AD101', '设计素描', '素描基础、构图、光影、透视', 2.0, 8, 6, 30, 0, '2025-2026-1', 'F101', '周五3-4节', 1);

-- =============================================
-- 6. 选课记录 (学生选择不同课程, 覆盖跨学院选课)
-- =============================================
-- CS101 Python程序设计 (学生1-8选了这门课)
INSERT INTO `course_selection` (`student_id`, `course_id`, `status`) VALUES
(1, 1, 1), (2, 1, 1), (3, 1, 1), (4, 1, 1), (5, 1, 1),
(6, 1, 1), (7, 1, 1), (8, 1, 1);

-- CS102 数据结构与算法 (学生1-6选了这门课)
INSERT INTO `course_selection` (`student_id`, `course_id`, `status`) VALUES
(1, 2, 1), (2, 2, 1), (3, 2, 1), (4, 2, 1), (5, 2, 1), (6, 2, 1);

-- MA101 高等数学A (学生6-12选了这门课, 含跨学院)
INSERT INTO `course_selection` (`student_id`, `course_id`, `status`) VALUES
(6, 3, 1), (7, 3, 1), (8, 3, 1), (9, 3, 1), (10, 3, 1), (11, 3, 1), (12, 3, 1);

-- MA102 线性代数 (学生6-10选了这门课)
INSERT INTO `course_selection` (`student_id`, `course_id`, `status`) VALUES
(6, 4, 1), (7, 4, 1), (8, 4, 1), (9, 4, 1), (10, 4, 1);

-- FL101 大学英语四级 (学生1-15, 大量学生选课)
INSERT INTO `course_selection` (`student_id`, `course_id`, `status`) VALUES
(1, 5, 1), (2, 5, 1), (3, 5, 1), (4, 5, 1), (5, 5, 1),
(6, 5, 1), (7, 5, 1), (8, 5, 1), (9, 5, 1), (10, 5, 1),
(11, 5, 1), (12, 5, 1), (13, 5, 1), (14, 5, 1), (15, 5, 1);

-- FL102 日语入门 (学生10-12选了这门课)
INSERT INTO `course_selection` (`student_id`, `course_id`, `status`) VALUES
(10, 6, 1), (11, 6, 1), (12, 6, 1);

-- EM101 管理学原理 (学生13-18选了这门课, 含跨学院)
INSERT INTO `course_selection` (`student_id`, `course_id`, `status`) VALUES
(13, 7, 1), (14, 7, 1), (15, 7, 1), (16, 7, 1), (17, 7, 1), (18, 7, 1);

-- EM102 会计学基础 (学生13-15选了这门课)
INSERT INTO `course_selection` (`student_id`, `course_id`, `status`) VALUES
(13, 8, 1), (14, 8, 1), (15, 8, 1);

-- EE101 电路分析基础 (学生16-18选了这门课)
INSERT INTO `course_selection` (`student_id`, `course_id`, `status`) VALUES
(16, 9, 1), (17, 9, 1), (18, 9, 1);

-- AD101 设计素描 (学生19-20选了这门课)
INSERT INTO `course_selection` (`student_id`, `course_id`, `status`) VALUES
(19, 10, 1), (20, 10, 1);

-- 更新各课程的已选人数
UPDATE `course` SET `current_count` = (SELECT COUNT(*) FROM `course_selection` WHERE `course_id` = `course`.`id` AND `status` = 1);

-- =============================================
-- 7. 成绩数据 (覆盖各分数段, 用于测试统计和AI报告)
-- =============================================

-- CS101 Python程序设计 (8人, 成绩分布: 1个优秀, 3个良好, 2个中等, 1个及格, 1个不及格)
INSERT INTO `score` (`student_id`, `course_id`, `score`, `grade_level`, `exam_type`) VALUES
(1,  1, 95.0,  '优秀', '期末考试'),
(2,  1, 88.0,  '良好', '期末考试'),
(3,  1, 82.0,  '良好', '期末考试'),
(4,  1, 85.0,  '良好', '期末考试'),
(5,  1, 73.0,  '中等', '期末考试'),
(6,  1, 76.0,  '中等', '期末考试'),
(7,  1, 62.0,  '及格', '期末考试'),
(8,  1, 45.0,  '不及格', '期末考试');

-- CS102 数据结构与算法 (6人, 成绩分布: 2个优秀, 2个良好, 1个中等, 1个不及格)
INSERT INTO `score` (`student_id`, `course_id`, `score`, `grade_level`, `exam_type`) VALUES
(1,  2, 92.0,  '优秀', '期末考试'),
(2,  2, 91.0,  '优秀', '期末考试'),
(3,  2, 78.0,  '良好', '期末考试'),
(4,  2, 81.0,  '良好', '期末考试'),
(5,  2, 70.0,  '中等', '期末考试'),
(6,  2, 55.0,  '不及格', '期末考试');

-- MA101 高等数学A (7人, 成绩分布: 1个优秀, 2个良好, 2个中等, 1个及格, 1个不及格)
INSERT INTO `score` (`student_id`, `course_id`, `score`, `grade_level`, `exam_type`) VALUES
(6,  3, 93.0,  '优秀', '期末考试'),
(7,  3, 84.0,  '良好', '期末考试'),
(8,  3, 79.0,  '良好', '期末考试'),
(9,  3, 72.0,  '中等', '期末考试'),
(10, 3, 75.0,  '中等', '期末考试'),
(11, 3, 61.0,  '及格', '期末考试'),
(12, 3, 42.0,  '不及格', '期末考试');

-- MA102 线性代数 (5人, 成绩分布: 1个优秀, 2个良好, 1个中等, 1个及格)
INSERT INTO `score` (`student_id`, `course_id`, `score`, `grade_level`, `exam_type`) VALUES
(6,  4, 96.0,  '优秀', '期末考试'),
(7,  4, 83.0,  '良好', '期末考试'),
(8,  4, 87.0,  '良好', '期末考试'),
(9,  4, 74.0,  '中等', '期末考试'),
(10, 4, 65.0,  '及格', '期末考试');

-- FL101 大学英语四级 (15人, 成绩分布: 3个优秀, 4个良好, 3个中等, 3个及格, 2个不及格)
INSERT INTO `score` (`student_id`, `course_id`, `score`, `grade_level`, `exam_type`) VALUES
(1,  5, 92.0,  '优秀', '期末考试'),
(2,  5, 88.0,  '良好', '期末考试'),
(3,  5, 91.0,  '优秀', '期末考试'),
(4,  5, 76.0,  '中等', '期末考试'),
(5,  5, 63.0,  '及格', '期末考试'),
(6,  5, 85.0,  '良好', '期末考试'),
(7,  5, 72.0,  '中等', '期末考试'),
(8,  5, 58.0,  '不及格', '期末考试'),
(9,  5, 89.0,  '良好', '期末考试'),
(10, 5, 94.0,  '优秀', '期末考试'),
(11, 5, 78.0,  '中等', '期末考试'),
(12, 5, 67.0,  '及格', '期末考试'),
(13, 5, 55.0,  '不及格', '期末考试'),
(14, 5, 81.0,  '良好', '期末考试'),
(15, 5, 62.0,  '及格', '期末考试');

-- FL102 日语入门 (3人, 成绩分布: 1个优秀, 1个良好, 1个中等)
INSERT INTO `score` (`student_id`, `course_id`, `score`, `grade_level`, `exam_type`) VALUES
(10, 6, 90.0,  '优秀', '期末考试'),
(11, 6, 82.0,  '良好', '期末考试'),
(12, 6, 71.0,  '中等', '期末考试');

-- EM101 管理学原理 (6人, 成绩分布: 1个优秀, 2个良好, 2个中等, 1个及格)
INSERT INTO `score` (`student_id`, `course_id`, `score`, `grade_level`, `exam_type`) VALUES
(13, 7, 91.0,  '优秀', '期末考试'),
(14, 7, 84.0,  '良好', '期末考试'),
(15, 7, 78.0,  '良好', '期末考试'),
(16, 7, 73.0,  '中等', '期末考试'),
(17, 7, 70.0,  '中等', '期末考试'),
(18, 7, 64.0,  '及格', '期末考试');

-- EM102 会计学基础 (3人, 成绩分布: 1个优秀, 1个良好, 1个中等)
INSERT INTO `score` (`student_id`, `course_id`, `score`, `grade_level`, `exam_type`) VALUES
(13, 8, 95.0,  '优秀', '期末考试'),
(14, 8, 86.0,  '良好', '期末考试'),
(15, 8, 77.0,  '中等', '期末考试');

-- EE101 电路分析基础 (3人, 成绩分布: 1个优秀, 1个良好, 1个不及格)
INSERT INTO `score` (`student_id`, `course_id`, `score`, `grade_level`, `exam_type`) VALUES
(16, 9, 93.0,  '优秀', '期末考试'),
(17, 9, 80.0,  '良好', '期末考试'),
(18, 9, 48.0,  '不及格', '期末考试');

-- AD101 设计素描 (2人, 成绩分布: 1个优秀, 1个良好)
INSERT INTO `score` (`student_id`, `course_id`, `score`, `grade_level`, `exam_type`) VALUES
(19, 10, 97.0, '优秀', '期末考试'),
(20, 10, 85.0, '良好', '期末考试');

-- =============================================
-- 数据统计概览
-- =============================================
-- 学院: 6个
-- 教师: 8位 (密码均为 123456)
-- 学生: 20位 (密码均为 123456)
-- 课程: 10门
-- 选课记录: 58条
-- 成绩记录: 58条
-- 分数段分布: 不及格(7人) 及格(9人) 中等(13人) 良好(17人) 优秀(12人)
-- =============================================
