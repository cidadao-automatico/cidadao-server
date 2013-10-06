package models

case class Party (id: Pk[Long], name: String, homePageUrl: String)

object Party {	

	val simple = {
		(get[Pk[Long]]("id") ~
			get[Long]("name") ~
			get[String]("home_page_url")
			) map {
			case id ~ name ~ home_page_url =>
			Party(id, name, home_page_url)
		}
	}

	def all(): Seq[Party] = {
		DB.withConnection { implicit connection =>
	  		SQL("select * from parties").as(Party.simple *)
		}
  	}

  	def save(name: String, homePageUrl: String){
  		DB.withConnection{ implicit connection => 
  			SQL("""
  				INSERT INTO parties(name, home_page_url)
  				VALUES({name}, {home_page_url})
  				""")
  			.on(
  				'name-> name,
  				'home_page_url-> homePageUrl
  				).executeInsert()
  		}
  	}

}