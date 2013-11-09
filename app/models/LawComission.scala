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

case class LawComission(law_proposal_id: Long, comission_id: Long)

object LawComission {

  implicit object PkFormat extends Format[Pk[Long]] {
    def reads(json: JsValue): JsResult[Pk[Long]] = JsSuccess(
      json.asOpt[Long].map(id => Id(id)).getOrElse(NotAssigned))
    def writes(id: Pk[Long]): JsValue = id.map(JsNumber(_)).getOrElse(JsNull)
  }

  implicit val lawComissionWrites = Json.writes[LawComission]

  val simple = {
    (get[Long]("law_proposal_id") ~
      get[Long]("comission_id")) map {
        case law_proposal_id ~ comission_id =>
          LawComission(law_proposal_id, comission_id)
      }
  }

  def findByLawProposal(lawProposal: LawProposal): Option[LawComission] = {
    DB.withConnection { implicit connection =>
      SQL("select * from law_proposal_comissions where law_proposal_id={law_proposal_id}").on(
        'law_proposal_id -> lawProposal.id).as(LawComission.simple singleOpt)
    }
  }

  def findByComission(comission: Comission): Option[LawComission] = {
    DB.withConnection { implicit connection =>
      SQL("select * from law_proposal_comissions where comission_id={comission_id}").on(
        'comission_id -> comission.id).as(LawComission.simple singleOpt)
    }
  }  

  def save(comission: Comission, lawProposal: LawProposal) {
    DB.withConnection { implicit connection =>
      SQL("""
			INSERT INTO law_proposal_comissions(law_proposal_id, comission_id)
			VALUES({law_proposal_id},{comission_id})
			""")
        .on(
          'comission_id -> comission.id,
          'law_proposal_id -> lawProposal.id).executeInsert()
    }
  }  
}
