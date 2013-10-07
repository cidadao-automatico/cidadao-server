package models

import play.api.db._
import play.api.Play.current

import java.util.Date

import anorm._
import anorm.SqlParser._

case class User (id: Pk[Long], firstName: String, lastName: String, email: String, oauthId: String, 
			countryName:String, stateName:String, docId: String, typeCode: Int)

object User {
	
	val NORMAL_TYPE=1
	
	val CONGRESS_TYPE=2

	val simple = {
		(get[Pk[Long]]("id") ~
			get[String]("first_name") ~
			get[String]("last_name") ~
			get[String]("email") ~ 
			get[String]("oauth_id") ~ 
			get[String]("country_name") ~ 
			get[String]("state_name") ~ 
			get[String]("doc_id") ~ 
			get[Int]("type_code")
			) map {
			case id ~ first_name ~ last_name ~ email ~ oauth_id ~ country_name ~ state_name ~ doc_id ~ type_code =>
			User(id,first_name,last_name,email,oauth_id,country_name,state_name,doc_id,type_code)
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

  	def save(firstName: String, lastName: String, email: String, oauthId: String, 
			countryName:String, stateName:String, docId: String, typeCode: Int){
  		DB.withConnection{ implicit connection => 
  			SQL("""
  				INSERT INTO Users(first_name, last_name, email, oauth_id, country_name,
  					state_name, doc_id, type_code)
  				VALUES({first_name}, {last_name}, {email}, {oauth_id}, {country_name}, {state_name},
  					{doc_id}, {type_code})
  				""")
  			.on(
  				'first_name -> firstName,
  				'last_name -> lastName,
  				'email -> email,
  				'oauth_id -> oauthId,
  				'country_name -> countryName,
  				'state_name-> stateName,
  				'doc_id-> docId,
  				'type_code -> typeCode
  				).executeInsert()
  		}
  	}

  	def recommendLaws(): Seq[LawProposal] = {
  		LawProposal.all()
  	}

}