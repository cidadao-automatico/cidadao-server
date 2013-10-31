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
import play.api.libs.json._
import play.api.libs.json.util._
import play.api.libs.json.Writes._
import play.api.libs.functional.syntax._
import java.util.Date

import anorm._
import anorm.SqlParser._

case class CongressmanInfo(userId: Long, partyId: Long, stateName: Option[String], homePageUrl: Option[String], congressId: Option[Long], sourceId: Option[Long], shortName: Option[String], photoUrl: Option[String], phoneNumber: Option[String], email: Option[String])

object CongressmanInfo {


  implicit val congressmanWrites = Json.writes[CongressmanInfo]
  implicit val congressmanReads = Json.reads[CongressmanInfo]

  val simple = {
    (get[Long]("user_id") ~
      get[Long]("party_id") ~
      get[Option[String]]("state_name") ~
      get[Option[String]]("home_page_url") ~
      get[Option[Long]]("congress_id") ~
      get[Option[Long]]("source_id") ~
      get[Option[String]]("short_name") ~
      get[Option[String]]("photo_url") ~
      get[Option[String]]("phone_number") ~
      get[Option[String]]("email")) map {
        case user_id ~ party_id ~ state_name ~ home_page_url ~ congress_id ~ source_id ~ short_name ~ photo_url ~ phone_number ~ email  =>
          CongressmanInfo(user_id, party_id, state_name, home_page_url, congress_id, source_id, short_name, photo_url, phone_number, email)
      }
  }

  def all(): Seq[CongressmanInfo] = {
    DB.withConnection { implicit connection =>
      SQL("select * from congressman_infos").as(CongressmanInfo.simple *)
    }
  }

  def save(user: User, party: Party, stateName: Option[String], homePageUrl: Option[String], congressId: Option[Long], sourceId: Option[Long], shortName: Option[String], photoUrl: Option[String], phoneNumber: Option[String], email: Option[String]) {
    DB.withConnection { implicit connection =>
      SQL("""
  				INSERT INTO congressman_infos(user_id, party_id, state_name, home_page_url, congress_id, source_id, short_name, photo_url, phone_number, email)
  				VALUES({user_id}, {party_id}, {state_name}, {home_page_url}, {congress_id}, {source_id}, {short_name}, {photo_url}, {phone_number}, {email})
  				""")
        .on(
          'user_id -> user.id,
          'party_id -> party.id,
          'state_name -> stateName.get,
          'home_page_url -> homePageUrl.get,
          'congress_id -> congressId.get,
          'source_id -> sourceId.get,
          'short_name -> shortName.get,
          'photo_url -> photoUrl.get,
          'phone_number -> phoneNumber.get,
          'email -> email.get
          ).executeInsert()
    }
  }

  def findByUser(user: User) : Option[CongressmanInfo]  = {
    DB.withConnection { implicit connection =>
      SQL("select * from congressman_infos inner join users on congressman_infos.user_id=users.id where users.id={user_id}").
        on('user_id -> user.id).
        as(CongressmanInfo.simple singleOpt)
    } 
  }

}