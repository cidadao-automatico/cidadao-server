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

case class Party(id: Pk[Long], name: String, homePageUrl: String)

object Party {

  implicit object PkFormat extends Format[Pk[Long]] {
    def reads(json: JsValue): JsResult[Pk[Long]] = JsSuccess(
      json.asOpt[Long].map(id => Id(id)).getOrElse(NotAssigned))
    def writes(id: Pk[Long]): JsValue = id.map(JsNumber(_)).getOrElse(JsNull)
  }

  implicit val partyWrites = Json.writes[Party]

  val simple = {
    (get[Pk[Long]]("id") ~
      get[String]("name") ~
      get[String]("home_page_url")) map {
        case id ~ name ~ home_page_url =>
          Party(id, name, home_page_url)
      }
  }

  def all(): Seq[Party] = {
    DB.withConnection { implicit connection =>
      SQL("select * from parties").as(Party.simple *)
    }
  }

  def save(name: String, homePageUrl: String) {
    DB.withConnection { implicit connection =>
      SQL("""
  				INSERT INTO parties(name, home_page_url)
  				VALUES({name}, {home_page_url})
  				""")
        .on(
          'name -> name,
          'home_page_url -> homePageUrl).executeInsert()
    }
  }

  def deleteById(id: Long) {
    DB.withConnection { implicit connection =>
      SQL("""
        DELETE FROM parties
        WHERE id={id}
        """)
        .on(
          'id -> id).executeInsert()
    }
  }
}