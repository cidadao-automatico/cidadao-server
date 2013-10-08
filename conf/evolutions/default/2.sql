# --- !Ups

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
  type_code INT NOT NULL ,
  PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS parties (
  id INT NOT NULL AUTO_INCREMENT,
  name VARCHAR(200) NOT NULL,
  home_page_url VARCHAR(512) NOT NULL ,
  PRIMARY KEY (id)
);

CREATE  TABLE IF NOT EXISTS congressman_infos (
  user_id INT NOT NULL,  
  state_name VARCHAR(30) NOT NULL ,
  party_id INT NOT NULL ,
  home_page_url VARCHAR(512) NOT NULL,
  PRIMARY KEY (user_id)
);

CREATE  TABLE IF NOT EXISTS votes (
  user_id INT NOT NULL,  
  law_proposal_id INT NOT NULL ,
  rate INT NOT NULL,
  PRIMARY KEY (user_id, law_proposal_id)
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
  std_code VARCHAR(256) NULL,
  PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS law_priorities (
	id INT NOT NULL AUTO_INCREMENT,
	description VARCHAR(256) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS law_authors (
	id INT NOT NULL AUTO_INCREMENT,
	name VARCHAR(256) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS law_regions (
	id INT NOT NULL AUTO_INCREMENT,
	description VARCHAR(256) NOT NULL,
	region_parent_id INT NULL,
	type_name VARCHAR(90) NULL,
  PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS law_types (
	id INT NOT NULL AUTO_INCREMENT,
	description VARCHAR(256) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS law_status (
	id INT NOT NULL AUTO_INCREMENT,
	description VARCHAR(256) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS user_law_regions (
	id INT NOT NULL AUTO_INCREMENT,
	user_id INT NOT NULL,
	law_region_id INT NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS law_tags (
  tag_id INT NOT NULL,
  law_proposal_id INT NOT NULL
);

CREATE TABLE IF NOT EXISTS tags (
  id INT NOT NULL AUTO_INCREMENT,
  name VARCHAR(40) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS user_tags (
  user_id INT NOT NULL,
  tag_id INT NOT NULL,
  PRIMARY KEY (user_id, tag_id)
);

CREATE TABLE IF NOT EXISTS user_representatives (
  user_id INT NOT NULL,
  congressman_id INT NOT NULL,
  PRIMARY KEY (user_id, congressman_id)
);


# --- !Downs

 
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS parties;
DROP TABLE IF EXISTS congressman_infos;
DROP TABLE IF EXISTS votes;
DROP TABLE IF EXISTS law_proposals;
DROP TABLE IF EXISTS law_priorities;
DROP TABLE IF EXISTS law_authors;
DROP TABLE IF EXISTS law_regions;
DROP TABLE IF EXISTS law_types;
DROP TABLE IF EXISTS law_status;
DROP TABLE IF EXISTS user_law_regions;
DROP TABLE IF EXISTS law_tags;
DROP TABLE IF EXISTS tags;
DROP TABLE IF EXISTS user_tags;
DROP TABLE IF EXISTS user_representatives;