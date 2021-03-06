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


package utils

import play.api.libs.json._
import play.api.libs.json.util._
import play.api.libs.json.Writes._
import play.api.libs.functional.syntax._

import java.util.Date

import models._

object ModelJson {

 //  private def comNoId(com: Comissoe): Option[(String, String)] = Some((com.name, com.short))

 //  implicit val comissoeWrites: Writes[Comissoe] = (
	// (__ \ "nome").write[String] and
	// (__ \ "short").write[String]
 //  )(unlift(comNoId))

 //  private def tagNoId(tag: Tag): Option[(String, Option[Long])] = Some((tag.tag, tag.count))

 //  implicit val tagWrites: Writes[Tag] = (
	// (__ \ "tag").write[String] and
	// (__ \ "count").write[Option[Long]]
 //  )(unlift(tagNoId))


 //  private def plNoId(proj: ProjetoLei): Option[(Int, String, Date, String, Option[String], Option[Int], Option[Date], Set[String], Set[Comissoe])] = {
	// val tags = ProjetoLei.getTags(proj).map(_.tag)
	// val coms = ProjetoLei.getComissoes(proj)
	// Some((proj.numero, proj.tipo, proj.data, proj.ementa, proj.tipoNorma, proj.numeroNorma, proj.dataNorma, tags, coms))
 //  }

 //  implicit val projetoWrites: Writes[ProjetoLei] = (
	// (__ \ "numero").write[Int] and
	// (__ \ "tipo").write[String] and
	// (__ \ "data").write[Date] and
	// (__ \ "ementa").write[String] and
	// (__ \ "tipoNorma").write[Option[String]] and
	// (__ \ "numeroNorma").write[Option[Int]] and
	// (__ \ "dataNorma").write[Option[Date]] and
	// (__ \ "tags").write[Set[String]] and
	// (__ \ "comissoes").write[Set[Comissoe]]
 //  )(unlift(plNoId))

 //  implicit val pageWrites: Writes[PLPage] = (
	// (__ \ "page").write[Long] and
	// (__ \ "pages").write[Long] and
	// (__ \ "perpage").write[Int] and
	// (__ \ "total").write[Long] and
	// (__ \ "content").write[Seq[ProjetoLei]]
 //  )(unlift(PLPage.unapply))


}
