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

case class UserLawRegion(userId: Long, lawRegionId: Long)

object UserLawRegion {

  val simple = {
    ( get[Long]("user_id") ~
      get[Long]("law_region_id")) map {
        case user_id ~ law_region_id =>
          UserLawRegion(user_id, law_region_id)
      }
  }

  def all(): Seq[UserLawRegion] = {
    DB.withConnection { implicit connection =>
      SQL("select * from user_law_regions").as(UserLawRegion.simple *)
    }
  }

  def save(user: User, lawRegion: LawRegion) {
    DB.withConnection { implicit connection =>
      SQL("""
          INSERT INTO user_law_regions(user_id, law_region_id)
          VALUES({user_id}, {law_region_id}) ON DUPLICATE KEY UPDATE user_id=user_id, law_region_id=law_region_id
          """)
        .on(
          'user_id -> user.id,
          'law_region_id -> lawRegion.id).executeInsert()
    }
  }

  def deleteByUserAndRegion(user: User, lawRegion: LawRegion){
    DB.withConnection { implicit connection =>
      SQL("""
        DELETE IGNORE FROM user_law_regions
        WHERE user_id={user_id} AND law_region_id={law_region_id}
        """)
        .on(
          'user_id -> user.id,
          'law_region_id -> lawRegion.id).executeInsert()
    }
  }

  def deleteByUser(user: User) {
    DB.withConnection { implicit connection =>
      SQL("""
        DELETE FROM user_law_regions
        WHERE user_id={user_id}
        """)
        .on(
          'user_id -> user.id).executeInsert()
    }
  }

  def deleteByLawRegion(lawRegion: LawRegion) {
    DB.withConnection { implicit connection =>
      SQL("""
        DELETE FROM user_law_regions
        WHERE law_region_id={law_region_id}
        """)
        .on(
          'law_region_id -> lawRegion.id).executeInsert()
    }
  }
}