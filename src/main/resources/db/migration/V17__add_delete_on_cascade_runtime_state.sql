ALTER TABLE queue_runtime_state
    DROP CONSTRAINT FK_QUEUE_RUNTIME_STATE_ON_QUEUE_PLAN;

ALTER TABLE queue_runtime_state
    ADD CONSTRAINT FK_QUEUE_RUNTIME_STATE_ON_QUEUE_PLAN FOREIGN KEY (queue_plan_id) REFERENCES queue_plans (id) on delete cascade ;
