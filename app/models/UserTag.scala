package models

import play.api.db._
import play.api.Play.current

import java.util.Date

import anorm._
import anorm.SqlParser._

case class UserTag(tag_id: Long, user_id: Long)

object UserTag {

 val simple = {
 	(get[Long]("tag_id") ~
 		get[Long]("user_id")
 		) map {
 		case tag_id ~ user_id =>
			UserTag(tag_id, user_id)
	}
}

def findByUser(user: User): Option[UserTag] = {
	DB.withConnection { implicit connection =>
		SQL("select * from user_tags where user_id={user_id}").on(
			'user_id -> user.id
			).as(UserTag.simple singleOpt)
	}
}

def findByTag(tag: Tag): Option[UserTag] = {
	DB.withConnection { implicit connection =>
		SQL("select * from law_tags where tag_id={tag_id}").on(
			'tag_id -> tag.id
			).as(UserTag.simple singleOpt)
	}
}

def save(tag: Tag, user: User) {
	DB.withConnection{ implicit connection => 
		SQL("""
			INSERT INTO user_tags(user_id, tag_id)
			VALUES({user_id},{tag_id})
			""")
		.on(
			'user_id -> user.id,
			'tag_id -> tag.id
			).executeInsert()
	}
}


}
