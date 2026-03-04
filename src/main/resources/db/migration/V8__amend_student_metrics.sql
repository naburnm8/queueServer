ALTER TABLE student_metrics
DROP
CONSTRAINT fk_student_metrics_on_student_user;

ALTER TABLE student_metrics
DROP
CONSTRAINT fk_student_metrics_on_teacher_user;

ALTER TABLE student_metrics
    ADD student_id UUID;

ALTER TABLE student_metrics
    ADD teacher_id UUID;

ALTER TABLE student_metrics
    ALTER COLUMN student_id SET NOT NULL;

ALTER TABLE student_metrics
    ALTER COLUMN teacher_id SET NOT NULL;

ALTER TABLE student_metrics
    ADD CONSTRAINT FK_STUDENT_METRICS_ON_STUDENT FOREIGN KEY (student_id) REFERENCES students (user_id);

ALTER TABLE student_metrics
    ADD CONSTRAINT FK_STUDENT_METRICS_ON_TEACHER FOREIGN KEY (teacher_id) REFERENCES teachers (user_id);

ALTER TABLE student_metrics
DROP
COLUMN student_user_id;

ALTER TABLE student_metrics
DROP
COLUMN teacher_user_id;