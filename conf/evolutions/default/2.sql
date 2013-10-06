CREATE  TABLE IF NOT EXISTS users (
  id INT NOT NULL AUTO_INCREMENT,
  first_name VARCHAR(45) NOT NULL ,
  last_name VARCHAR(45) NOT NULL ,
  email VARCHAR(200) NOT NULL ,
  oauth_id VARCHAR(300) NULL ,
  country_name VARCHAR(30) NOT NULL ,
  state_name VARCHAR(30) NOT NULL ,
  city_name VARCHAR(80) NOT NULL,
  doc_id VARCHAR(60) NULL,
  type_code INT NOT NULL  
);

-- These are not needed right now
-- CREATE TABLE IF NOT EXISTS state (
--   id INT NOT NULL AUTO_INCREMENT,
--   name VARCHAR(200) NOT NULL
-- );

-- CREATE TABLE IF NOT EXISTS country (
--   id INT NOT NULL AUTO_INCREMENT,
--   name VARCHAR(200) NOT NULL
-- );

CREATE TABLE IF NOT EXISTS parties (
  id INT NOT NULL AUTO_INCREMENT,
  name VARCHAR(200) NOT NULL
  home_page_url VARCHAR(512) NOT NULL ,
);

CREATE  TABLE IF NOT EXISTS congressman_infos (
  user_id INT NOT NULL,  
  state_name VARCHAR(30) NOT NULL ,
  party_id INT NOT NULL ,
  home_page_url VARCHAR(512) NOT NULL ,
);

CREATE  TABLE IF NOT EXISTS votes (
  user_id INT NOT NULL,  
  law_proposal_id INT NOT NULL ,
  rate INT NOT NULL,
);

CREATE  TABLE IF NOT EXISTS law_proposals (
  id INT NOT NULL AUTO_INCREMENT,  
  law_author_id INT NOT NULL,
  law_type_id INT NOT NULL,
  law_status_id INT NOT NULL,
  law_priority_id INT NOT NULL,
  law_region_id INT NOT NULL,  
  law_url VARCHAR(512) NULL,
  created_at DATE NOT NULL,
  std_code VARCHAR(256) NULL
);

CREATE TABLE IF NOT EXISTS law_priorities{
	id INT NOT NULL AUTO_INCREMENT,
	description VARCHAR(256) NOT NULL
};

CREATE TABLE IF NOT EXISTS law_authors{
	id INT NOT NULL AUTO_INCREMENT,
	name VARCHAR(256) NOT NULL
};

CREATE TABLE IF NOT EXISTS law_regions{
	id INT NOT NULL AUTO_INCREMENT,
	description VARCHAR(256) NOT NULL,
	region_parent_id INT NULL,
	type_name VARCHAR(90) NULL
};

CREATE TABLE IF NOT EXISTS law_types{
	id INT NOT NULL AUTO_INCREMENT,
	description VARCHAR(256) NOT NULL
};

CREATE TABLE IF NOT EXISTS law_status{
	id INT NOT NULL AUTO_INCREMENT,
	description VARCHAR(256) NOT NULL
};

CREATE TABLE IF NOT EXISTS user_law_regions{
	id INT NOT NULL AUTO_INCREMENT,
	user_id INT NOT NULL,
	law_region_id INT NOT NULL
};