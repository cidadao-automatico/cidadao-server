# --- !Ups

ALTER TABLE user_representatives ADD UNIQUE KEY user_representative_key(user_id,congressman_id);

# --- !Downs

ALTER TABLE user_representatives DROP KEY user_representative_key;