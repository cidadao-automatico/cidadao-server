# --- !Ups

ALTER TABLE law_proposals ADD COLUMN vote_status BOOLEAN NOT NULL DEFAULT 0;

# --- !Downs

ALTER TABLE law_proposals DROP COLUMN vote_status BOOLEAN NOT NULL DEFAULT 0;