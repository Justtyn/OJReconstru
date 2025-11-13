DROP TABLE IF EXISTS admin;
CREATE TABLE admin (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(64) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  name VARCHAR(100),
  sex VARCHAR(16),
  birth DATE,
  phone VARCHAR(32),
  email VARCHAR(255),
  avatar VARCHAR(512),
  last_login_ip VARCHAR(45),
  last_login_time TIMESTAMP NULL,
  created_at TIMESTAMP NULL,
  updated_at TIMESTAMP NULL
);

DROP TABLE IF EXISTS teacher;
CREATE TABLE teacher (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(64) NOT NULL UNIQUE,
  password VARCHAR(255),
  name VARCHAR(100),
  sex VARCHAR(16),
  phone VARCHAR(32),
  email VARCHAR(255),
  avatar VARCHAR(512),
  class_id BIGINT,
  title VARCHAR(128),
  last_login_time TIMESTAMP NULL,
  created_at TIMESTAMP NULL,
  updated_at TIMESTAMP NULL
);

DROP TABLE IF EXISTS classes;
CREATE TABLE classes (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  creator_id BIGINT,
  code VARCHAR(64),
  start_date DATE,
  end_date DATE,
  description VARCHAR(1024),
  created_at TIMESTAMP NULL,
  updated_at TIMESTAMP NULL
);

DROP TABLE IF EXISTS student;
CREATE TABLE student (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(64) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  name VARCHAR(100),
  sex VARCHAR(16),
  birth DATE,
  phone VARCHAR(32),
  email VARCHAR(255),
  avatar VARCHAR(512),
  background VARCHAR(512),
  ac INT,
  submit INT,
  school VARCHAR(255),
  score INT,
  last_login_time TIMESTAMP NULL,
  last_language VARCHAR(64),
  create_time TIMESTAMP NULL,
  last_visit_time TIMESTAMP NULL,
  class_id BIGINT,
  daily_challenge VARCHAR(255),
  register_ip VARCHAR(45),
  last_login_ip VARCHAR(45),
  bio VARCHAR(1024),
  is_verified BOOLEAN,
  updated_at TIMESTAMP NULL
);

DROP TABLE IF EXISTS classes_member;
CREATE TABLE classes_member (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  class_id BIGINT NOT NULL,
  member_type VARCHAR(16) NOT NULL,
  student_id BIGINT,
  teacher_id BIGINT,
  joined_at TIMESTAMP NULL,
  left_at TIMESTAMP NULL
);

DROP TABLE IF EXISTS login_log;
CREATE TABLE login_log (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  role VARCHAR(16) NOT NULL,
  user_id BIGINT NOT NULL,
  username VARCHAR(64) NOT NULL,
  ip_address VARCHAR(45),
  location VARCHAR(128),
  user_agent VARCHAR(512),
  device VARCHAR(128),
  login_time TIMESTAMP NULL,
  logout_time TIMESTAMP NULL,
  success BOOLEAN,
  fail_reason VARCHAR(255),
  created_at TIMESTAMP NULL,
  updated_at TIMESTAMP NULL
);
