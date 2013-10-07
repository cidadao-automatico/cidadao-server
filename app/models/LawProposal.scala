package models

import play.api.db._
import play.api.Play.current

import java.util.Date

import anorm._
import anorm.SqlParser._

case class LawProposal (id: Pk[Long], authorId: Long, typeId: Long, statusId: Long, priorityId: Long, regionId: Long,
  url: String, createdAt: Date, stdCode: String)

object LawProposal {	

	val simple = {
		(get[Pk[Long]]("id") ~
			get[Long]("law_author_id") ~
      get[Long]("law_type_id") ~
      get[Long]("law_status_id") ~
      get[Long]("law_priority_id") ~
      get[Long]("law_region_id") ~
      get[String]("law_url") ~
      get[Date]("created_at") ~
      get[String]("std_code")
      ) map {
			case id ~ law_author_id ~ law_type_id ~ 
           law_status_id ~ law_priority_id ~ law_region_id ~ law_url ~ created_at ~ std_code =>
			LawProposal(id,law_author_id,law_type_id,law_status_id,law_priority_id,law_region_id,law_url,created_at,std_code)
		}
	}

  def findById(id: Long): Option[LawProposal] =
  {
    DB.withConnection { implicit conn => 
      SQL("select * from law_proposals where id={id}").
        on(
          'id -> id
        ).as(LawProposal.simple singleOpt) 
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

  def save(user: User, lawPriority: LawPriority, lawAuthor: LawAuthor, lawRegion: LawRegion, lawType: LawType, lawStatus: LawStatus, url: String, createdAt: Date, stdCode: String){
    DB.withConnection{ implicit connection => 
     SQL("""
      INSERT INTO law_proposals(id, law_author_id, law_type_id, law_status_id, law_priority_id, law_region_id, law_url, created_at, std_code)
      VALUES({id}, {law_author_id}, {law_type_id}, {law_status_id}, {law_priority_id}, {law_region_id}, {law_url}, {created_at}, {std_code})
      """)
     .on(
      'id -> user.id,
      'law_author_id -> lawAuthor.id,
      'law_type_id -> lawType.id,
      'law_status_id -> lawStatus.id,
      'law_priority_id -> lawPriority.id,
      'law_region_id -> lawRegion.id,
      'law_url -> url,
      'created_at -> createdAt,
      'std_code -> stdCode
      ).executeInsert()
    }
  }

}