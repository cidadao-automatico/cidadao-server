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

case class Vote(userId: Long, lawProposalId: Long, rate: Int, predictedRate: Option[Int])

object Vote {

  implicit object PkFormat extends Format[Pk[Long]] {
    def reads(json: JsValue): JsResult[Pk[Long]] = JsSuccess(
      json.asOpt[Long].map(id => Id(id)).getOrElse(NotAssigned))
    def writes(id: Pk[Long]): JsValue = id.map(JsNumber(_)).getOrElse(JsNull)
  }

  implicit val voteWrites = Json.writes[Vote]
  implicit val voteReads = Json.reads[Vote]

  val simple = {
    (get[Long]("user_id") ~
      get[Long]("law_proposal_id") ~
      get[Int]("rate") ~
      get[Option[Int]]("predicted_rate")) map {
        case user_id ~ law_proposal_id ~ rate ~ predicted_rate =>
          Vote(user_id, law_proposal_id, rate, predicted_rate)
      }
  }

  def all(): Seq[Vote] = {
    DB.withConnection { implicit connection =>
      SQL("select * from votes").as(Vote.simple *)
    }
  }

  def save(user: User, lawProposal: LawProposal, rate: Int, predictedRate: Option[Int]) {
    DB.withConnection { implicit connection =>
      SQL("""
  				INSERT INTO votes(user_id, law_proposal_id, rate, predicted_rate)
  				VALUES({user_id}, {law_proposal_id}, {rate}, {predicted_rate}) ON DUPLICATE KEY UPDATE rate=VALUES(rate), predicted_rate=VALUES(predicted_rate)
  				""")
        .on(
          'user_id -> user.id,
          'law_proposal_id -> lawProposal.id,
          'rate -> rate,
          'predicted_rate -> predictedRate.get).executeInsert()
    }
  }

  def save(userId: Int, lawProposalId: Int, rate: Int, predictedRate: Option[Int]) {
    println("salva voto userid:"+userId+" lawProposalId:"+lawProposalId+" rate:"+rate)
    DB.withConnection { implicit connection =>
      SQL("""
          INSERT INTO votes(user_id, law_proposal_id, rate, predicted_rate)
          VALUES({user_id}, {law_proposal_id}, {rate}, {predicted_rate}) ON DUPLICATE KEY UPDATE rate=VALUES(rate), predicted_rate=VALUES(predicted_rate)
          """)
        .on(
          'user_id -> userId,
          'law_proposal_id -> lawProposalId,
          'rate -> rate,
          'predicted_rate -> predictedRate.get).executeInsert()
    }
  }

  def save(userId: Int, lawProposalId: Int, rate: Int) {
    // println("salva voto userid:"+userId+" lawProposalId:"+lawProposalId+" rate:"+rate)
    DB.withConnection { implicit connection =>
      SQL("""
          INSERT INTO votes(user_id, law_proposal_id, rate)
          VALUES({user_id}, {law_proposal_id}, {rate}) ON DUPLICATE KEY UPDATE rate=VALUES(rate)
          """)
        .on(
          'user_id -> userId,
          'law_proposal_id -> lawProposalId,
          'rate -> rate).executeInsert()
    }
  }


  def findByUser(user: User): Seq[Vote] = {
   DB.withConnection { implicit connection =>
      SQL("select * from votes where user_id={user_id}").on('user_id -> user.id).as(Vote.simple *)
    } 
  }

  def findByLawId(law_id: Long): Seq[Vote] = {
   DB.withConnection { implicit connection =>
      SQL("select * from votes where law_proposal_id={law_proposal_id}").on('law_proposal_id -> law_id).as(Vote.simple *)
    } 
  }

  def findManualByUser(user: User): Seq[Vote] = {
    DB.withConnection { implicit connection =>
      SQL("select * from votes where rate IS NOT NULL AND user_id={user_id}").on('user_id -> user.id).as(Vote.simple *)
    }  
  }

  
}