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

case class Party(id: Pk[Long], name: String, homePageUrl: String)

object Party {

  implicit object PkFormat extends Format[Pk[Long]] {
    def reads(json: JsValue): JsResult[Pk[Long]] = JsSuccess(
      json.asOpt[Long].map(id => Id(id)).getOrElse(NotAssigned))
    def writes(id: Pk[Long]): JsValue = id.map(JsNumber(_)).getOrElse(JsNull)
  }

  implicit val partyWrites = Json.writes[Party]

  val simple = {
    (get[Pk[Long]]("id") ~
      get[String]("name") ~
      get[String]("home_page_url")) map {
        case id ~ name ~ home_page_url =>
          Party(id, name, home_page_url)
      }
  }

  def all(): Seq[Party] = {
    DB.withConnection { implicit connection =>
      SQL("select * from parties").as(Party.simple *)
    }
  }

  def save(name: String, homePageUrl: String): Party = {
    DB.withConnection { implicit connection =>
        val idOpt: Option[Long] = SQL("""
  				INSERT INTO parties(name, home_page_url)
  				VALUES({name}, {home_page_url})
  				""")
        .on(
          'name -> name,
          'home_page_url -> homePageUrl).executeInsert()

        idOpt.map{ id => Party(Id(id), name, homePageUrl) }.get
    }
  }

  def deleteById(id: Long) {
    DB.withConnection { implicit connection =>
      SQL("""
        DELETE FROM parties
        WHERE id={id}
        """)
        .on(
          'id -> id).executeInsert()
    }
  }

  def findByName(name: String): Option[Party] = {
    DB.withConnection { implicit connection =>
      SQL("select * from parties where name={name} LIMIT 1").on(
        'name -> name).as(Party.simple singleOpt)
    }

  }

  def findOrSave(name: String, homePageUrl: String): Party = {
    var oldParty=findByName(name)
    oldParty match {
      case Some(party) => party
      case _ => save(name, homePageUrl)
    }
  }
}