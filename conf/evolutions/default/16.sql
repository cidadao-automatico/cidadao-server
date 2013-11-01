# --- !Ups

ALTER TABLE tags ADD INDEX name_index(name);

# --- !Downs

ALTER TABLE tags DROP INDEX name_index;