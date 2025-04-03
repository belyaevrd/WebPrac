INSERT INTO Users(login, fullName, role, password) VALUES
('login', 'Name', 'student', 'password'),
('a.gerasimov', 'Artur Gerasimov', 'teacher', 'ag1234'),
('m.zhukov',    'Maksim Zhukov', 'teacher', 'mz1234'),
('t.kuznecova', 'Tatyana Kuznecova', 'teacher', 'tk1234'),
('e.alekseeva', 'Evgeniya Alekseeva', 'student', 'ea1234'),
('a.volkova',   'Alina Volkova', 'student', 'av1234'),
('n.egorov',    'Nikolay Egorov', 'student', 'ne1234');

INSERT INTO Courses(name, description) VALUES
('Изучение C', 'Курс по языку программирования C'),
('Изучение C++', 'Курс по языку программирования C++'),
('Изучение Java', 'Курс по языку программирования Java'),
('Изучение Python', 'Курс по языку программирования Python'),
('Изучение Rust', 'Курс по языку программирования Rust');

INSERT INTO Lessons(title, course, begining, ending) VALUES
('Урок 1. Введение', 1,          '2026-02-01 15:00:00', '2026-02-01 16:00:00'),
('Урок 2. Функции', 1,          '2026-02-02 15:00:00', '2026-03-01 16:00:00'),
('Урок 3. Структуры данных', 1, '2026-02-03 17:30:00', '2026-03-01 18:30:00'),
('Урок 4. Макросы', 1,          '2026-02-04 12:00:00', '2026-04-01 13:00:00'),
('Урок 5. Библиотеки', 1,       '2026-02-05 17:30:00', '2026-04-01 18:30:00'),
('Урок 1. Введение', 2,         '2026-02-02 18:30:00', '2026-05-01 19:30:00'),
('Урок 2. Функции', 2,          '2026-02-04 18:30:00', '2026-05-01 19:30:00');

select * from users;

INSERT INTO TeachersCourses(user_id, course_id) VALUES
(2, 1),
(3, 1),
(4, 2),
(2, 3),
(4, 3);

INSERT INTO StudentsCourses(user_id, course_id) VALUES
(5, 1),
(1, 1),
(1, 2),
(5, 3),
(6, 3);
