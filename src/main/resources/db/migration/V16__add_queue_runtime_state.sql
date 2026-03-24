CREATE TABLE queue_runtime_state
(
    queue_plan_id      UUID NOT NULL,
    current_request_id UUID,
    taken_at           TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_queue_runtime_state PRIMARY KEY (queue_plan_id)
);

ALTER TABLE queue_runtime_state
    ADD CONSTRAINT FK_QUEUE_RUNTIME_STATE_ON_CURRENT_REQUEST FOREIGN KEY (current_request_id) REFERENCES submission_requests (id);

ALTER TABLE queue_runtime_state
    ADD CONSTRAINT FK_QUEUE_RUNTIME_STATE_ON_QUEUE_PLAN FOREIGN KEY (queue_plan_id) REFERENCES queue_plans (id);