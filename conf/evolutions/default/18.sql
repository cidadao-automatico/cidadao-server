# --- !Ups

ALTER TABLE congressman_infos ADD INDEX congressman_id_index(congress_id);
ALTER TABLE congressman_infos ADD INDEX source_id_index(source_id);

# --- !Downs

ALTER TABLE congressman_infos DROP INDEX congressman_id_index;
ALTER TABLE congressman_infos DROP INDEX source_id_index;