CREATE TABLE student_metrics
(
    id                         UUID    NOT NULL,
    discipline_id              UUID    NOT NULL,
    teacher_user_id            UUID    NOT NULL,
    student_user_id            UUID    NOT NULL,
    debts_count                INTEGER NOT NULL,
    personal_achievments_score INTEGER NOT NULL,
    CONSTRAINT pk_student_metrics PRIMARY KEY (id)
);

ALTER TABLE student_metrics
    ADD CONSTRAINT FK_STUDENT_METRICS_ON_DISCIPLINE FOREIGN KEY (discipline_id) REFERENCES disciplines (id);

ALTER TABLE student_metrics
    ADD CONSTRAINT FK_STUDENT_METRICS_ON_STUDENT_USER FOREIGN KEY (student_user_id) REFERENCES students (user_id);

ALTER TABLE student_metrics
    ADD CONSTRAINT FK_STUDENT_METRICS_ON_TEACHER_USER FOREIGN KEY (teacher_user_id) REFERENCES teachers (user_id);