package models

case class LawRegion (id: Pk[Long], description: String, typeName: String, parentRegionId: Long)

object LawRegion {	

	val simple = {
		(get[Pk[Long]]("id") ~			
      get[String]("description") ~
      get[String]("type_name") ~
      get[String]("region_parent_id") ~
			) map {
			case id ~ description ~ type_name ~ region_parent_id =>
			LawRegion(id, description, type_name, region_parent_id)
		}
	}

	def all(): Seq[LawRegion] = {
		DB.withConnection { implicit connection =>
	  		SQL("select * from law_regions").as(LawRegion.simple *)
		}
  	}

  	def save(){
  		DB.withConnection{ implicit connection => 
  			SQL("""
  				INSERT INTO law_regions(description)
  				VALUES({description})
  				""")
  			.on(
          'description -> description
  				).executeInsert()
  		}
  	}

}