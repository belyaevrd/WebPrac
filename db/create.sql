CREATE TYPE role_type as ENUM('Student', 'Teacher');

CREATE TABLE IF NOT EXISTS Users (
       user_id  SERIAL PRIMARY KEY,
       login    VARCHAR(100) NOT NULL,
       role     role_type,
       fullName VARCHAR(100) NOT NULL,
       password VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS Courses (
       course_id    SERIAL PRIMARY KEY,
       name         VARCHAR(100) NOT NULL,
       description  TEXT
);

CREATE TABLE IF NOT EXISTS Lessons (
       lesson_id    SERIAL PRIMARY KEY,
       title        VARCHAR(100) NOT NULL,
       course       INT REFERENCES Courses(course_id) ON DELETE CASCADE ON UPDATE CASCADE,
       begining     timestamp NOT NULL,
       ending       timestamp NOT NULL
);

CREATE TABLE IF NOT EXISTS StudentsCourses (
       user_id      INT REFERENCES Users(user_id) ON DELETE CASCADE ON UPDATE CASCADE,
       course_id    INT REFERENCES Courses(course_id) ON DELETE CASCADE ON UPDATE CASCADE,
       PRIMARY KEY(user_id, course_id)
);

CREATE TABLE IF NOT EXISTS TeachersCourses (
       user_id      INT REFERENCES Users(user_id) ON DELETE CASCADE ON UPDATE CASCADE,
       course_id    INT REFERENCES Courses(course_id) ON DELETE CASCADE ON UPDATE CASCADE,
       PRIMARY KEY(user_id, course_id)
);
