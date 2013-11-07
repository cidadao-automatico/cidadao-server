# --- !Ups

ALTER TABLE law_tags ADD PRIMARY KEY law_tags_index(tag_id, law_proposal_id);
ALTER TABLE law_tags ADD INDEX law_tags_proposal_index(law_proposal_id);
ALTER TABLE law_tags ADD INDEX law_tags_tag_index(tag_id);

# --- !Downs

ALTER TABLE law_tags DROP PRIMARY KEY law_tags_index;
ALTER TABLE law_tags DROP INDEX law_proposal_tag_index;
ALTER TABLE law_tags DROP INDEX law_tags_tag_index;