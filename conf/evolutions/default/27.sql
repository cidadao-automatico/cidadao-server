# --- !Ups

ALTER TABLE users ADD COLUMN model_path VARCHAR(128) NULL;
ALTER TABLE parties ADD COLUMN model_path VARCHAR(128) NULL;

# --- !Downs

ALTER TABLE users DROP COLUMN model_path;
ALTER TABLE parties DROP COLUMN model_path;

