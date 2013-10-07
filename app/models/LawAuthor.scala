package models

import play.api.db._
import play.api.Play.current

import java.util.Date

import anorm._
import anorm.SqlParser._

case class LawAuthor (id: Pk[Long], name: String)

object LawAuthor {	

	val simple = {
		(get[Pk[Long]]("id") ~			
      get[String]("name")
			) map {
			case id ~ name =>
			LawAuthor(id,name)
		}
	}

	def all(): Seq[LawAuthor] = {
		DB.withConnection { implicit connection =>
	  		SQL("select * from law_authors").as(LawAuthor.simple *)
		}
  	}

  	def save(name: String){
  		DB.withConnection{ implicit connection => 
  			SQL("""
  				INSERT INTO law_authors(name)
  				VALUES({name})
  				""")
  			.on(
          'name -> name
  				).executeInsert()
  		}
  	}

}