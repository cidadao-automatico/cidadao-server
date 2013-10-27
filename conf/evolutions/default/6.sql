# --- !Ups

ALTER TABLE user_law_regions DROP COLUMN id;
ALTER TABLE user_law_regions ADD UNIQUE KEY user_law_region_key(user_id,law_region_id);

# --- !Downs

ALTER TABLE user_law_regions ADD COLUMN id INT NOT NULL AUTO_INCREMENT;
ALTER TABLE user_law_regions DROP KEY user_law_region_key;