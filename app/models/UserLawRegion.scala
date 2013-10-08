package models

import play.api.db._
import play.api.Play.current

import java.util.Date

import anorm._
import anorm.SqlParser._

case class UserLawRegion (id: Pk[Long], userId: Long, lawRegionId: Long)

object UserLawRegion {  

  val simple = {
    (get[Pk[Long]]("id") ~      
      get[Long]("user_id") ~
      get[Long]("law_region_id")
      ) map {
      case id ~ user_id ~ law_region_id =>
      UserLawRegion(id, user_id, law_region_id)
    }
  }

  def all(): Seq[UserLawRegion] = {
    DB.withConnection { implicit connection =>
        SQL("select * from user_law_regions").as(UserLawRegion.simple *)
    }
    }

    def save(user: User, lawRegion: LawRegion){
      DB.withConnection{ implicit connection => 
        SQL("""
          INSERT INTO user_law_regions(user_id, law_region_id)
          VALUES({user_id}, {law_region_id})
          """)
        .on(
          'user_id -> user.id,
          'law_region_id -> lawRegion.id
          ).executeInsert()
      }
    }

}