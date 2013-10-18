package models

import securesocial.core.{Identity, Authorization}

class LawRecommender
{
	def recommendLawsForUser(user: Identity) : Seq[LawProposal] = {
		LawProposal.all()		
	}
}