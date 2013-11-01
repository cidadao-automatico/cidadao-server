# --- !Ups

ALTER TABLE votes CHANGE COLUMN predicted_vote predicted_rate INT NULL;

# --- !Downs

ALTER TABLE votes CHANGE COLUMN predicted_rate predicted_vote INT NULL;