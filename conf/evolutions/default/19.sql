# --- !Ups

ALTER TABLE votes ADD INDEX user_id_index(user_id);
ALTER TABLE votes ADD INDEX law_id_index(law_proposal_id);

# --- !Downs

ALTER TABLE votes DROP INDEX user_id_index;
ALTER TABLE votes DROP INDEX law_id_index;