ALTER TABLE work_types
    DROP CONSTRAINT fk_work_types_on_discipline;

ALTER TABLE work_types
    ADD CONSTRAINT fk_work_types_on_discipline
    FOREIGN KEY (discipline_id) REFERENCES disciplines(id)
    ON DELETE CASCADE;