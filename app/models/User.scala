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

import securesocial.core._

case class User (id: Pk[Long], firstName: String, lastName: String, email: Option[String], oauthId: String, oauthProvider: String, 
			countryName:String, stateName:String, cityName:String, docId: String, typeCode: Int)

object User {
	
	val NORMAL_TYPE:Int=1
	
	val CONGRESS_TYPE:Int=2

	implicit object PkFormat extends Format[Pk[Long]] {
        def reads(json: JsValue): JsResult[Pk[Long]] = JsSuccess (
            json.asOpt[Long].map(id => Id(id)).getOrElse(NotAssigned)
        )
        def writes(id: Pk[Long]): JsValue = id.map(JsNumber(_)).getOrElse(JsNull)
  }


	implicit val userWrites = Json.writes[User]

	val simple = {
		(get[Pk[Long]]("id") ~
			get[String]("first_name") ~
			get[String]("last_name") ~
			get[Option[String]]("email") ~ 
			get[String]("oauth_id") ~ 
			get[String]("oauth_provider") ~ 
			get[String]("country_name") ~ 
			get[String]("state_name") ~ 
			get[String]("city_name") ~ 
			get[String]("doc_id") ~ 
			get[Int]("type_code")
			) map {
			case id ~ first_name ~ last_name ~ email ~ oauth_id ~ oauth_provider ~ country_name ~ state_name ~ city_name ~ doc_id ~ type_code =>
			User(id,first_name,last_name,email,oauth_id,oauth_provider,country_name,state_name,city_name, doc_id,type_code)
		}
	}

	def all(): Seq[User] = {
		DB.withConnection { implicit connection =>
	  		SQL("select * from users").as(User.simple *)
		}
  	}

  	def findById(id: Long): Option[User] = {
		DB.withConnection { implicit connection =>
			SQL("select * from users where id={id}").on(
				'id -> id
				).as(User.simple singleOpt)
		}
	}	

	def findByEmail(email: String): Option[User] = {
		DB.withConnection { implicit connection =>
			SQL("select * from users where email={email}").on(
				'email -> email
				).as(User.simple singleOpt)
		}
	}

	def findByEmailAndOAuthId(email: String, oauth_id: String): Option[User] =	{
		DB.withConnection { implicit connection =>
			SQL("select * from users where oauth_id={oauth_id} and email={email}").on(
				'email -> email,
				'oauth_id -> oauth_id
				).as(User.simple singleOpt)
		}	
	}

	def findByOAuthId(oauth_id: String): Option[User] =	{
		DB.withConnection { implicit connection =>
			SQL("select * from users where oauth_id={oauth_id}").on(
				'oauth_id -> oauth_id
				).as(User.simple singleOpt)
		}	
	}

  	def save(firstName: String, lastName: String, email: Option[String], oauthId: String, oauthProvider: String, 
			countryName:String, stateName:String, cityName: String, docId: String, typeCode: Int){
  		DB.withConnection{ implicit connection => 
  			SQL("""
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
  				'state_name-> stateName,
  				'city_name-> cityName,
  				'doc_id-> docId,
  				'type_code -> typeCode
  				).executeInsert()
  		}
  	}

}