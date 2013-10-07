package models

import play.api.db._
import play.api.Play.current

import java.util.Date

import anorm._
import anorm.SqlParser._

case class UserRepresentative(user_id: Long, congressman_id: Long)

object UserRepresentative {

 val simple = {
 	(get[Long]("user_id") ~
 		get[Long]("congressman_id")
 		) map {
 		case user_id ~ congressman_id =>
			UserRepresentative(user_id, congressman_id)
	}
}

def findByCongressman(congressman: User): Option[UserRepresentative] = {
	DB.withConnection { implicit connection =>
		SQL("select * from user_representatives where congressman_id={congressman_id}").on(
			'congressman_id -> congressman.id
			).as(UserRepresentative.simple singleOpt)
	}
}

def findByUser(user: User): Option[UserRepresentative] = {
	DB.withConnection { implicit connection =>
		SQL("select * from user_representatives where user_id={user_id}").on(
			'user_id -> user.id
			).as(UserRepresentative.simple singleOpt)
	}
}

def save(user: User, congressman: User) {
	DB.withConnection{ implicit connection => 
		SQL("""
			INSERT INTO user_representatives(user_id, congressman_id)
			VALUES({user_id},{congressman_id})
			""")
		.on(
			'user_id -> user.id,
			'congressman_id -> congressman.id
			).executeInsert()
	}
}


}
