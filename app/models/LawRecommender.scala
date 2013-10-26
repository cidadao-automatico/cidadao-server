package models

import securesocial.core.{Identity, Authorization}

class LawRecommender
{
	def recommendLawsForUser(user: Identity, page: Int) : Seq[LawProposal] = {
		LawProposal.all(Option.apply(page))		
	}
}