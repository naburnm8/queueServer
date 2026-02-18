ALTER TABLE refresh_tokens
    DROP CONSTRAINT fk_refresh_tokens_on_user;

ALTER TABLE refresh_tokens
    ADD CONSTRAINT fk_refresh_tokens_on_user
    FOREIGN KEY (user_id) REFERENCES users(id)
    ON DELETE CASCADE;

ALTER TABLE students
    DROP CONSTRAINT fk_students_on_user;

ALTER TABLE students
    ADD CONSTRAINT fk_students_on_user
    FOREIGN KEY (user_id) REFERENCES users(id)
    ON DELETE CASCADE;

ALTER TABLE teachers
    DROP CONSTRAINT fk_teachers_on_user;

ALTER TABLE teachers
    ADD CONSTRAINT fk_teachers_on_user
    FOREIGN KEY (user_id) REFERENCES users(id)
    ON DELETE CASCADE;