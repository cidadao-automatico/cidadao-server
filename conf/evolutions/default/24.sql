# --- !Ups

ALTER TABLE tags MODIFY COLUMN normalized_name VARCHAR(100) NULL;

# --- !Downs

ALTER TABLE tags MODIFY COLUMN normalized_name INT NULL;