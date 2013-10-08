package models

class LawRecommender
{
	def recommendLawsForUser(user: User) : Seq[LawProposal] = {
		LawProposal.all()		
	}
}