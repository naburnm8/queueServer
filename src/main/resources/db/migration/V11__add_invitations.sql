CREATE TABLE invitation_students
(
    invitation_id UUID NOT NULL,
    student_id    UUID NOT NULL,
    CONSTRAINT pk_invitation_students PRIMARY KEY (invitation_id, student_id)
);

CREATE TABLE invitations
(
    id            UUID         NOT NULL,
    queue_plan_id UUID         NOT NULL,
    created_by    UUID         NOT NULL,
    enabled       BOOLEAN      NOT NULL,
    mode          VARCHAR(255) NOT NULL,
    code          VARCHAR(64),
    target_group  VARCHAR(64),
    created_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    expires_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    max_uses      INTEGER      NOT NULL,
    used_count    INTEGER      NOT NULL,
    CONSTRAINT pk_invitations PRIMARY KEY (id)
);

CREATE INDEX ix_invitations_code ON invitations (code);

CREATE INDEX ix_invitations_expires ON invitations (expires_at);

CREATE INDEX ix_invitations_group ON invitations (target_group);

ALTER TABLE invitations
    ADD CONSTRAINT FK_INVITATIONS_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES teachers (user_id);

ALTER TABLE invitations
    ADD CONSTRAINT FK_INVITATIONS_ON_QUEUE_PLAN FOREIGN KEY (queue_plan_id) REFERENCES queue_plans (id);

CREATE INDEX ix_invitations_queue_plan ON invitations (queue_plan_id);

ALTER TABLE invitation_students
    ADD CONSTRAINT fk_invstu_on_invitation FOREIGN KEY (invitation_id) REFERENCES invitations (id);

ALTER TABLE invitation_students
    ADD CONSTRAINT fk_invstu_on_student FOREIGN KEY (student_id) REFERENCES students (user_id);