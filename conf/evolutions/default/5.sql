# --- !Ups

ALTER TABLE law_proposals MODIFY COLUMN law_status_id INT NULL;
ALTER TABLE law_proposals MODIFY COLUMN law_priority_id INT NULL;
ALTER TABLE law_proposals CHANGE created_at year INT NULL;
ALTER TABLE law_proposals ADD COLUMN description TEXT NULL;

# --- !Downs

ALTER TABLE law_proposals MODIFY COLUMN law_status_id INT  NOT NULL;
ALTER TABLE law_proposals MODIFY COLUMN law_priority_id INT NOT NULL;
ALTER TABLE law_proposals CHANGE year created_at DATE NOT NULL;
ALTER TABLE law_proposals DROP COLUMN description TEXT NULL;