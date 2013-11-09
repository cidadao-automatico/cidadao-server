# --- !Ups

CREATE TABLE IF NOT EXISTS user_law_blacklist(
	user_id INT NOT NULL,
	law_proposal_id INT NOT NULL,	
	PRIMARY KEY(law_proposal_id, user_id)
);

# --- !Downs

DROP TABLE 	user_law_blacklist;