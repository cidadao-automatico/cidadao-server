# --- !Ups

ALTER TABLE users ADD COLUMN configured BOOLEAN NOT NULL DEFAULT 0;

# --- !Downs

ALTER TABLE users DROP COLUMN configured;
