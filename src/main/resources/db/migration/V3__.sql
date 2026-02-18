CREATE TABLE students
(
    user_id        UUID         NOT NULL,
    first_name     VARCHAR(255) NOT NULL,
    last_name      VARCHAR(255) NOT NULL,
    patronymic     VARCHAR(255) NOT NULL,
    academic_group VARCHAR(255) NOT NULL,
    telegram       VARCHAR(255),
    avatar_url     VARCHAR(255),
    CONSTRAINT pk_students PRIMARY KEY (user_id)
);

CREATE TABLE teachers
(
    user_id    UUID         NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name  VARCHAR(255) NOT NULL,
    patronymic VARCHAR(255) NOT NULL,
    department VARCHAR(255) NOT NULL,
    telegram   VARCHAR(255),
    avatar_url VARCHAR(255),
    CONSTRAINT pk_teachers PRIMARY KEY (user_id)
);

CREATE INDEX idx_student_group ON students (academic_group);

CREATE INDEX idx_student_telegram ON students (telegram);

CREATE INDEX ix_teachers_department ON teachers (department);

CREATE INDEX ix_teachers_telegram ON teachers (telegram);

ALTER TABLE students
    ADD CONSTRAINT FK_STUDENTS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE teachers
    ADD CONSTRAINT FK_TEACHERS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);