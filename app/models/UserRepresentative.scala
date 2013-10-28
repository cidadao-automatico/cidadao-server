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
      get[Long]("congressman_id")) map {
        case user_id ~ congressman_id =>
          UserRepresentative(user_id, congressman_id)
      }
  }

  def findByCongressman(congressman: User): Option[UserRepresentative] = {
    DB.withConnection { implicit connection =>
      SQL("select * from user_representatives where congressman_id={congressman_id}").on(
        'congressman_id -> congressman.id).as(UserRepresentative.simple singleOpt)
    }
  }

  def findByUser(user: User): Seq[UserRepresentative] = {
    DB.withConnection { implicit connection =>
      SQL("select * from user_representatives where user_id={user_id}").on(
        'user_id -> user.id).as(UserRepresentative.simple *)
    }
  }

  def save(user: User, congressman: User) {
    DB.withConnection { implicit connection =>
      SQL("""
			INSERT INTO user_representatives(user_id, congressman_id)
			VALUES ({user_id},{congressman_id}) ON DUPLICATE KEY UPDATE user_id=user_id, congressman_id=congressman_id
			""")
        .on(
          'user_id -> user.id,
          'congressman_id -> congressman.id).executeInsert()
    }
  }

  def deleteByUser(user: User) {
    DB.withConnection { implicit connection =>
      SQL("""
        DELETE FROM user_representatives
        WHERE user_id={user_id}
        """)
        .on(
          'user_id -> user.id).executeInsert()
    }
  }

  def deleteByCongressman(congressman: User) {
    DB.withConnection { implicit connection =>
      SQL("""
        DELETE FROM user_representatives
        WHERE congressman_id={congressman_id}
        """)
        .on(
          'congressman_id -> congressman.id).executeInsert()
    }
  }

  def deleteByUserAndCongressman(user: User, congressman: User) {
    DB.withConnection { implicit connection =>
      SQL("""
        DELETE IGNORE FROM user_representatives
        WHERE user_id={user_id} AND congressman_id={congressman_id}
        """)
        .on(
          'user_id -> user.id,
          'congressman_id -> congressman.id).executeInsert()
    }
  }

}
