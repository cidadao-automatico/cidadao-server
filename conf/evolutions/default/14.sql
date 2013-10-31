# --- !Ups

ALTER TABLE votes ADD COLUMN predicted_vote INT NULL;

# --- !Downs

ALTER TABLE votes DROP COLUMN predicted_vote INT NULL;