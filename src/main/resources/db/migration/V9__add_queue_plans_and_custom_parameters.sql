CREATE TABLE custom_parameters
(
    id              UUID NOT NULL,
    creator_user_id UUID,
    parameter_body  VARCHAR(255),
    CONSTRAINT pk_custom_parameters PRIMARY KEY (id)
);

CREATE TABLE queue_plans
(
    id               UUID             NOT NULL,
    discipline_id    UUID             NOT NULL,
    created_by       UUID             NOT NULL,
    title            VARCHAR(255)     NOT NULL,
    status           VARCHAR(255)     NOT NULL,
    use_debts        BOOLEAN          NOT NULL,
    w_debts          DOUBLE PRECISION NOT NULL,
    use_time         BOOLEAN          NOT NULL,
    w_time           DOUBLE PRECISION NOT NULL,
    use_achievements BOOLEAN          NOT NULL,
    w_achievements   DOUBLE PRECISION NOT NULL,
    created_at       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_queue_plans PRIMARY KEY (id)
);

ALTER TABLE custom_parameters
    ADD CONSTRAINT FK_CUSTOM_PARAMETERS_ON_CREATOR_USER FOREIGN KEY (creator_user_id) REFERENCES teachers (user_id);

ALTER TABLE queue_plans
    ADD CONSTRAINT FK_QUEUE_PLANS_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES teachers (user_id);

CREATE INDEX ix_queue_plans_creator ON queue_plans (created_by);

ALTER TABLE queue_plans
    ADD CONSTRAINT FK_QUEUE_PLANS_ON_DISCIPLINE FOREIGN KEY (discipline_id) REFERENCES disciplines (id);

CREATE INDEX ix_queue_plans_discipline ON queue_plans (discipline_id);