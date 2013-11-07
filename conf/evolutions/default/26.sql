# --- !Ups

ALTER TABLE law_tags DROP PRIMARY KEY;

# --- !Downs

ALTER TABLE law_tags ADD PRIMARY KEY law_tags_index(tag_id, law_proposal_id);

