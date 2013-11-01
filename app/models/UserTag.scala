/*
// Copyright 2012/2013 de Gustavo Steinberg, Flavio Soares, Pierre Andrews, Gustavo Salazar Torres, Thomaz Abramo
//
// Este arquivo é parte do programa Vigia Político. O projeto Vigia
// Político é um software livre; você pode redistribuí-lo e/ou
// modificá-lo dentro dos termos da GNU Affero General Public License
// como publicada pela Fundação do Software Livre (FSF); na versão 3 da
// Licença. Este programa é distribuído na esperança que possa ser útil,
// mas SEM NENHUMA GARANTIA; sem uma garantia implícita de ADEQUAÇÃO a
// qualquer MERCADO ou APLICAÇÃO EM PARTICULAR. Veja a licença para
// maiores detalhes. Você deve ter recebido uma cópia da GNU Affero
// General Public License, sob o título "LICENCA.txt", junto com este
// programa, se não, acesse http://www.gnu.org/licenses/
*/


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
      get[Long]("user_id")) map {
        case tag_id ~ user_id =>
          UserTag(tag_id, user_id)
      }
  }

  def findByUser(user: User): Option[UserTag] = {
    DB.withConnection { implicit connection =>
      SQL("select * from user_tags where user_id={user_id}").on(
        'user_id -> user.id).as(UserTag.simple singleOpt)
    }
  }

  def findByTag(tag: Tag): Option[UserTag] = {
    DB.withConnection { implicit connection =>
      SQL("select * from law_tags where tag_id={tag_id}").on(
        'tag_id -> tag.id).as(UserTag.simple singleOpt)
    }
  }

  def save(user: User, tag: Tag) {
    DB.withConnection { implicit connection =>
      SQL("""
          INSERT INTO user_tags(user_id, tag_id)
          VALUES({user_id}, {tag_id}) ON DUPLICATE KEY UPDATE user_id=user_id, tag_id=tag_id
          """)
        .on(
          'user_id -> user.id,
          'tag_id -> tag.id).executeInsert()
    }
  }

  def deleteByUserAndTag(user: User, tag: Tag){
    DB.withConnection { implicit connection =>
      SQL("""
        DELETE IGNORE FROM user_tags
        WHERE user_id={user_id} AND tag_id={tag_id}
        """)
        .on(
          'user_id -> user.id,
          'tag_id -> tag.id).executeInsert()
    }
  }

  def deleteByUser(user: User) {
    DB.withConnection { implicit connection =>
      SQL("""
        DELETE FROM user_tags
        WHERE user_id={user_id}
        """)
        .on(
          'user_id -> user.id).executeInsert()
    }
  }

  def deleteByTag(tag: Tag) {
    DB.withConnection { implicit connection =>
      SQL("""
        DELETE FROM user_tags
        WHERE tag_id={tag_id}
        """)
        .on(
          'tag_id -> tag.id).executeInsert()
    }
  }

}
