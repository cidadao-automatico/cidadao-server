package models

case class Vote (userId: Long, lawProposalId: Long, rate: Int)

object Vote {	

	val simple = {
		(get[Long]("user_id") ~
			get[Long]("law_proposal_id") ~
			get[String]("home_page_url")
			) map {
			case user_id ~ law_proposal_id ~ home_page_url =>
			Vote(user_id, law_proposal_id, home_page_url)
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