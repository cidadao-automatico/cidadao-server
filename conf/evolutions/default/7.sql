# --- !Ups

ALTER TABLE user_tags ADD UNIQUE KEY user_tag_key(user_id,tag_id);

# --- !Downs

ALTER TABLE user_tags DROP KEY user_tag_key;