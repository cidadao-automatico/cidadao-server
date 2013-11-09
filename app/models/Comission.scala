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

case class Comission(id: Pk[Long], name: String, prefix: String)

object Comission {

  val simple = {
    (get[Pk[Long]]("id") ~
      get[String]("name") ~
      get[String]("prefix")) map {
        case id ~ name ~ prefix =>
          Comission(id, name, prefix)
      }
  }

  implicit object PkFormat extends Format[Pk[Long]] {
    def reads(json: JsValue): JsResult[Pk[Long]] = JsSuccess(
      json.asOpt[Long].map(id => Id(id)).getOrElse(NotAssigned))
    def writes(id: Pk[Long]): JsValue = id.map(JsNumber(_)).getOrElse(JsNull)
  }

  implicit val comissionWrites = Json.writes[Comission]
  implicit val comissionReads = Json.reads[Comission]

  def find(name: String): Option[Comission] = {
    DB.withConnection { implicit connection =>
      SQL("select * from comissions where name={name}").on(
        'name -> name).as(Comission.simple singleOpt)
    }
  }

  def findAll(): Seq[Comission] = {
    DB.withConnection { implicit connection =>
      SQL("select * from comissions").as(Comission.simple *)
    }
  }

  def findOrCreate(name: String, prefix: String): Option[Comission] = {
    DB.withConnection { implicit connection =>
      find(name) match {
        case Some(com) => Some(com)
        case None =>
          val idOpt: Option[Long] = SQL("""INSERT INTO comissions
										(name, prefix)
										VALUES
										({name}, {prefix})""")
            .on(
              'name -> name,
              'prefix -> prefix).executeInsert()
          idOpt.map { id => Comission(Id(id), name, prefix) }
      }
    }
  }

}
