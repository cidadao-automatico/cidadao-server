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

case class ProjetoLei(id: Pk[Long], numero: Int, tipo: String, data: Date,
  ementa: String, tipoNorma: Option[String], numeroNorma: Option[Int], dataNorma: Option[Date])

object ProjetoLei {

  val simple = {
    (get[Pk[Long]]("id") ~
      get[Int]("numero") ~
      get[String]("tipo") ~
      get[Date]("data") ~
      get[String]("ementa") ~
      get[Option[String]]("tipo_norma") ~
      get[Option[Int]]("numero_norma") ~
      get[Option[Date]]("data_norma")) map {
        case id ~ numero ~ tipo ~ data ~ ementa ~ tipoNorma ~ numeroNorma ~ dataNorma =>
          ProjetoLei(id, numero, tipo, data, ementa, tipoNorma, numeroNorma, dataNorma)
      }
  }

  def findAll(per_page: Int, page: Int): PLPage = {
    DB.withConnection { implicit connection =>
      val total = SQL("select count(*) from projeto_lei").as(scalar[Long].single)
      val max = math.min(total, math.min(per_page, 500))
      val pages = math.ceil(total.toDouble / per_page) - 1 toInt
      val offset = 1 + math.min(pages, page) * max
      val rez = SQL("select * from projeto_lei limit {limit} offset {offset}")
        .on('limit -> max,
          'offset -> offset)
        .as(ProjetoLei.simple *)
      PLPage(math.min(pages, page), pages, max toInt, total, rez)
    }
  }

  def create(projeto: ProjetoLei): Option[Long] = {
    DB.withConnection { implicit connection =>
      SQL("""INSERT INTO projeto_lei
		  (numero,tipo,data,ementa,tipo_norma,numero_norma,data_norma)
		  VALUES
		  ({numero},{tipo},{data},{ementa},{tipoNorma},{numeroNorma},{dataNorma})""")
        .on(
          'numero -> projeto.numero,
          'tipo -> projeto.tipo,
          'data -> projeto.data,
          'ementa -> projeto.ementa,
          'tipoNorma -> projeto.tipoNorma,
          'numeroNorma -> projeto.numeroNorma,
          'dataNorma -> projeto.dataNorma).executeInsert()
    }
  }

  def getComissoes(projeto: ProjetoLei): Set[Comissoe] = {
    DB.withConnection { implicit connection =>
      SQL("""SELECT comissoes.*
		  FROM comissoes, projeto_lei, projeto_lei_has_comissoes
		  WHERE comissoes.id=projeto_lei_has_comissoes.comissoes_id
		  AND projeto_lei.id=projeto_lei_has_comissoes.projeto_lei_id
		  AND projeto_lei.id={id}""")
        .on('id -> projeto.id)
        .as(Comissoe.simple *)
        .toSet
    }
  }

  def addComissoe(projeto: Long, com: Comissoe): Unit = {
    DB.withConnection { implicit connection =>
      SQL("""INSERT INTO projeto_lei_has_comissoes
		  (projeto_lei_id, comissoes_id)
		  VALUES
		  ({projetoId},{comissoesId})""")
        .on(
          'projetoId -> projeto,
          'comissoesId -> com.id).executeInsert()
    }
  }

  def getTags(projeto: ProjetoLei): Set[Tag] = {
    DB.withConnection { implicit connection =>
      SQL("""SELECT keyword.*
		  FROM keyword, projeto_lei, projeto_lei_has_keyword
		  WHERE keyword.id=projeto_lei_has_keyword.keyword_id
		  AND projeto_lei.id=projeto_lei_has_keyword.projeto_lei_id
		  AND projeto_lei.id={id}""")
        .on('id -> projeto.id)
        .as(Tag.simple *)
        .toSet
    }
  }
  def addTag(projeto: Long, tag: Tag): Unit = {
    DB.withConnection { implicit connection =>
      SQL("""INSERT INTO projeto_lei_has_keyword
		  (projeto_lei_id, keyword_id)
		  VALUES
		  ({projetoId},{tagId})""")
        .on(
          'projetoId -> projeto,
          'tagId -> tag.id).executeInsert()
    }
  }

}
