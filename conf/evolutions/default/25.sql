# --- !Ups

ALTER TABLE law_proposals ADD COLUMN priority_status VARCHAR(100) NULL;

# --- !Downs

ALTER TABLE law_proposals DROP COLUMN priority_status;