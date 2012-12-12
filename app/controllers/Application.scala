package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json.Json._
import scala.io.Source
import play.api.libs.iteratee.Enumerator
import play.api.libs.concurrent.Promise

import models.ProjetoLei

import anorm.NotAssigned

import scala.util.control.Exception._

import java.util.Date

object Application extends Controller {

  val xmlVotes = scala.xml.XML.load(getClass.getResource("/cmsp-data/Votacoes_2012_merge.xml"))

  val PROP_REGEX = "([a-zA-Z]{1,3}) ?([0-9]{1,4}) ?/([0-9]{4}), DO EXECUTIVO".r
  val format = new java.text.SimpleDateFormat("dd/MM/yyyy")

  def loadPL = Action {
	def parseInt(value: String): Option[Int] = {
	  allCatch opt { value.toInt }
	}

	def parseDate(value: String): Option[Date] = {
	  allCatch opt {format.parse(value)}
	}

	val file = Source.fromURL(getClass.getResource("/cmsp-data/pl.txt"))(scala.io.Codec.ISO8859)
	var cnt = 0
	val projs = for {line <- file.getLines take 100} yield {
	  val cols = line.split('#')
	  try {
		val proj = ProjetoLei(NotAssigned,
							  cols(1).toInt,
							  cols(0),
							  format.parse(cols(2)),
							  cols(4),
							  None,
							  None,
							  None
							)
		ProjetoLei.create(proj)
		System.out.print("1")
		Some(proj)
	  } catch {case e:Exception => None}
	}
	val it = (projs  flatten) map {_.toString}
	val enum: Enumerator[String] = Enumerator.fromCallback { () =>
	  if(it.hasNext)
		Promise.pure(Some(it.next))
	  else
		Promise.pure(None)
																	 }
	Ok.stream(enum)
  }

  def index = Action {
	val all = ProjetoLei.findAll()
    Ok("size:"+all.length)
  }

  def getVotes(lawid: String) = Action {
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
	Ok(toJson(plVotes toList))
  }

  /*def getVotes(lawid: String) = Action {
    Ok(lawid)
  }*/

}
