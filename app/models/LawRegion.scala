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

import utils._

case class LawRegion(id: Pk[Long] = NotAssigned, description: String, typeName: String, parentRegionId: Option[Long])

object LawRegion {

  val COUNTRY: String = "COUNTRY"
  val STATE: String = "STATE"
  val CITY: String = "CITY"

  implicit object PkFormat extends Format[Pk[Long]] {
    def reads(json: JsValue): JsResult[Pk[Long]] = JsSuccess(
      json.asOpt[Long].map(id => Id(id)).getOrElse(NotAssigned))
    def writes(id: Pk[Long]): JsValue = id.map(JsNumber(_)).getOrElse(JsNull)
  }

  implicit val regionWrites = Json.writes[LawRegion]
  implicit val regionReads = Json.reads[LawRegion]

  val simple = {
    (get[Pk[Long]]("id") ~
      get[String]("description") ~
      get[String]("type_name") ~
      get[Option[Long]]("region_parent_id")) map {
        case id ~ description ~ type_name ~ region_parent_id =>
          LawRegion(id, description, type_name, region_parent_id)
      }
  }

  def findAll(): Seq[LawRegion] = {
    DB.withConnection { implicit connection =>
      SQL("select * from law_regions").as(LawRegion.simple *)
    }
  }

  def findById(id: Long): Option[LawRegion] = {
    DB.withConnection { implicit connection =>
      SQL("select * from law_regions where id={id}").on(
        'id -> id).as(LawRegion.simple singleOpt)
    }
  }

  def findByDescription(description: String): Option[LawRegion] = {
    DB.withConnection { implicit connection =>
      SQL("select * from law_regions where description={description}").on(
        'description -> description).as(LawRegion.simple singleOpt)
    }
  }

  def findByUser(user: User): Seq[LawRegion] = {
    DB.withConnection { implicit connection =>
      SQL("select * from law_regions inner join user_law_regions on law_regions.id=user_law_regions.law_region_id where user_law_regions.user_id={userId} order by law_regions.id").on(
        'userId -> user.id).as(LawRegion.simple *)
    }    
  }

  def save(description: String, typeName: String, parent: Option[LawRegion]) : LawRegion = {
    var idOpt: Option[Long] = None
    parent match {
      case Some(parentRegion) =>
        DB.withConnection { implicit connection =>
          idOpt = SQL("""
              INSERT INTO law_regions(description, type_name, region_parent_id)
              VALUES({description},{type_name},{region_parent_id})
              """)
            .on(
              'description -> description,
              'type_name -> typeName,
              'region_parent_id -> parentRegion.id).executeInsert()

          idOpt.map { id => LawRegion(Id(id), description, typeName, Option(parentRegion.id.get)) }.get
        }
      case _ =>
        DB.withConnection { implicit connection =>
          idOpt = SQL("""
              INSERT INTO law_regions(description, type_name)
              VALUES({description}, {type_name})
              """)
            .on(
              'description -> description,
              'type_name -> typeName).executeInsert()

          idOpt.map { id => LawRegion(Id(id), description, typeName, None) }.get
        }
    }


    

  }

  def deleteById(id: Long) {
    DB.withConnection { implicit connection =>
      SQL("""
        DELETE FROM law_regions
        WHERE id={id}
        """)
        .on(
          'id -> id).executeInsert()
    }
  }
}