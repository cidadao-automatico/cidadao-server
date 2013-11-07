# --- !Ups

ALTER TABLE users ADD COLUMN source_id INT NULL;
ALTER TABLE users ADD INDEX law_source_id_index(source_id);

# --- !Downs

ALTER TABLE users DROP COLUMN source_id;
ALTER TABLE users ADD INDEX law_source_id_index;