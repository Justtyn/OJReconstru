INSERT INTO admin (username, password, name, sex, created_at, updated_at)
VALUES ('root', 'passhash', '超级管理员', 'male', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('alice', 'passhash', 'Alice', 'female', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO teacher (username, password, name, sex, created_at, updated_at)
VALUES ('t1', 'pwd', 'Teacher One', 'male', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO classes (name, creator_id, code, created_at, updated_at)
VALUES ('Class A', 1, 'CODEA', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
-- 预置 student / classes_member / login_log 数据以满足测试
INSERT INTO student (username, password, name, sex, score, create_time, updated_at)
VALUES ('stu1', 'pwd', 'Student One', 'male', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 班级成员：classId=1, studentId=1
INSERT INTO classes_member (class_id, member_type, student_id, joined_at)
VALUES (1, 'student', 1, CURRENT_TIMESTAMP);

-- 登录日志：role=student, user_id=1
INSERT INTO login_log (role, user_id, username, ip_address, device, login_time, success, created_at, updated_at)
VALUES ('student', 1, 'stu1', '127.0.0.1', 'Windows', CURRENT_TIMESTAMP, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
