package models

import play.api.db._
import play.api.Play.current

import java.util.Date

import anorm._
import anorm.SqlParser._

case class Party (id: Pk[Long], name: String, homePageUrl: String)

object Party {	

	val simple = {
		(get[Pk[Long]]("id") ~
			get[String]("name") ~
			get[String]("home_page_url")
			) map {
			case id ~ name ~ home_page_url =>
			Party(id, name, home_page_url)
		}
	}

	def all(): Seq[Party] = {
		DB.withConnection { implicit connection =>
	  		SQL("select * from parties").as(Party.simple *)
		}
  	}

  	def save(name: String, homePageUrl: String){
  		DB.withConnection{ implicit connection => 
  			SQL("""
  				INSERT INTO parties(name, home_page_url)
  				VALUES({name}, {home_page_url})
  				""")
  			.on(
  				'name-> name,
  				'home_page_url-> homePageUrl
  				).executeInsert()
  		}
  	}

}