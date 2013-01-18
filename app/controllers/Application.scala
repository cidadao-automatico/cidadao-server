package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json.Json._
import scala.io.Source
import play.api.libs.iteratee.Enumerator
import play.api.libs.concurrent.Promise
import play.api.libs.Jsonp

import models._
import utils.ModelJson._

import anorm.NotAssigned

import scala.util.control.Exception._

import java.util.Date

object Application extends Controller {

  val xmlVotes = scala.xml.XML.load(getClass.getResource("/data/Votacoes_2012_merge.xml"))

  val PROP_REGEX = "^([a-zA-Z]{1,3}) ?([0-9]{1,4}) ?/([0-9]{4}), DO EXECUTIVO".r
  val format = new java.text.SimpleDateFormat("dd/MM/yyyy")

  def loadPL = Action {
	def parseInt(value: String): Option[Int] = {
	  allCatch opt { value.toInt }
	}

	def parseDate(value: String): Option[Date] = {
	  allCatch opt {format.parse(value)}
	}

	val file = Source.fromURL(getClass.getResource("/data/pl-2012.txt"))(scala.io.Codec.ISO8859)
	var cnt = 0
	var fail = 0
	for (line <- file.getLines.take(100)) {
	  try {
		val cols = line.split('#')
		val tags: Seq[Tag] = (if(cols(5).length > 0) {
		  val tagStr = cols(5).split('%')
		  (for { tag <- tagStr } yield Tag.findOrCreate(tag.toLowerCase)) toSeq
		} else {
		  Seq()
		}) flatten
		val comissions: Seq[Comissoe] = (if(cols(9).length > 0) {
		  val comStr = cols(9).split('%')
		  (for { com <- comStr } yield {
			val name_short = com.split('-')
			Comissoe.findOrCreate(name_short(0).trim.toLowerCase, name_short(1).trim.toLowerCase)
		  }) toSeq
		} else {
		  Seq()
		}) flatten
		val proj = ProjetoLei(NotAssigned,
							  cols(1).toInt,
							  cols(0),
							  format.parse(cols(2)),
							  cols(4).replace("%", "\n"),
							  None,
							  None,
							  None
							)
		val ins = ProjetoLei.create(proj)
		ins match {
		  case Some(id) =>
			for(tag <- tags) {
			  try { ProjetoLei.addTag(id, tag) } catch {case e:Exception => System.err.println(e.getMessage) }
			}
		  for(com <- comissions) {
			try { ProjetoLei.addComissoe(id, com) } catch {case e:Exception => System.err.println(e.getMessage) }
		  }
		  case None => Unit
		}
		cnt +=1
	  } catch {case e:Exception =>
		System.err.println(e.getMessage)
		fail+=1
	  }
	}
	Ok("done: O"+cnt+" X"+fail)
  }

  def getTags(callback: Option[String]) = Action {
	val all = Tag.findAll()
	val json = toJson(all)
	callback match {
	  case Some(call) =>
		Ok(Jsonp(call, json))
	  case None => Ok(json)
	}
  }

  def getPLs(page: Option[Int], callback: Option[String]) = Action {
	val p = page match {
	  case Some(p) => p
	  case None => 0
	}
	val all = ProjetoLei.findAll(10, p)
	callback match {
	  case Some(call) =>
		Ok(Jsonp(call,toJson(all)))
	  case None => 		Ok(toJson(all))
	}
  }

  def getVotes(lawid: String, callback: Option[String]) = Action {
	val plVotes = for {
	  votacao <- (xmlVotes \\ "Votacao")
	  materia <- (votacao \ "@Materia")
	  id = PROP_REGEX findFirstIn (materia text) match {
		case Some(PROP_REGEX(tipo, num, year)) => Some(tipo.toUpperCase()+num+'-'+year)
		case None => None
	  }
	 vote <- votacao \ "Vereador" if (id map { _ equals lawid }).getOrElse(false) && ((votacao \ "@TipoVotacao" text).equals("Nominal"))
	} yield {
	  Map(
		"nome" -> (vote \ "@NomeParlamentar" text),
		"partido" -> (vote \ "@Partido" text),
		"voto" -> (vote \ "@Voto" text)
	  )
	}
	val json = toJson(plVotes toList)
	callback match {
	  case Some(call) => Ok(Jsonp(call, json))
	  case None => Ok(json)
	}
  }

  /*def getVotes(lawid: String) = Action {
    Ok(lawid)
  }*/

}
