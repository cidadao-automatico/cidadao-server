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

case class LawTag(tag_id: Long, law_proposal_id: Long)

object LawTag {

  implicit object PkFormat extends Format[Pk[Long]] {
    def reads(json: JsValue): JsResult[Pk[Long]] = JsSuccess(
      json.asOpt[Long].map(id => Id(id)).getOrElse(NotAssigned))
    def writes(id: Pk[Long]): JsValue = id.map(JsNumber(_)).getOrElse(JsNull)
  }

  implicit val lawTagWrites = Json.writes[LawTag]

  val simple = {
    (get[Long]("tag_id") ~
      get[Long]("law_proposal_id")) map {
        case tag_id ~ law_proposal_id =>
          LawTag(tag_id, law_proposal_id)
      }
  }

  def findByLawProposal(lawProposal: LawProposal): Option[LawTag] = {
    DB.withConnection { implicit connection =>
      SQL("select * from law_tags where law_proposal_id={law_proposal_id}").on(
        'law_proposal_id -> lawProposal.id).as(LawTag.simple singleOpt)
    }
  }

  def findByTag(tag: Tag): Option[LawTag] = {
    DB.withConnection { implicit connection =>
      SQL("select * from law_tags where tag_id={tag_id}").on(
        'tag_id -> tag.id).as(LawTag.simple singleOpt)
    }
  }  

  def save(tag: Tag, lawProposal: LawProposal) {
    DB.withConnection { implicit connection =>
      SQL("""
			INSERT INTO law_tags(tag_id, law_proposal_id)
			VALUES({tag_id},{law_proposal_id})
			""")
        .on(
          'tag_id -> tag.id,
          'law_proposal_id -> lawProposal.id).executeInsert()
    }
  }

  def getCountPairs():List[(Int,Long)] = {
   DB.withConnection { implicit connection =>
      SQL("select t.id as tid, count(lt.law_proposal_id) as conta from tags t left join law_tags lt on t.id=lt.tag_id group by t.id order by t.id asc").as(int("tags.id") ~ long("conta") map(flatten) *)
    } 
  }

  def deleteById(id: Long) {
    DB.withConnection { implicit connection =>
      SQL("""
        DELETE FROM law_tags
        WHERE id={id}
        """)
        .on(
          'id -> id).executeInsert()
    }
  }

  def deleteByLaw(lawId: Long) {
    DB.withConnection { implicit connection =>
      SQL("""
        DELETE FROM law_tags
        WHERE law_proposal_id={law_proposal_id}
        """)
        .on(
          'law_proposal_id -> lawId).executeInsert()
    }
  }

}
