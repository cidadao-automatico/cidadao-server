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

case class LawAuthor(id: Pk[Long], name: String)

object LawAuthor {

  implicit object PkFormat extends Format[Pk[Long]] {
    def reads(json: JsValue): JsResult[Pk[Long]] = JsSuccess(
      json.asOpt[Long].map(id => Id(id)).getOrElse(NotAssigned))
    def writes(id: Pk[Long]): JsValue = id.map(JsNumber(_)).getOrElse(JsNull)
  }

  implicit val authorWrites = Json.writes[LawAuthor]

  val simple = {
    (get[Pk[Long]]("id") ~
      get[String]("name")) map {
        case id ~ name =>
          LawAuthor(id, name)
      }
  }

  def all(): Seq[LawAuthor] = {
    DB.withConnection { implicit connection =>
      SQL("select * from law_authors").as(LawAuthor.simple *)
    }
  }

  def findById(id: Long): Option[LawAuthor] = {
    DB.withConnection { implicit connection =>
      SQL("select * from law_authors where id={id}").on(
        'id -> id).as(LawAuthor.simple singleOpt)
    }
  }

  def findByName(name: Long): Option[LawAuthor] = {
    DB.withConnection { implicit connection =>
      SQL("select * from law_authors where name={name}").on(
        'name -> name).as(LawAuthor.simple singleOpt)
    }
  }

  def save(name: String) : LawAuthor = {
    DB.withConnection { implicit connection =>
      val idOpt: Option[Long] = SQL("""
				INSERT INTO law_authors(name)
				VALUES({name})
				""")
        .on(
          'name -> name).executeInsert()

      idOpt.map { id => LawAuthor(Id(id), name) }.get
    }
  }

  def deleteById(id: Long) {
    DB.withConnection { implicit connection =>
      SQL("""
        DELETE FROM law_authors
        WHERE id={id}
        """)
        .on(
          'id -> id).executeInsert()
    }
  }
}