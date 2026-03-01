CREATE TABLE queue_rules
(
    id            UUID        NOT NULL,
    queue_plan_id UUID        NOT NULL,
    type          VARCHAR(32) NOT NULL,
    enabled       BOOLEAN     NOT NULL,
    payload       JSONB       NOT NULL,
    CONSTRAINT pk_queue_rules PRIMARY KEY (id)
);

ALTER TABLE custom_parameters
    ADD payload JSONB;

ALTER TABLE custom_parameters
    ADD queue_plan_id UUID;

ALTER TABLE custom_parameters
    ALTER COLUMN payload SET NOT NULL;

ALTER TABLE custom_parameters
    ALTER COLUMN queue_plan_id SET NOT NULL;

ALTER TABLE custom_parameters
    ADD CONSTRAINT FK_CUSTOM_PARAMETERS_ON_QUEUE_PLAN FOREIGN KEY (queue_plan_id) REFERENCES queue_plans (id);

CREATE INDEX ix_custom_parameters_plan ON custom_parameters (queue_plan_id);

ALTER TABLE queue_rules
    ADD CONSTRAINT FK_QUEUE_RULES_ON_QUEUE_PLAN FOREIGN KEY (queue_plan_id) REFERENCES queue_plans (id);

CREATE INDEX ix_queue_rules_plan ON queue_rules (queue_plan_id);

ALTER TABLE custom_parameters
DROP
COLUMN parameter_body;