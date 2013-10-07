package models

import play.api.db._
import play.api.Play.current

import java.util.Date

import anorm._
import anorm.SqlParser._

case class LawStatus (id: Pk[Long], description: String)

object LawStatus {  

  val simple = {
    (get[Pk[Long]]("id") ~      
      get[String]("description")
      ) map {
      case id ~ description =>
      LawStatus(id,description)
    }
  }

  def all(): Seq[LawStatus] = {
    DB.withConnection { implicit connection =>
        SQL("select * from law_status").as(LawStatus.simple *)
    }
    }

    def save(description: String){
      DB.withConnection{ implicit connection => 
        SQL("""
          INSERT INTO law_status(description)
          VALUES({description})
          """)
        .on(
          'description -> description
          ).executeInsert()
      }
    }

}