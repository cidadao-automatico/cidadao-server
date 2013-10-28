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

case class CongressmanInfo(userId: Long, partyId: Long, stateName: Option[String], homePageUrl: Option[String])

object CongressmanInfo {


  implicit val congressmanWrites = Json.writes[CongressmanInfo]
  implicit val congressmanReads = Json.reads[CongressmanInfo]

  val simple = {
    (get[Long]("user_id") ~
      get[Long]("party_id") ~
      get[Option[String]]("state_name") ~
      get[Option[String]]("home_page_url")) map {
        case user_id ~ party_id ~ state_name ~ home_page_url =>
          CongressmanInfo(user_id, party_id, state_name, home_page_url)
      }
  }

  def all(): Seq[CongressmanInfo] = {
    DB.withConnection { implicit connection =>
      SQL("select * from congressman_infos").as(CongressmanInfo.simple *)
    }
  }

  def save(user: User, party: Party, stateName: String, homePageUrl: String) {
    DB.withConnection { implicit connection =>
      SQL("""
  				INSERT INTO congressman_infos(user_id, party_id, state_name, home_page_url)
  				VALUES({user_id}, {party_id}, {state_name}, {home_page_url})
  				""")
        .on(
          'user_id -> user.id,
          'party_id -> party.id,
          'state_name -> stateName,
          'home_page_url -> homePageUrl).executeInsert()
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