# --- !Ups

ALTER TABLE congressman_infos ADD COLUMN congress_id INT NULL;
ALTER TABLE congressman_infos ADD COLUMN source_id INT NULL;
ALTER TABLE congressman_infos ADD COLUMN short_name VARCHAR(200) NULL;
ALTER TABLE congressman_infos ADD COLUMN photo_url VARCHAR(4000) NULL;
ALTER TABLE congressman_infos ADD COLUMN phone_number VARCHAR(30) NULL;
ALTER TABLE congressman_infos ADD COLUMN email VARCHAR(100) NULL;


# --- !Downs

ALTER TABLE congressman_infos DROP COLUMN congress_id;
ALTER TABLE congressman_infos DROP COLUMN source_id;
ALTER TABLE congressman_infos DROP COLUMN short_name;
ALTER TABLE congressman_infos DROP COLUMN photo_url;
ALTER TABLE congressman_infos DROP COLUMN phone_number;
ALTER TABLE congressman_infos DROP COLUMN email;
