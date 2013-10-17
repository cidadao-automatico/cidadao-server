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

import utils._

case class LawRegion(id: Pk[Long] = NotAssigned, description: String, typeName: String, parentRegionId: Option[Long])

object LawRegion {

  implicit object PkFormat extends Format[Pk[Long]] {
    def reads(json: JsValue): JsResult[Pk[Long]] = JsSuccess(
      json.asOpt[Long].map(id => Id(id)).getOrElse(NotAssigned))
    def writes(id: Pk[Long]): JsValue = id.map(JsNumber(_)).getOrElse(JsNull)
  }

  implicit val regionWrites = Json.writes[LawRegion]

  val simple = {
    (get[Pk[Long]]("id") ~
      get[String]("description") ~
      get[String]("type_name") ~
      get[Option[Long]]("region_parent_id")) map {
        case id ~ description ~ type_name ~ region_parent_id =>
          LawRegion(id, description, type_name, region_parent_id)
      }
  }

  def findAll(): Seq[LawRegion] = {
    DB.withConnection { implicit connection =>
      SQL("select * from law_regions").as(LawRegion.simple *)
    }
  }

  def findById(id: Long): Option[LawRegion] = {
    DB.withConnection { implicit connection =>
      SQL("select * from law_regions where id={id}").on(
        'id -> id).as(LawRegion.simple singleOpt)
    }
  }

  def save(description: String, typeName: String, parent: Option[LawRegion]) {
    parent match {
      case Some(parentRegion) =>
        DB.withConnection { implicit connection =>
          SQL("""
              INSERT INTO law_regions(description, type_name, region_parent_id)
              VALUES({description},{type_name},{region_parent_id})
              """)
            .on(
              'description -> description,
              'type_name -> typeName,
              'region_parent_id -> parentRegion.id).executeInsert()
        }
      case _ =>
        DB.withConnection { implicit connection =>
          SQL("""
              INSERT INTO law_regions(description, type_name)
              VALUES({description}, {type_name})
              """)
            .on(
              'description -> description,
              'type_name -> typeName).executeInsert()
        }
    }

  }

  def deleteById(id: Long) {
    DB.withConnection { implicit connection =>
      SQL("""
        DELETE FROM law_regions
        WHERE id={id}
        """)
        .on(
          'id -> id).executeInsert()
    }
  }
}