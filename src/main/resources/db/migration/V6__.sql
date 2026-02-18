CREATE TABLE disciplines
(
    id   UUID         NOT NULL,
    name VARCHAR(255) NOT NULL,
    CONSTRAINT pk_disciplines PRIMARY KEY (id)
);

CREATE TABLE disciplines_owners
(
    discipline_id UUID NOT NULL,
    teacher_id    UUID NOT NULL,
    CONSTRAINT pk_disciplines_owners PRIMARY KEY (discipline_id, teacher_id)
);

CREATE TABLE work_types
(
    id                     UUID         NOT NULL,
    name                   VARCHAR(255) NOT NULL,
    estimated_time_minutes INTEGER      NOT NULL,
    discipline_id          UUID         NOT NULL,
    CONSTRAINT pk_work_types PRIMARY KEY (id)
);

ALTER TABLE work_types
    ADD CONSTRAINT FK_WORK_TYPES_ON_DISCIPLINE FOREIGN KEY (discipline_id) REFERENCES disciplines (id);

ALTER TABLE disciplines_owners
    ADD CONSTRAINT fk_disown_on_discipline FOREIGN KEY (discipline_id) REFERENCES disciplines (id);

ALTER TABLE disciplines_owners
    ADD CONSTRAINT fk_disown_on_teacher FOREIGN KEY (teacher_id) REFERENCES teachers (user_id);