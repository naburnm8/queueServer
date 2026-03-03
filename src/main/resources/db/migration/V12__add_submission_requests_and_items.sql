CREATE TABLE submission_request_items
(
    id               UUID    NOT NULL,
    request_id       UUID,
    work_type_id     UUID    NOT NULL,
    quantity         INTEGER NOT NULL,
    minutes_override INTEGER,
    CONSTRAINT pk_submission_request_items PRIMARY KEY (id)
);

CREATE TABLE submission_requests
(
    id            UUID         NOT NULL,
    queue_plan_id UUID         NOT NULL,
    student_id    UUID         NOT NULL,
    status        VARCHAR(255) NOT NULL,
    created_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_submission_requests PRIMARY KEY (id)
);

CREATE INDEX ix_requests_status ON submission_requests (status);

ALTER TABLE submission_requests
    ADD CONSTRAINT FK_SUBMISSION_REQUESTS_ON_QUEUE_PLAN FOREIGN KEY (queue_plan_id) REFERENCES queue_plans (id);

CREATE INDEX ix_requests_queue ON submission_requests (queue_plan_id);

ALTER TABLE submission_requests
    ADD CONSTRAINT FK_SUBMISSION_REQUESTS_ON_STUDENT FOREIGN KEY (student_id) REFERENCES students (user_id);

CREATE INDEX ix_requests_student ON submission_requests (student_id);

ALTER TABLE submission_request_items
    ADD CONSTRAINT FK_SUBMISSION_REQUEST_ITEMS_ON_REQUEST FOREIGN KEY (request_id) REFERENCES submission_requests (id);

CREATE INDEX ix_request_items_request ON submission_request_items (request_id);

ALTER TABLE submission_request_items
    ADD CONSTRAINT FK_SUBMISSION_REQUEST_ITEMS_ON_WORK_TYPE FOREIGN KEY (work_type_id) REFERENCES work_types (id);

CREATE INDEX ix_request_items_work_type ON submission_request_items (work_type_id);