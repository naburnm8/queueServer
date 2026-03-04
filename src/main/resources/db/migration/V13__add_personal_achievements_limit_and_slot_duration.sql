ALTER TABLE disciplines
    ADD personal_achievements_score_limit INTEGER;

ALTER TABLE disciplines
    ALTER COLUMN personal_achievements_score_limit SET NOT NULL;

ALTER TABLE queue_plans
    ADD slot_duration_minutes INTEGER;

ALTER TABLE queue_plans
    ALTER COLUMN slot_duration_minutes SET NOT NULL;