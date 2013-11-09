# --- !Ups

RENAME TABLE comissoes TO comissions;

ALTER TABLE comissions CHANGE COLUMN nome name VARCHAR(255) NOT NULL;
ALTER TABLE comissions CHANGE COLUMN short prefix VARCHAR(10) NULL;

CREATE TABLE IF NOT EXISTS law_proposal_comissions(
	law_proposal_id INT NOT NULL,
	comission_id INT NOT NULL,
	PRIMARY KEY(law_proposal_id, comission_id)
);

CREATE TABLE IF NOT EXISTS user_comissions(
	user_id INT NOT NULL,
	comission_id INT NOT NULL,
	PRIMARY KEY(user_id, comission_id)
);

DROP TABLE user_tags;

# --- !Downs

RENAME TABLE comissions TO comissoes;

ALTER TABLE comissions CHANGE COLUMN name nome VARCHAR(255) NOT NULL;
ALTER TABLE comissions CHANGE COLUMN prefix short VARCHAR(10) NULL;

DROP TABLE law_proposal_comissions;

DROP TABLE user_comissions;

CREATE TABLE IF NOT EXISTS user_tags (
  user_id INT NOT NULL,
  tag_id INT NOT NULL,
  PRIMARY KEY (user_id, tag_id)
);