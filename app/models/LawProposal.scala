/*
// Copyright 2012/2013 de Gustavo Steinberg, Flavio Soares, Pierre Andrews, Gustavo Salazar Torres, Thomaz Abramo
//
// Este arquivo é parte do programa Vigia Político. O projeto Vigia
// Político é um software livre; você pode redistribuí-lo e/ou
// modificá-lo dentro dos termos da GNU Affero General Public License
// como publicada pela Fundação do Software Livre (FSF); na versão 3 da
// Licença. Este programa é distribuído na esperança que possa ser útil,
// mas SEM NENHUMA GARANTIA; sem uma garantia implícita de ADEQUAÇÃO a
// qualquer MERCADO ou APLICAÇÃO EM PARTICULAR. Veja a licença para
// maiores detalhes. Você deve ter recebido uma cópia da GNU Affero
// General Public License, sob o título "LICENCA.txt", junto com este
// programa, se não, acesse http://www.gnu.org/licenses/
*/


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

case class LawProposal(id: Pk[Long], regionId: Long,
  url: String, year: Int, stdCode: String, description: String, prefix: String, priorityStatus: String)

object LawProposal {

  implicit object PkFormat extends Format[Pk[Long]] {
    def reads(json: JsValue): JsResult[Pk[Long]] = JsSuccess(
      json.asOpt[Long].map(id => Id(id)).getOrElse(NotAssigned))
    def writes(id: Pk[Long]): JsValue = id.map(JsNumber(_)).getOrElse(JsNull)
  }

  implicit val lawProposalWrites = Json.writes[LawProposal]
  implicit val lawProposalReads = Json.reads[LawProposal]

  val simple = {
    (get[Pk[Long]]("id") ~      
      get[Long]("law_region_id") ~
      get[String]("law_url") ~
      get[Int]("year") ~
      get[String]("std_code") ~
      get[String]("description") ~
      get[String]("prefix") ~
      get[String]("priority_status") ) map {
        case id ~ law_region_id ~ law_url ~ year ~ std_code ~ description ~ prefix ~ priority_status=>
          LawProposal(id, law_region_id, law_url, year, std_code, description, prefix, priority_status)
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

  def all(page: Option[Int]): Seq[LawProposal] = {
    page match {
      case Some(pageVal) => 
        DB.withConnection { implicit connection =>
          SQL("select * from law_proposals where vote_status=0 limit 4 offset {offset}").
          on('offset -> (pageVal-1)*4).
          as(LawProposal.simple *)
        }
      case _ => 
        DB.withConnection { implicit connection =>
          SQL("select * from law_proposals where vote_status=0").as(LawProposal.simple *)
        }
      }    
  }

  def countAllVoted(): Long = {
    DB.withConnection { implicit connection =>
          SQL("select count(*) as conta from law_proposals where vote_status=1").as(long("conta") single)
        }
  }

  def allVoted(page: Option[Int]): Seq[LawProposal] = {
    page match {
      case Some(pageVal) => 
        DB.withConnection { implicit connection =>
          SQL("select * from law_proposals where vote_status=1 limit 4 offset {offset}").
          on('offset -> pageVal*4).
          as(LawProposal.simple *)
        }
      case _ => 
        DB.withConnection { implicit connection =>
          SQL("select * from law_proposals where vote_status=1").as(LawProposal.simple *)
        }
      }    
  }

  def allVotedNoPage(): Seq[LawProposal] = {    
      DB.withConnection { implicit connection =>
        SQL("select * from law_proposals where vote_status=1").as(LawProposal.simple *)
      }    
  }

  def findByUser(user: User): Seq[LawProposal] = {
    DB.withConnection { implicit connection =>
          SQL("""
            select law_proposals.* from law_proposals inner join votes on law_proposals.id=votes.law_proposal_id where votes.user_id={user_id}
            """).
          on('user_id -> user.id).
          as(LawProposal.simple *)
        }
  }

  def lawsNotVotedForUser(user: User): Seq[LawProposal] = {
    DB.withConnection { implicit connection =>
          SQL("""
            select * from law_proposals where vote_status=1 AND id not in (select law_proposal_id from votes where user_id={user_id}) order by rand() limit 4
            """).
          on('user_id -> user.id).
          as(LawProposal.simple *)
        }
  }

  def lawsNotVotedForCongressman(congressman: User): Seq[LawProposal] = {
    DB.withConnection { implicit connection =>
          SQL("""
            select * from law_proposals where vote_status=1 AND id not in (select law_proposal_id from votes where user_id={user_id})
            """).
          on('user_id -> congressman.id).
          as(LawProposal.simple *)
        }
  }

  def findRandom(): Option[LawProposal] = {
    DB.withConnection { implicit connection =>
      SQL("select * from law_proposals order by rand() limit 1").as(LawProposal.simple singleOpt)
    }
  }

  def findByUserConfiguration(user: User, page: Int): Seq[LawProposal] ={
    DB.withConnection { implicit connection =>
        SQL("""
      select distinct lp.* from law_proposals lp inner join law_proposal_comissions lpc on lp.id=lpc.law_proposal_id 
      inner join law_regions lr on lr.id=lp.law_region_id where 
      lpc.comission_id in (select uc.comission_id from user_comissions uc where uc.user_id={user_id}) and 
      lr.id in (select ulr.law_region_id from user_law_regions ulr where ulr.user_id={user_id})
      order by lp.id limit 4 offset {offset}
    """).on('user_id -> user.id,
            'offset -> (page-1)*4).as(LawProposal.simple *)
      }
  }

  /*select lp.* from law_proposals lp inner join law_proposal_comissions lpc on lp.id=lpc.law_proposal_id 
      inner join law_regions lr on lr.id=lp.law_region_id where lpc.comission_id in 
      (select uc.comission_id from user_comissions uc where uc.user_id={user_id}) and 
      lr.id in (select ulr.law_region_id from user_law_regions ulr where ulr.user_id={user_id})
      and lp.id not in (select bl.law_proposal_id from user_law_blacklist ulb where ulb.user_id={user_id});*/

  def save(lawRegion: LawRegion, prefix: String, url: String, year: Int, stdCode: String, description: String, priorityStatus:String) : LawProposal = {
    
    DB.withConnection { implicit connection =>
        val idOpt: Option[Long] = SQL("""
          INSERT INTO law_proposals(prefix, law_region_id, law_url, year, std_code, description, priority_status)
          VALUES({prefix}, {law_region_id}, {law_url}, {year}, {std_code}, {description}, {priority_status})
          """)
            .on(
              'prefix -> prefix,
              'law_region_id -> lawRegion.id,
              'law_url -> url,
              'year -> year,
              'std_code -> stdCode,
              'description -> description,
              'priority_status -> priorityStatus).executeInsert()

        idOpt.map { id => LawProposal(Id(id), lawRegion.id.get, url, year, stdCode, description, prefix, priorityStatus) }.get

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

  def updateVoteStatus(law_id : Long, status: Boolean)
  {
    DB.withConnection { implicit connection =>
        SQL("""
          UPDATE law_proposals SET vote_status={vote_status} WHERE id={law_id}
          """)
            .on(
              'vote_status -> status,
              'law_id -> law_id).executeUpdate()
        }
    }
}