package models

case class CongressmanInfo (userId: Long, partyId: Long, stateName: String, homePageUrl: String)

object CongressmanInfo {	

	val simple = {
		(get[Long]("user_id") ~
			get[Long]("party_id") ~
			get[String]("state_name") ~
			get[String]("home_page_url")
			) map {
			case user_id ~ party_id ~ state_name ~ home_page_url =>
			CongressmanInfo(user_id, party_id, state_name, home_page_url)
		}
	}

	def all(): Seq[CongressmanInfo] = {
		DB.withConnection { implicit connection =>
	  		SQL("select * from congressman_infos").as(CongressmanInfo.simple *)
		}
  	}

  	def save(user: User, party: Party, stateName: String, homePageUrl: String){
  		DB.withConnection{ implicit connection => 
  			SQL("""
  				INSERT INTO congressman_infos(user_id, party_id, state_name, home_page_url)
  				VALUES({user_id}, {party_id}, {state_name}, {home_page_url})
  				""")
  			.on(
  				'user_id -> user.id,
  				'party_id -> party.id,
  				'state_name-> stateName,
  				'home_page_url-> homePageUrl
  				).executeInsert()
  		}
  	}

}