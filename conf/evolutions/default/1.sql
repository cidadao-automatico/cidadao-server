# --- !Ups


CREATE  TABLE IF NOT EXISTS vereador (
  id INT NOT NULL AUTO_INCREMENT,
  name VARCHAR(45) NOT NULL ,
  party VARCHAR(45) NOT NULL ,
  nome_parlamentar VARCHAR(45) NULL ,
  PRIMARY KEY (id),
  CONSTRAINT v_u_name UNIQUE (name) );







CREATE  TABLE IF NOT EXISTS projeto_lei (
  id INT NOT NULL AUTO_INCREMENT,
  numero INT NOT NULL ,
  tipo VARCHAR(10) NOT NULL ,
  data TIMESTAMP NOT NULL ,
  ementa TEXT NULL ,
  tipo_norma VARCHAR(45) NULL ,
  numero_norma INT NULL ,
  data_norma VARCHAR(45) NULL ,
  PRIMARY KEY (id),
  CONSTRAINT pl_u_pl UNIQUE (numero, tipo, data) );







CREATE  TABLE IF NOT EXISTS promovente (
  tipo INT NOT NULL ,
  promovante_id INT NOT NULL ,
  projeto_lei_id INT NOT NULL ,
  PRIMARY KEY (tipo, promovante_id, projeto_lei_id) ,
  CONSTRAINT fk_promovente_projeto_lei
    FOREIGN KEY (projeto_lei_id )
    REFERENCES projeto_lei (id )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);







CREATE  TABLE IF NOT EXISTS comissoes (
  id INT NOT NULL AUTO_INCREMENT,
  nome VARCHAR(255) NOT NULL ,
  short VARCHAR(10) NULL,
  PRIMARY KEY (id),
  UNIQUE (nome) );







CREATE  TABLE IF NOT EXISTS projeto_lei_has_comissoes (
  projeto_lei_id INT NOT NULL ,
  comissoes_id INT NOT NULL ,
  PRIMARY KEY (projeto_lei_id, comissoes_id) ,
  CONSTRAINT fk_projeto_lei_has_comissoes_projeto_lei1
    FOREIGN KEY (projeto_lei_id )
    REFERENCES projeto_lei (id )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT fk_projeto_lei_has_comissoes_comissoes1
    FOREIGN KEY (comissoes_id )
    REFERENCES comissoes (id )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);







CREATE  TABLE IF NOT EXISTS keyword (
  id INT NOT NULL AUTO_INCREMENT,
  tag VARCHAR(255) NOT NULL ,
  PRIMARY KEY (id),
  UNIQUE  (tag) );







CREATE  TABLE IF NOT EXISTS projeto_lei_has_keyword (
  projeto_lei_id INT NOT NULL ,
  keyword_id INT NOT NULL ,
  PRIMARY KEY (projeto_lei_id, keyword_id) ,
  CONSTRAINT fk_projeto_lei_has_keyword_projeto_lei1
    FOREIGN KEY (projeto_lei_id )
    REFERENCES projeto_lei (id )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT fk_projeto_lei_has_keyword_keyword1
    FOREIGN KEY (keyword_id )
    REFERENCES keyword (id )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);







CREATE  TABLE IF NOT EXISTS vote_session (
  id INT NOT NULL AUTO_INCREMENT,
  data TIMESTAMP NOT NULL ,
  numero INT NOT NULL ,
  tipo VARCHAR(45) NOT NULL ,
  PRIMARY KEY (id),
  CONSTRAINT s_u UNIQUE (data, numero, tipo)  );







CREATE  TABLE IF NOT EXISTS vote (
  vereador_id INT NOT NULL ,
  projeto_lei_id INT NOT NULL ,
  vote_session_id INT NOT NULL ,
  vote INT NOT NULL ,
  PRIMARY KEY (vereador_id, projeto_lei_id, vote_session_id, vote) ,
  CONSTRAINT fk_vote_vereador1
    FOREIGN KEY (vereador_id )
    REFERENCES vereador (id )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT fk_vote_projeto_lei1
    FOREIGN KEY (projeto_lei_id )
    REFERENCES projeto_lei (id )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT fk_vote_vote_session1
    FOREIGN KEY (vote_session_id )
    REFERENCES vote_session (id )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);


# --- !Downs

DROP TABLE IF EXISTS comissoes ;
DROP TABLE IF EXISTS projeto_lei_has_comissoes ;
DROP TABLE IF EXISTS keyword ;
DROP TABLE IF EXISTS projeto_lei_has_keyword ;
DROP TABLE IF EXISTS vote_session ;
DROP TABLE IF EXISTS vote ;
DROP TABLE IF EXISTS vereador ;
DROP TABLE IF EXISTS projeto_lei ;
DROP TABLE IF EXISTS promovente ;
