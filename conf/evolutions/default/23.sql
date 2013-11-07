# --- !Ups

ALTER TABLE tags ADD COLUMN normalized_name INT NULL;
ALTER TABLE tags ADD INDEX normalized_name_index(normalized_name);

# --- !Downs

ALTER TABLE tags DROP COLUMN normalized_name;
ALTER TABLE tags ADD INDEX normalized_name_index;