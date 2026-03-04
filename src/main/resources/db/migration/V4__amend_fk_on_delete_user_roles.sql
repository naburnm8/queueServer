ALTER TABLE user_roles
    DROP CONSTRAINT fk_userol_on_user;

ALTER TABLE user_roles
    ADD CONSTRAINT fk_userol_on_user
    FOREIGN KEY (user_id) REFERENCES users(id)
    ON DELETE CASCADE;

ALTER TABLE user_roles
    DROP CONSTRAINT fk_userol_on_role;

ALTER TABLE user_roles
    ADD CONSTRAINT fk_userol_on_role
    FOREIGN KEY (role_id) REFERENCES roles(id)
    ON DELETE CASCADE