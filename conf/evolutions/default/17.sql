# --- !Ups

ALTER TABLE tags MODIFY COLUMN name VARCHAR(200) NOT NULL;

# --- !Downs

ALTER TABLE tags MODIFY COLUMN name VARCHAR(40) NOT NULL;