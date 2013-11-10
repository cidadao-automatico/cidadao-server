/*
// Copyright 2012/2013 de Gustavo Steinberg, Flavio Soares, Pierre Andrews, Gustavo Salazar Torres, Thomaz Abramo
//
// Este arquivo é parte do programa Vigia Político. O projeto Vigia
// Político é um software livre; você pode redistribuí-lo e/ou
// modificá-lo dentro dos termos da GNU Affero General Public License
// como publicada pela Fundação do Software Livre (FSF); na versão 3 da
// Licença. Este programa é distribuído na esperança que possa ser útil,
// mas SEM NENHUMA GARANTIA; sem uma garantia implícita de ADEQUAÇÃO a
// qualquer MERCADO ou APLICAÇÃO EM PARTICULAR. Veja a licença para
// maiores detalhes. Você deve ter recebido uma cópia da GNU Affero
// General Public License, sob o título "LICENCA.txt", junto com este
// programa, se não, acesse http://www.gnu.org/licenses/
*/


package models

import play.api.db._
import play.api.Play.current
import play.api.libs.json._
import play.api.libs.json.util._
import play.api.libs.json.Writes._
import play.api.libs.functional.syntax._

import java.util.Date

import anorm._
import anorm.SqlParser._

// import securesocial.core._

case class User(id: Pk[Long], firstName: String, lastName: String, email: Option[String], oauthId: String, oauthProvider: String,
  countryName: Option[String], stateName: Option[String], cityName: Option[String], docId: Option[String], typeCode: Int, var configured: Boolean)

object User {

  val NORMAL_TYPE: Int = 1

  val CONGRESS_TYPE: Int = 2

  implicit object PkFormat extends Format[Pk[Long]] {
    def reads(json: JsValue): JsResult[Pk[Long]] = JsSuccess(
      json.asOpt[Long].map(id => Id(id)).getOrElse(NotAssigned))
    def writes(id: Pk[Long]): JsValue = id.map(JsNumber(_)).getOrElse(JsNull)
  }

  implicit val userWrites = Json.writes[User]
  implicit val userReads = Json.reads[User]

  val simple = {
    (get[Pk[Long]]("id") ~
      get[String]("first_name") ~
      get[String]("last_name") ~
      get[Option[String]]("email") ~
      get[String]("oauth_id") ~
      get[String]("oauth_provider") ~
      get[Option[String]]("country_name") ~
      get[Option[String]]("state_name") ~
      get[Option[String]]("city_name") ~
      get[Option[String]]("doc_id") ~
      get[Int]("type_code") ~
      get[Boolean]("configured")) map {
        case id ~ first_name ~ last_name ~ email ~ oauth_id ~ oauth_provider ~ country_name ~ state_name ~ city_name ~ doc_id ~ type_code ~ configured =>
          User(id, first_name, last_name, email, oauth_id, oauth_provider, country_name, state_name, city_name, doc_id, type_code, configured)
      }
  }

  def all(): Seq[User] = {
    DB.withConnection { implicit connection =>
      SQL("select * from users").as(User.simple *)
    }
  }

  def findAllCongressman(): Seq[User] = {
    DB.withConnection { implicit connection =>
      SQL("select * from users where type_code={type_code}").on('type_code -> CONGRESS_TYPE).as(User.simple *)
    }
  }

  def findFirst100Congressman(): Seq[User] = {
    DB.withConnection { implicit connection =>
      SQL("select * from users where type_code={type_code}").on('type_code -> CONGRESS_TYPE).as(User.simple *)
    }
  }

  def findById(id: Long): Option[User] = {
    DB.withConnection { implicit connection =>
      SQL("select * from users where id={id}").on(
        'id -> id).as(User.simple singleOpt)
    }
  }

  def findByEmail(email: String): Option[User] = {
    DB.withConnection { implicit connection =>
      SQL("select * from users where email={email} LIMIT 1").on(
        'email -> email).as(User.simple singleOpt)
    }
  }

  def findByEmailAndOAuthId(email: String, oauth_id: String): Option[User] = {
    DB.withConnection { implicit connection =>
      SQL("select * from users where oauth_id={oauth_id} and email={email}").on(
        'email -> email,
        'oauth_id -> oauth_id).as(User.simple singleOpt)
    }
  }

  def findByOAuthId(oauth_id: String): Option[User] = {
    DB.withConnection { implicit connection =>
      SQL("select * from users where oauth_id={oauth_id}").on(
        'oauth_id -> oauth_id).as(User.simple singleOpt)
    }
  }

  def save(firstName: String, lastName: String, email: Option[String], oauthId: String, oauthProvider: String,
    countryName: Option[String], stateName: Option[String], cityName: Option[String], docId: Option[String], typeCode: Int): User = {
    println("entrou no metodo")

    DB.withConnection { implicit connection =>
      val idOpt: Option[Long] = SQL("""
  				INSERT INTO users(first_name, last_name, email, oauth_id, oauth_provider, country_name,
  					state_name, city_name, doc_id, type_code)
  				VALUES({first_name}, {last_name}, {email}, {oauth_id}, {oauth_provider}, {country_name}, {state_name},
  					{city_name},{doc_id}, {type_code})
  				""")
        .on(
          'first_name -> firstName,
          'last_name -> lastName,
          'email -> email,
          'oauth_id -> oauthId,
          'oauth_provider -> oauthProvider,
          'country_name -> countryName,
          'state_name -> stateName,
          'city_name -> cityName,
          'doc_id -> docId,
          'type_code -> typeCode).executeInsert()
        println("saiu do metodo")
      idOpt.map { id => User(Id(id), firstName, lastName, email, oauthId, oauthProvider, countryName, stateName, cityName, docId, typeCode, false) }.get
    }
    
  }

  def updateConfigured(user: User): Option[User] ={
    DB.withConnection { implicit connection =>
      SQL("""
          UPDATE users SET configured={configured} WHERE id={id}
          """)
        .on(
          'id -> user.id,
          'configured -> user.configured).executeUpdate()

      Option(user)
    }
  }

  def updateById(id: Long, firstName: String, lastName: String, email: Option[String], oauthId: String, oauthProvider: String,
    countryName: Option[String], stateName: Option[String], cityName: Option[String], docId: Option[String], typeCode: Int): Option[User] = {
    DB.withConnection { implicit connection =>
      SQL("""
  				UPDATE users SET first_name={first_name}, last_name={last_name}, email={email}, 
  					oauth_id={oauth_id}, oauth_provider={oauth_provider}, country_name={country_name},
  					state_name={state_name}, city_name={city_name}, doc_id={doc_id}, type_code={type_code}
  				WHERE id={id}
  				""")
        .on(
          'id -> id,
          'first_name -> firstName,
          'last_name -> lastName,
          'email -> email,
          'oauth_id -> oauthId,
          'oauth_provider -> oauthProvider,
          'country_name -> countryName,
          'state_name -> stateName,
          'city_name -> cityName,
          'doc_id -> docId,
          'type_code -> typeCode).executeUpdate()

      Option(User(Id(id), firstName, lastName, email, oauthId, oauthProvider, countryName, stateName, cityName, docId, typeCode, false))
    }
  }

  def deleteById(id: Long) {
    DB.withConnection { implicit connection =>
      SQL("""
        DELETE FROM users
        WHERE id={id}
        """)
        .on(
          'id -> id).executeInsert()
    }
  }

  def findByEmail(email: Option[String]): Option[User] = {
    DB.withConnection { implicit connection =>
      SQL("select * from users where email={email} LIMIT 1").on(
        'email -> email.get).as(User.simple singleOpt)
    }

  }

  def updateModelPath(id: Long, modelPath: Option[String])
  {
    DB.withConnection { implicit connection =>
      SQL("""
          UPDATE users SET model_path={model_path} WHERE id={id}
          """)
        .on(
          'id -> id,
          'model_path -> modelPath).executeUpdate()      
    } 
  }

  def findModelPath(id: Long): Option[String] ={
    DB.withConnection { implicit connection =>
      var result=SQL("select model_path from users where id={id}").on(
        'id -> id).apply().head

      try{
        return Option(result[String]("model_path"))
      } catch{
        case e: Exception =>  return None
      }

    }
  }
  

}