# --- !Ups

ALTER TABLE users ADD COLUMN oauth_provider VARCHAR(60) NOT NULL;

# --- !Downs

ALTER TABLE users DROP COLUMN oauth_provider;