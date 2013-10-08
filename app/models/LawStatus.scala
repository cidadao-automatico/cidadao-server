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

case class LawStatus (id: Pk[Long], description: String)

object LawStatus {

  implicit object PkFormat extends Format[Pk[Long]] {
        def reads(json: JsValue): JsResult[Pk[Long]] = JsSuccess (
            json.asOpt[Long].map(id => Id(id)).getOrElse(NotAssigned)
        )
        def writes(id: Pk[Long]): JsValue = id.map(JsNumber(_)).getOrElse(JsNull)
  }

  implicit val statusWrites = Json.writes[LawStatus]  

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

  def findById(id: Long) : Option[LawStatus] ={
   DB.withConnection { implicit connection =>
        SQL("select * from law_status where id={id}").on(
          'id -> id
          ).as(LawStatus.simple singleOpt)
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