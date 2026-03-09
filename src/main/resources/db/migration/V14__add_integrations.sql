CREATE TABLE integrations
(
    id      UUID         NOT NULL,
    name    VARCHAR(255) NOT NULL,
    payload JSONB        NOT NULL,
    CONSTRAINT pk_integrations PRIMARY KEY (id)
);

ALTER TABLE users
    ADD created_with_integration_id UUID;

ALTER TABLE users
    ADD integration_id UUID;

ALTER TABLE users
    ADD CONSTRAINT FK_USERS_ON_CREATEDWITHINTEGRATION FOREIGN KEY (created_with_integration_id) REFERENCES integrations (id);