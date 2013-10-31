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
  url: String, year: Int, stdCode: String, description: String, prefix: String)

object LawProposal {

  implicit object PkFormat extends Format[Pk[Long]] {
    def reads(json: JsValue): JsResult[Pk[Long]] = JsSuccess(
      json.asOpt[Long].map(id => Id(id)).getOrElse(NotAssigned))
    def writes(id: Pk[Long]): JsValue = id.map(JsNumber(_)).getOrElse(JsNull)
  }

  implicit val lawProposalWrites = Json.writes[LawProposal]

  val simple = {
    (get[Pk[Long]]("id") ~      
      get[Long]("law_region_id") ~
      get[String]("law_url") ~
      get[Int]("year") ~
      get[String]("std_code") ~
      get[String]("description") ~
      get[String]("prefix")) map {
        case id ~ law_region_id ~ law_url ~ year ~ std_code ~ description ~ prefix =>
          LawProposal(id, law_region_id, law_url, year, std_code, description, prefix)
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


  def lawsNotVotedForUser(user: User): Seq[LawProposal] = {
    DB.withConnection { implicit connection =>
          SQL("""
            select * from law_proposals where vote_status=1 AND id not in (select law_proposal_id from votes where user_id={user_id}) order by rand() limit 4
            """).
          on('user_id -> user.id).
          as(LawProposal.simple *)
        }
  }

  def findRandom(): Option[LawProposal] = {
    DB.withConnection { implicit connection =>
      SQL("select * from law_proposals order by rand() limit 1").as(LawProposal.simple singleOpt)
    }
  }

  def save(lawRegion: LawRegion, prefix: String, url: String, year: Int, stdCode: String, description: String) : LawProposal = {
    
    DB.withConnection { implicit connection =>
        val idOpt: Option[Long] = SQL("""
          INSERT INTO law_proposals(prefix, law_region_id, law_url, year, std_code, description)
          VALUES({prefix}, {law_region_id}, {law_url}, {year}, {std_code}, {description})
          """)
            .on(
              'prefix -> prefix,
              'law_region_id -> lawRegion.id,
              'law_url -> url,
              'year -> year,
              'std_code -> stdCode,
              'description -> description).executeInsert()

        idOpt.map { id => LawProposal(Id(id), lawRegion.id.get, url, year, stdCode, description, prefix) }.get

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