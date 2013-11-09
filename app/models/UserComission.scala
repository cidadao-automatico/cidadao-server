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

case class UserComission(comission_id: Long, user_id: Long)

object UserComission {

  val simple = {
    (get[Long]("comission_id") ~
      get[Long]("user_id")) map {
        case comission_id ~ user_id =>
          UserComission(comission_id, user_id)
      }
  }

  def findByUser(user: User): Seq[UserComission] = {
    DB.withConnection { implicit connection =>
      SQL("select * from user_comissions where user_id={user_id}").on(
        'user_id -> user.id).as(UserComission.simple *)
    }
  }

  def findByComission(comission: Comission): Seq[UserComission] = {
    DB.withConnection { implicit connection =>
      SQL("select * from law_tags where comission_id={comission_id}").on(
        'comission_id -> comission.id).as(UserComission.simple *)
    }
  }

  def save(user: User, comission: Comission) {
    DB.withConnection { implicit connection =>
      SQL("""
          INSERT INTO user_comissions(user_id, comission_id)
          VALUES({user_id}, {comission_id}) ON DUPLICATE KEY UPDATE user_id=user_id, comission_id=comission_id
          """)
        .on(
          'user_id -> user.id,
          'comission_id -> comission.id).executeInsert()
    }
  }

  def deleteByUserAndComission(user: User, comission: Comission){
    DB.withConnection { implicit connection =>
      SQL("""
        DELETE IGNORE FROM user_comissions
        WHERE user_id={user_id} AND comission_id={comission_id}
        """)
        .on(
          'user_id -> user.id,
          'comission_id -> comission.id).executeInsert()
    }
  }

  def deleteByUser(user: User) {
    DB.withConnection { implicit connection =>
      SQL("""
        DELETE FROM user_comissions
        WHERE user_id={user_id}
        """)
        .on(
          'user_id -> user.id).executeInsert()
    }
  }

  def deleteByComission(comission: Comission) {
    DB.withConnection { implicit connection =>
      SQL("""
        DELETE FROM user_comissions
        WHERE comission_id={comission_id}
        """)
        .on(
          'comission_id -> comission.id).executeInsert()
    }
  }

}
