package models

case class LawPriority (id: Pk[Long], description: String)

object LawPriority {	

	val simple = {
		(get[Pk[Long]]("id") ~			
      get[String]("description")
			) map {
			case id ~ description =>
			LawPriority(id,description)
		}
	}

	def all(): Seq[LawPriority] = {
		DB.withConnection { implicit connection =>
	  		SQL("select * from law_priorities").as(LawPriority.simple *)
		}
  	}

  	def save(){
  		DB.withConnection{ implicit connection => 
  			SQL("""
  				INSERT INTO law_priorities(description)
  				VALUES({description})
  				""")
  			.on(
          'description -> description
  				).executeInsert()
  		}
  	}

}