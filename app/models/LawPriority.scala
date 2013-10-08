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

case class LawPriority (id: Pk[Long], description: String)

object LawPriority {	

  implicit object PkFormat extends Format[Pk[Long]] {
        def reads(json: JsValue): JsResult[Pk[Long]] = JsSuccess (
            json.asOpt[Long].map(id => Id(id)).getOrElse(NotAssigned)
        )
        def writes(id: Pk[Long]): JsValue = id.map(JsNumber(_)).getOrElse(JsNull)
  }

	val simple = {
		(get[Pk[Long]]("id") ~			
      get[String]("description")
			) map {
			case id ~ description =>
			LawPriority(id,description)
		}
	}

	def all(): Seq[LawPriority] = {
		DB.withConnection { implicit connection =>
	  		SQL("select * from law_priorities").as(LawPriority.simple *)
		}
  }

  def findById(id: Long) : Option[LawPriority] ={
   DB.withConnection { implicit connection =>
        SQL("select * from law_priorities where id={id}").on(
          'id -> id
          ).as(LawPriority.simple singleOpt)
    } 
  }

  	def save(description: String){
  		DB.withConnection{ implicit connection => 
  			SQL("""
  				INSERT INTO law_priorities(description)
  				VALUES({description})
  				""")
  			.on(
          'description -> description
  				).executeInsert()
  		}
  	}

}