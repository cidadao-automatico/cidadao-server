# --- !Ups

ALTER TABLE votes MODIFY COLUMN rate INT NULL;

# --- !Downs

ALTER TABLE votes MODIFY COLUMN rate INT NOT NULL;


