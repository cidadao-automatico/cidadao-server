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

case class LawType(id: Pk[Long], description: String)

object LawType {

  implicit object PkFormat extends Format[Pk[Long]] {
    def reads(json: JsValue): JsResult[Pk[Long]] = JsSuccess(
      json.asOpt[Long].map(id => Id(id)).getOrElse(NotAssigned))
    def writes(id: Pk[Long]): JsValue = id.map(JsNumber(_)).getOrElse(JsNull)
  }

  implicit val lawTypeWrites = Json.writes[LawType]

  val simple = {
    (get[Pk[Long]]("id") ~
      get[String]("description")) map {
        case id ~ description =>
          LawType(id, description)
      }
  }

  def all(): Seq[LawType] = {
    DB.withConnection { implicit connection =>
      SQL("select * from law_types").as(LawType.simple *)
    }
  }

  def findById(id: Long): Option[LawType] = {
    DB.withConnection { implicit connection =>
      SQL("select * from law_types where id={id}").on(
        'id -> id).as(LawType.simple singleOpt)
    }
  }

  def save(description: String) {
    DB.withConnection { implicit connection =>
      SQL("""
  				INSERT INTO law_types(description)
  				VALUES({description})
  				""")
        .on(
          'description -> description).executeInsert()
    }
  }

  def deleteById(id: Long) {
    DB.withConnection { implicit connection =>
      SQL("""
        DELETE FROM law_types
        WHERE id={id}
        """)
        .on(
          'id -> id).executeInsert()
    }
  }
}