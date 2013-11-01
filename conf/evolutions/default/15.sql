# --- !Ups

ALTER TABLE law_proposals ADD COLUMN prefix VARCHAR(200) NULL;
ALTER TABLE law_proposals MODIFY COLUMN law_author_id INT NULL;
ALTER TABLE law_proposals MODIFY COLUMN law_type_id INT NULL;

# --- !Downs

ALTER TABLE law_proposals DROP COLUMN prefix VARCHAR(200) NULL;
ALTER TABLE law_proposals MODIFY COLUMN law_author_id INT NOT NULL;
ALTER TABLE law_proposals MODIFY COLUMN law_type_id INT NOT NULL;