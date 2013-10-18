package models

import play.api.db._
import play.api.Play.current

import java.util.Date

import anorm._
import anorm.SqlParser._

case class Comissoe(id: Pk[Long], name: String, short: String)

object Comissoe {

  val simple = {
    (get[Pk[Long]]("id") ~
      get[String]("nome") ~
      get[String]("short")) map {
        case id ~ name ~ short =>
          Comissoe(id, name, short)
      }
  }

  def find(comissoe: String): Option[Comissoe] = {
    DB.withConnection { implicit connection =>
      SQL("select * from comissoes where nome={comissoe}").on(
        'comissoe -> comissoe).as(Comissoe.simple singleOpt)
    }
  }

  def findAll(): Seq[Comissoe] = {
    DB.withConnection { implicit connection =>
      SQL("select * from comissoes").as(Comissoe.simple *)
    }
  }

  def findOrCreate(comString: String, short: String): Option[Comissoe] = {
    DB.withConnection { implicit connection =>
      find(comString) match {
        case Some(com) => Some(com)
        case None =>
          val idOpt: Option[Long] = SQL("""INSERT INTO comissoes
										(nome, short)
										VALUES
										({nome}, {short})""")
            .on(
              'nome -> comString,
              'short -> short).executeInsert()
          idOpt.map { id => Comissoe(Id(id), comString, short) }
      }
    }
  }

}
