# --- !Ups

ALTER TABLE votes ADD UNIQUE KEY user_law_proposal_key(user_id,law_proposal_id);

# --- !Downs

ALTER TABLE votes DROP KEY user_law_proposal_key;