package models

import play.api.db._
import play.api.Play.current

import java.util.Date

import anorm._
import anorm.SqlParser._

case class LawType (id: Pk[Long], description: String)

object LawType {	

	val simple = {
		(get[Pk[Long]]("id") ~			
      get[String]("description")
			) map {
			case id ~ description =>
			LawType(id,description)
		}
	}

	def all(): Seq[LawType] = {
		DB.withConnection { implicit connection =>
	  		SQL("select * from law_types").as(LawType.simple *)
		}
  	}

  	def save(description: String){
  		DB.withConnection{ implicit connection => 
  			SQL("""
  				INSERT INTO law_types(description)
  				VALUES({description})
  				""")
  			.on(
          'description -> description
  				).executeInsert()
  		}
  	}

}