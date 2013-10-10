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


case class Vote (userId: Long, lawProposalId: Long, rate: Int)

object Vote {	


  implicit object PkFormat extends Format[Pk[Long]] {
        def reads(json: JsValue): JsResult[Pk[Long]] = JsSuccess (
            json.asOpt[Long].map(id => Id(id)).getOrElse(NotAssigned)
        )
        def writes(id: Pk[Long]): JsValue = id.map(JsNumber(_)).getOrElse(JsNull)
  }

  implicit val voteWrites = Json.writes[Vote]  

	val simple = {
		(get[Long]("user_id") ~
			get[Long]("law_proposal_id") ~
			get[Int]("rate")
			) map {
			case user_id ~ law_proposal_id ~ rate =>
			 Vote(user_id, law_proposal_id, rate)
		}
	}

	def all(): Seq[Vote] = {
		DB.withConnection { implicit connection =>
	  		SQL("select * from votes").as(Vote.simple *)
		}
  	}

  	def save(user: User, lawProposal: LawProposal, rate: Int){
  		DB.withConnection{ implicit connection => 
  			SQL("""
  				INSERT INTO parties(user_id, law_proposal_id, rate)
  				VALUES({user_id}, {law_proposal_id}, {rate})
  				""")
  			.on(
  				'user_id-> user.id,
  				'law_proposal_id -> lawProposal.id,
  				'rate -> rate
  				).executeInsert()
  		}
  	}

    
}