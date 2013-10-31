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


package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json.Json._
import play.api.libs.json.Json
import play.api.libs.json.JsArray
import scala.io.Source
import play.api.libs.iteratee.Enumerator
import play.api.libs.concurrent.Promise
import play.api.libs.Jsonp

import models._
import utils.ModelJson._

import anorm.NotAssigned

import scala.util.control.Exception._

import java.util.Date

import play.api.libs.ws.WS
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits._
import org.apache.commons.lang3._
import wela.examples._
import wela.core._
import wela.classifiers._
import weka.classifiers.bayes.NaiveBayes
import weka.classifiers.functions.LeastMedSq
import sys.process._
import java.net.URL
import java.io.File
import java.util.concurrent.Executors
import java.io.FileInputStream

object Application extends Controller {

	def loadPL = Action{
		
		Ok("done")
	}

 
  def loadAllDeputados() = Action{
  	AsyncResult{
  		var deputadosPromise=WS.url("http://www.camara.gov.br/SitCamaraWS/Deputados.asmx/ObterDeputados").get()

  		deputadosPromise.map{ response =>

  			var deputadosXML=response.xml
  			(deputadosXML \\ "deputado").map { xml =>
		  		var congressId = (xml \ "idParlamentar").text
		  		var sourceId = (xml \ "ideCadastro").text
		  		var fullnameDeputado = ((xml \ "nome").text.split("\\s") map {(x) => StringUtils.capitalize(x.toLowerCase) + " "}).mkString("")
		  		var shortnameDeputado =  ((xml \ "nomeParlamentar").text.split("\\s") map {(x) => StringUtils.capitalize(x.toLowerCase) + " "}).mkString("")  
		  		var urlFoto = (xml \ "urlFoto").text
		  		var uf = (xml \ "uf").text
		  		var party = (xml \ "partido").text
		  		var phoneNumber = (xml \ "fone").text
		  		var email = (xml \ "email").text
		  		
		  		println("Saving deputado "+fullnameDeputado)
		  		println(congressId+" "+ sourceId +" "+fullnameDeputado+" "+shortnameDeputado+" "+urlFoto+" "+uf+" "+party+" "+phoneNumber+" "+email)
		  		// Future {
		  			var user=User.save(fullnameDeputado,"",Option(""),"","",None,None,None,None,User.CONGRESS_TYPE)
		  			println("salvou usuario")
		  			var partyObj=Party.findOrSave(party,"")

		  			CongressmanInfo.save(user, partyObj, Option(uf), Option(""), Option(congressId.toLong), Option(sourceId.toLong), Option(shortnameDeputado), 
		  				Option(urlFoto), Option(phoneNumber), Option(email))
		  			println("salvou deputado "+fullnameDeputado)
		  		// }
	  		}  
	  		Ok("ok")			
  		}
  	}
  }

  def downloadProposicoes() = Action {
  	AsyncResult{
  	var lawTypes=List("PEC","PL")
  	var yearRange=1999 until 2014
  	for (year <- yearRange){
  		for (lawType <- lawTypes){
  			new URL("http://www.camara.gov.br/SitCamaraWS/Proposicoes.asmx/ListarProposicoes?sigla="+lawType+"&numero=&ano="+year+"&datApresentacaoIni=&datApresentacaoFim=&autor=&parteNomeAutor=&siglaPartidoAutor=&siglaUFAutor=&generoAutor=&codEstado=&codOrgaoEstado=&emTramitacao=") #> new File("/tmp/proposicoes_"+lawType+"_"+year+".xml") !!
  		}
  	}
  	Future(Ok("ok"))
  	}
  }

  class DownloadDesc(val yearData:String, val sourceData: String, val typeData: String, val stdCodeData: String) extends Runnable{
  	var sourceId: String = sourceData
  	var typeCode: String = typeData
  	var stdCode: String = stdCodeData
  	var year: String = yearData

  	def run(){
  		println("Downloading "+year+" "+typeCode+" "+stdCode+" srcId:"+sourceId)
  		var path="/tmp/leis"+sourceId.split("").mkString("/")
  		var direct=new File(path)
  		if (!direct.exists())
  		{
  			direct.mkdirs()
  		}
  		new URL("http://www.camara.gov.br/SitCamaraWS/Proposicoes.asmx/ObterProposicaoPorID?IdProp="+sourceId) #> new File(path+"/detalhe_"+typeCode+"_"+stdCode+".xml") !!	  	 		  		
  	}
  }




  class DownloadVotacao(val yearData:String, val sourceData: String, val typeData: String, val stdCodeData: String) extends Runnable{
  	var sourceId: String = sourceData
  	var typeCode: String = typeData
  	var stdCode: String = stdCodeData
  	var year: String = yearData

  	def run(){
  		println("Downloading "+year+" "+typeCode+" "+stdCode+" srcId:"+sourceId)
  		var path="/tmp/leis"+sourceId.split("").mkString("/")
  		var direct=new File(path)
  		if (!direct.exists())
  		{
  			direct.mkdirs()
  		}
  		try{

  			new URL("http://www.camara.gov.br/SitCamaraWS/Proposicoes.asmx/ObterVotacaoProposicao?tipo="+typeCode+"&numero="+stdCode+"&ano="+year) #> new File(path+"/votacao_"+typeCode+"_"+stdCode+".xml") !!	  	 		  			
  			// new URL("http://www.camara.gov.br/SitCamaraWS/Proposicoes.asmx/ObterVotacaoProposicao?tipo="+typeCode+"&numero="+stdCode+"&ano="+year)  	 		  			
  		} catch{
  			case e: Exception => println("Couldnt download "+year+" "+typeCode+" "+stdCode+" srcId:"+sourceId)
  		}
  		
  	}
  }

  def loadAllData() = Action 
  { 
  	val cores = 32
	val pool = Executors.newFixedThreadPool(cores)

	AsyncResult
	{	
		var lawTypes=List("PEC","PL")
	  	var yearRange=1999 until 2014
	  	
	  	for (year <- yearRange)
		{
			for (lawType <- lawTypes)
			{

			  	val proposicaoXML = scala.xml.XML.load(getClass.getResource("/data/proposicoes_"+lawType+"_"+year+".xml"))				
				
	  			(proposicaoXML \\ "proposicao").map { xml =>
			  		var sourceId = (xml \ "id").text
			  		var prefix = (xml \ "tipoProposicao" \ "sigla").text
			  		var stdCode = (xml \ "numero").text
			  		var year = (xml \ "ano").text
			  		var description = (xml \ "txtEmenta").text
			  		println(sourceId+" "+prefix+" "+stdCode+" "+year+" "+description)
			  		pool.submit(new DownloadDesc(year,sourceId,lawType,stdCode))
			  	}
			  	
			}  			
		}
		Future(Ok("ok"))
	}
  	
  }

  def verifyLeisData() = Action 
  { 
  	val cores = 32
	val pool = Executors.newFixedThreadPool(cores)

	AsyncResult
	{	
		var lawTypes=List("PEC","PL")
	  	var yearRange=1999 until 2014

	  	for (year <- yearRange)
		{
			for (lawType <- lawTypes)
			{

			  	val proposicaoXML = scala.xml.XML.load(getClass.getResource("/data/proposicoes_"+lawType+"_"+year+".xml"))				
				
	  			(proposicaoXML \\ "proposicao").map { xml =>
			  		var sourceId = (xml \ "id").text
			  		var prefix = (xml \ "tipoProposicao" \ "sigla").text
			  		var stdCode = (xml \ "numero").text
			  		var year = (xml \ "ano").text
			  		var description = (xml \ "txtEmenta").text
			  		var path="/tmp/leis"+sourceId.split("").mkString("/")+"/detalhe_"+lawType+"_"+stdCode+".xml"
			  		var direct=new File(path)
			  		if (!direct.exists() || direct.length()==0)
			  		{
			  			println("Lei "+lawType+" "+stdCode+" nao existe ("+path+")")
			  			pool.submit(new DownloadDesc(year,sourceId,lawType,stdCode))
			  		}
			  		// else
			  		// {
			  		// 	println("Lei "+lawType+" "+stdCode+" existe ("+path+")")
			  		// }
			  		
			  	}
			  	
			}  			
		}
		Future(Ok("ok"))
	}
  	
  }

  def loadVotacao() = Action{
  	val cores = 1
	val pool = Executors.newFixedThreadPool(cores)
	//pool.submit(new DownloadVotacao("2013","324234232","PL","4935"))

	AsyncResult
	{	
		var lawTypes=List("PEC","PL")
	  	var yearRange=1999 until 2014
	  	var file=new File("/tmp/urlsdownload.txt")
	  	val pw = new java.io.PrintWriter(file)
	  	for (year <- yearRange)
		{
			for (lawType <- lawTypes)
			{

			  	val proposicaoXML = scala.xml.XML.load(getClass.getResource("/data/proposicoes_"+lawType+"_"+year+".xml"))				
				
	  			(proposicaoXML \\ "proposicao").map { xml =>
			  		var sourceId = (xml \ "id").text
			  		var prefix = (xml \ "tipoProposicao" \ "sigla").text
			  		var stdCode = (xml \ "numero").text
			  		var year = (xml \ "ano").text
			  		var description = (xml \ "txtEmenta").text
			  		var urldownload="http://www.camara.gov.br/SitCamaraWS/Proposicoes.asmx/ObterVotacaoProposicao?tipo="+lawType+"&numero="+stdCode+"&ano="+year
			  		var path="/tmp/leis"+sourceId.split("").mkString("/")
			  		var direct=new File(path)
			  		if (!direct.exists())
			  		{
			  			direct.mkdirs()
			  		}
			  		println("wget \""+urldownload+"\" -O "+path+"/votacao_"+lawType+"_"+stdCode+".xml\n")
			  		pw.write("wget \""+urldownload+"\" -O "+path+"/votacao_"+lawType+"_"+stdCode+".xml\n")
			  		//println(sourceId+" "+prefix+" "+stdCode+" "+year+" "+description)
			  		//pool.submit(new DownloadVotacao(year,sourceId,lawType,stdCode))			  		
			  	}
			  	
			}  			
		}
		pw.close()
		Future(Ok("ok"))
	}
  }

  def loadAllLaws() = Action{

  	AsyncResult{

  		var lawTypes=List("PEC","PL")
	  	var yearRange=1999 until 2014
	  	var region=LawRegion.findByDescription("Brasil").get
	  	for (year <- yearRange)
		{
			for (lawType <- lawTypes)
			{

			  	val proposicaoXML = scala.xml.XML.load(getClass.getResource("/data/proposicoes_"+lawType+"_"+year+".xml"))				
				
	  			(proposicaoXML \\ "proposicao").map { xml =>
			  		var sourceId = (xml \ "id").text
			  		var prefix = (xml \ "tipoProposicao" \ "sigla").text
			  		var stdCode = (xml \ "numero").text
			  		var year = (xml \ "ano").text
			  		var description = (xml \ "txtEmenta").text
			  		var detalhePath="/tmp/leis"+sourceId.split("").mkString("/")+"/detalhe_"+lawType+"_"+stdCode+".xml"
			  		var detalheFile=new File(detalhePath)
			  		if (detalheFile.exists() && detalheFile.length()>0)
			  		{
			  			println("Lei "+lawType+" "+stdCode+" existe ("+detalhePath+")")
			  			val detalheXml = scala.xml.XML.load(new FileInputStream(detalheFile))	
			  			var lawTags=(detalheXml \ "Indexacao").text.split(",")
			  			
			  			var lawProposal=LawProposal.save(region, prefix, "", year.toInt, stdCode, description)
			  			for (tag <- lawTags)
			  			{	
			  				var cleanTag=(tag.replaceAll("[\\p{Punct}]","").split("\\s") map {(x) => StringUtils.capitalize(x.toLowerCase) + " "}).mkString("")
			  				var noTrailingTag=StringUtils.trim(cleanTag)
			  				println(noTrailingTag)
			  				var tagObj:Tag=Tag.findOrCreate(noTrailingTag)
			  				LawTag.save(tagObj,lawProposal)
			  				
			  			}
			  		}

			  		
			  		// else
			  		// {
			  		// 	println("Lei "+lawType+" "+stdCode+" existe ("+path+")")
			  		// }
			  		
			  	}
			  	
			}  			
		}
		Future(Ok("ok"))
  	}
  }
  //GET
  def regionList() = Action {
  		var data = toJson(LawRegion.findAll())
     	Ok(data)
  }

  //GET
  def tagList() = Action {
     Ok(toJson(Tag.findAll()))
  }

  def congressmanList = Action {
  	var jsonArray = new JsArray()
  	var congressmanList=User.findAllCongressman()
  	for (congressman <- congressmanList)
  	{
  		//TODO: this should be refactored to a proper object
  		var userObj = User.findById(congressman.id.get)
      	var userJson = toJson(userObj)
      	var congressmanJson = toJson(CongressmanInfo.findByUser(userObj.get))
      	var finalJson = Json.obj( "user" -> userJson, "congressmanInfo" -> congressmanJson)
      	jsonArray=jsonArray :+ finalJson
  	}
  	Ok(jsonArray)
  }

  def newLogin() = Action{
  	Redirect("/#/user_home")
  }
}
