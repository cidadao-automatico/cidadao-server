package models

import play.api.db._
import play.api.Play.current

import java.util.Date

import anorm._
import anorm.SqlParser._

case class LawTag(tag_id: Long, law_proposal_id: Long)

object LawTag {

 val simple = {
 	(get[Long]("tag_id") ~
 		get[Long]("law_proposal_id")
 		) map {
 		case tag_id ~ law_proposal_id =>
			LawTag(tag_id, law_proposal_id)
	}
}

def findByLawProposal(lawProposal: LawProposal): Option[LawTag] = {
	DB.withConnection { implicit connection =>
		SQL("select * from law_tags where law_proposal_id={law_proposal_id}").on(
			'law_proposal_id -> lawProposal.id
			).as(LawTag.simple singleOpt)
	}
}

def findByTag(tag: Tag): Option[LawTag] = {
	DB.withConnection { implicit connection =>
		SQL("select * from law_tags where tag_id={tag_id}").on(
			'tag_id -> tag.id
			).as(LawTag.simple singleOpt)
	}
}

def save(tag: Tag, lawProposal: LawProposal) {
	DB.withConnection{ implicit connection => 
		SQL("""
			INSERT INTO law_tags(user_id, law_proposal_id)
			VALUES({tag_id},{law_proposal_id})
			""")
		.on(
			'tag_id -> tag.id,
			'law_proposal_id -> lawProposal.id
			).executeInsert()
	}
}


}
