CREATE TABLE refresh_tokens
(
    id               UUID        NOT NULL,
    user_id          UUID        NOT NULL,
    token_hash       VARCHAR(64) NOT NULL,
    created_at       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    expires_at       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    revoked_at       TIMESTAMP WITHOUT TIME ZONE,
    replaced_by_hash VARCHAR(64),
    user_agent       VARCHAR(300),
    ip               VARCHAR(64),
    CONSTRAINT pk_refresh_tokens PRIMARY KEY (id)
);

ALTER TABLE refresh_tokens
    ADD CONSTRAINT uc_refresh_tokens_token_hash UNIQUE (token_hash);

CREATE UNIQUE INDEX ix_refresh_tokens_hash ON refresh_tokens (token_hash);

ALTER TABLE refresh_tokens
    ADD CONSTRAINT FK_REFRESH_TOKENS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

CREATE INDEX ix_refresh_tokens_user_id ON refresh_tokens (user_id);