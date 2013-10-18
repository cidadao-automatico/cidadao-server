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

case class LawProposal(id: Pk[Long], authorId: Long, typeId: Long, statusId: Option[Long], priorityId: Option[Long], regionId: Long,
  url: String, year: Int, stdCode: String, description: String)

object LawProposal {

  implicit object PkFormat extends Format[Pk[Long]] {
    def reads(json: JsValue): JsResult[Pk[Long]] = JsSuccess(
      json.asOpt[Long].map(id => Id(id)).getOrElse(NotAssigned))
    def writes(id: Pk[Long]): JsValue = id.map(JsNumber(_)).getOrElse(JsNull)
  }

  implicit val lawProposalWrites = Json.writes[LawProposal]

  val simple = {
    (get[Pk[Long]]("id") ~
      get[Long]("law_author_id") ~
      get[Long]("law_type_id") ~
      get[Option[Long]]("law_status_id") ~
      get[Option[Long]]("law_priority_id") ~
      get[Long]("law_region_id") ~
      get[String]("law_url") ~
      get[Int]("year") ~
      get[String]("std_code")) ~
      get[String]("description") map {
        case id ~ law_author_id ~ law_type_id ~
          law_status_id ~ law_priority_id ~ law_region_id ~ law_url ~ year ~ std_code ~ description =>
          LawProposal(id, law_author_id, law_type_id, law_status_id, law_priority_id, law_region_id, law_url, year, std_code, description)
      }
  }

  def findById(id: Long): Option[LawProposal] =
    {
      DB.withConnection { implicit conn =>
        SQL("select * from law_proposals where id={id}").
          on(
            'id -> id).as(LawProposal.simple singleOpt)
      }
    }

  def all(): Seq[LawProposal] = {
    DB.withConnection { implicit connection =>
      SQL("select * from law_proposals").as(LawProposal.simple *)
    }
  }

  def findRandom(): Option[LawProposal] = {
    DB.withConnection { implicit connection =>
      SQL("select * from law_proposals order by rand() limit 1").as(LawProposal.simple singleOpt)
    }
  }

  def save(lawAuthor: LawAuthor, lawRegion: LawRegion,
    lawType: LawType, url: String, year: Int, stdCode: String, description: String) : LawProposal = {
    
    DB.withConnection { implicit connection =>
        val idOpt: Option[Long] = SQL("""
          INSERT INTO law_proposals(law_author_id, law_type_id, law_region_id, law_url, year, std_code, description)
          VALUES({law_author_id}, {law_type_id}, {law_region_id}, {law_url}, {year}, {std_code}, {description})
          """)
            .on(
              'law_author_id -> lawAuthor.id,
              'law_type_id -> lawType.id,
              'law_region_id -> lawRegion.id,
              'law_url -> url,
              'year -> year,
              'std_code -> stdCode,
              'description -> description).executeInsert()

        idOpt.map { id => LawProposal(Id(id), lawAuthor.id.get, lawType.id.get, None, None, lawRegion.id.get, url, year, stdCode, description) }.get

        }
  }

  def deleteById(id: Long) {
    DB.withConnection { implicit connection =>
      SQL("""
        DELETE FROM law_proposals
        WHERE id={id}
        """)
        .on(
          'id -> id).executeInsert()
    }
  }
}