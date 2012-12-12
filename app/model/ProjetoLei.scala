package models

import play.api.db._
import play.api.Play.current

import java.util.Date

import anorm._
import anorm.SqlParser._

case class ProjetoLei(id: Pk[Long], numero: Int, tipo: String, data: Date,
					  ementa: String, tipoNorma: Option[String], numeroNorma:Option[Int], dataNorma: Option[Date])

object ProjetoLei {
 
  val simple = {
    (get[Pk[Long]]("id") ~
	get[Int]("numero") ~
	get[String]("tipo") ~
	get[Date]("data") ~
	get[String]("ementa") ~
	get[Option[String]]("tipoNorma") ~
	get[Option[Int]]("numeroNorma") ~
	get[Option[Date]]("dataNorma")
   ) map {
      case id ~ numero ~ tipo  ~ data  ~ ementa ~ tipoNorma  ~ numeroNorma~ dataNorma =>
		ProjetoLei(id,numero,tipo,data,ementa,tipoNorma,numeroNorma,dataNorma)
    }
  }
 
  def findAll(): Seq[ProjetoLei] = {
    DB.withConnection { implicit connection =>
      SQL("select * from projeto_lei").as(ProjetoLei.simple *)
    }
  }
 
  def create(projeto: ProjetoLei): Unit = {
    DB.withConnection { implicit connection =>
      SQL("""INSERT INTO projeto_lei
		  (numero,tipo,data,ementa,tipoNorma,numeroNorma,dataNorma)
		  VALUES
		  ({numero},{tipo},{data},{ementa},{tipoNorma},{numeroNorma},{dataNorma})""")
					   .on(
						 'numero -> projeto.numero,
						 'tipo -> projeto.tipo,
						 'data -> projeto.data,
						 'ementa -> projeto.ementa,
						   'tipoNorma -> projeto.tipoNorma,
						 'numeroNorma -> projeto.numeroNorma,
						 'dataNorma -> projeto.dataNorma
					   ).executeUpdate()
    }
  }
 
}
