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
import play.api.db._
import anorm._
import anorm.SqlParser._

import models._
import utils.ModelJson._

import anorm.NotAssigned

import java.util.regex.Pattern
import scala.util.control.Exception._

import java.util.Date
import java.text.Normalizer

import play.api.libs.ws.WS
import scala.concurrent.duration._
import scala.concurrent._
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
import scala.xml._
import java.util.ArrayList
import scala.sys.process._

import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.classifiers.bayes.NaiveBayesUpdateable;

import play.api.libs.concurrent.Akka;
import play.api.Play.current;

object DataController extends Controller {

	val PATH_PREFIX="/home/gustavo/camara"

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
  			
  		}
  	}
  	Future(Ok("ok"))
  	}
  }

  

  def loadLawsDesc() = Action 
  { 
  	val cores = 32
	val pool = Executors.newFixedThreadPool(cores)

	AsyncResult
	{	
		var linkOutputFile=new File(PATH_PREFIX+"/linkoutput.txt")

		var linkWriter = new java.io.PrintWriter(linkOutputFile)

		def filter(fn:String) = fn.endsWith("xml")
		
		var files=for (file <- new File(PATH_PREFIX).list.filter(filter _))  yield{ PATH_PREFIX+"/"+file}

		for (file <- files)
		{
			var listFile=new File(file)
			val proposicaoXML = scala.xml.XML.load(new FileInputStream(listFile))				
			(proposicaoXML \\ "proposicao").map { xml =>
		  		var sourceId = StringUtils.trim((xml \ "id").text)
		  		var prefix = StringUtils.trim((xml \ "tipoProposicao" \ "sigla").text)
		  		var stdCode = StringUtils.trim((xml \ "numero").text)
		  		var year = StringUtils.trim((xml \ "ano").text)
		  		var description = StringUtils.trim((xml \ "txtEmenta").text)
		  		logmsg("Writing url for sourceId "+sourceId+" prefix:"+prefix+" stdCode:"+stdCode+" year:"+year)
				var path=PATH_PREFIX+sourceId.split("").mkString("/")
		  		var direct=new File(path)
		  		if (!direct.exists())
		  		{
		  			direct.mkdirs()
		  		}
		  		linkWriter.println("wget -nv http://www.camara.gov.br/SitCamaraWS/Proposicoes.asmx/ObterProposicaoPorID?IdProp="+sourceId+" -O "+path+"/detalhe_"+prefix+"_"+year+"_"+stdCode+".xml")
		  		
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

// class SaveVote(val )

class SaveLaw(val xmlData:NodeSeq, val pool: java.util.concurrent.ExecutorService) extends Runnable{
  	var xml: NodeSeq = xmlData
  	var stopwords=Set("de","da","do","sa","sem","não","sim","a","à","agora","ainda","alguém","algum","alguma","algumas","alguns","ampla","amplas","amplo","amplos","ante","antes","ao","aos","após","aquela",
			"aquelas","aquele","aqueles","aquilo","as","até","através","cada","coisa","coisas","com","como","contra","contudo","da","daquele","daqueles","das","de","dela",
			"delas","dele","deles","depois","dessa","dessas","desse","desses","desta","destas","deste","deste","destes","deve","devem","devendo","dever","deverá","deverão",
			"deveria","deveriam","devia","deviam","disse","disso","disto","dito","diz","dizem","do","dos","e","é","e'","ela","elas","ele","eles","em","enquanto","entre","era",
			"essa","essas","esse","esses","esta","está","estamos","estão","estas","estava","estavam","estávamos","este","estes","estou","eu","fazendo","fazer","feita","feitas","feito",
			"feitos","foi","for","foram","fosse","fossem","grande","grandes","há","isso","isto","já","la","la","lá","lhe","lhes","lo","mas","me","mesma","mesmas","mesmo","mesmos","meu",
			"meus","minha","minhas","muita","muitas","muito","muitos","na","não","nas","nem","nenhum","nessa","nessas","nesta","nestas","ninguém","no","nos","nós","nossa","nossas","nosso",
			"nossos","num","numa","nunca","o","os","ou","outra","outras","outro","outros","para","pela","pelas","pelo","pelos","pequena","pequenas","pequeno","pequenos","per","perante",
			"pode","pôde","podendo","poder","poderia","poderiam","podia","podiam","pois","por","porém","porque","posso","pouca","poucas","pouco","poucos","primeiro","primeiros","própria",
			"próprias","próprio","próprios","quais","qual","quando","quanto","quantos","que","quem","são","se","seja","sejam","sem","sempre","sendo","será","serão","seu","seus","si","sido",
			"só","sob","sobre","sua","suas","talvez","também","tampouco","te","tem","tendo","tenha","ter","teu","teus","ti","tido","tinha","tinham","toda","todas","todavia","todo","todos",
			"tu","tua","tuas","tudo","última","últimas","último","últimos","um","uma","umas","uns","vendo","ver","vez","vindo","vir","vos","vós")
	var pattern=Pattern.compile("\\p{InCombiningDiacriticalMarks}+")

  	def saveLaw(detalheFile:File, detalhePath:String, region:LawRegion, prefix:String, year:String, stdCode:String, description:String): LawProposal = {
  		var inputStream:FileInputStream = new FileInputStream(detalheFile)
  		val detalheXml = scala.xml.XML.load(inputStream)	
		var lawTags=(detalheXml \ "Indexacao").text.split(",")
		var regimeTramitacao = (detalheXml \ "RegimeTramitacao").text
		// logmsg("regimeTramitacao: "+regimeTramitacao)

		var lawProposal=LawProposal.save(region, prefix, "", year.toInt, stdCode, description, mapRegimeStatus(regimeTramitacao))
		logmsg("Lei id "+lawProposal.id.get+" "+year+" "+prefix+" "+stdCode+" existe ("+detalhePath+")")
		for (tag <- lawTags)
		{	
			var cleanTag=(tag.replaceAll("[\\p{Punct}]","").split("\\s") map {(x) => StringUtils.capitalize(x.toLowerCase) + " "}).mkString("")
			var tagList=StringUtils.trim(cleanTag).split("\\s")
			for (tag <- tagList)
			{
				var lowerCaseTag=tag.toLowerCase
				if (!StringUtils.isBlank(lowerCaseTag) && !stopwords.contains(lowerCaseTag))
				{
					var normTag=pattern.matcher(Normalizer.normalize(lowerCaseTag,Normalizer.Form.NFD)).replaceAll("")
					var tagObj=Tag.findOrCreate(tag,normTag)
					LawTag.save(tagObj,lawProposal)
				}
			}  				
		}	
		inputStream.close()
		return lawProposal
  	}

  	def run(){
  		
  		
  		var region=LawRegion.findByDescription("Brasil").get
  		var sourceId = (xml \ "id").text
  		var prefix = (xml \ "tipoProposicao" \ "sigla").text
  		var stdCode = (xml \ "numero").text
  		var year = (xml \ "ano").text
  		var description = (xml \ "txtEmenta").text  		

  		var detalhePath=PATH_PREFIX+sourceId.split("").mkString("/")+"/detalhe_"+prefix+"_"+year+"_"+stdCode+".xml"
  		var detalheFile=new File(detalhePath)
  		
	  	//votacaoFile.exists()
	  	
  		if (detalheFile.exists() && detalheFile.length()>0)
  		{  		
  			var lawProposal=saveLaw(detalheFile, detalhePath, region, prefix, year, stdCode, description)  			
  			pool.submit(new SaveVote(lawProposal, sourceId, prefix, year, stdCode))  			
  		}
  	}
  }


  class SaveVote(val lawProposal: LawProposal, sourceId:String, prefix: String, year:String, stdCode: String) extends Runnable
  {
  	def run(){

  		var votacaoPath=PATH_PREFIX+sourceId.split("").mkString("/")+"/votacao_"+prefix+"_"+year+"_"+stdCode+".xml"
	  	var votacaoFile=new File(votacaoPath)
	  	logmsg(" Arquivo "+votacaoPath+" ABRE: "+(votacaoFile.exists() && votacaoFile.length()>0))
	  	if (votacaoFile.exists() && votacaoFile.length()>0)
		{  					
			// logmsg("Arquivo de votacao "+votacaoPath+": "+votacaoFile.exists()+" length: "+votacaoFile.length())
			logmsg("Votacao de Lei "+lawProposal.id.get+" "+year+" "+prefix+" "+stdCode+" existe ("+votacaoPath+")")
			var inputStream=new FileInputStream(votacaoFile)
  			val votacaoXml = scala.xml.XML.load(inputStream)	
  			var deputadosList=((votacaoXml \\ "Votacao").last \ "votos" \\ "Deputado").map { deputadoXml => 
				var sourceId=(deputadoXml \ "@ideCadastro").text
				var votoText=StringUtils.trim((deputadoXml \ "@Voto").text)
				var rate = votoText match{
					case "Sim" => 5
					case _ => 1
				}
				
				var congressmanObj = CongressmanInfo.findBySourceId(sourceId.toInt)

				if (congressmanObj!=None)
				{
					Vote.save(congressmanObj.get.userId.toInt, lawProposal.id.get.toInt, rate)
					LawProposal.updateVoteStatus(lawProposal.id.get, true)
				}
					  				
			}
  			inputStream.close()
		}

  	}



  }

  def mapRegimeStatus(regime: String):String = {
  	var regimeLowCase=regime.toLowerCase
  	if (regimeLowCase.contains("urgência"))
  		return "Votação próxima"
  	else if (regimeLowCase.contains("prioridade") || regimeLowCase.contains("especial"))
  		return "Votação a médio prazo"
  	else 
  		return "Votação em prazo indefinido"
  }


  def internalLoadLaw(xml: NodeSeq) {
	  	var stopwords=Set("de","da","do","sa","sem","não","sim","a","à","agora","ainda","alguém","algum","alguma","algumas","alguns","ampla","amplas","amplo","amplos","ante","antes","ao","aos","após","aquela",
				"aquelas","aquele","aqueles","aquilo","as","até","através","cada","coisa","coisas","com","como","contra","contudo","da","daquele","daqueles","das","de","dela",
				"delas","dele","deles","depois","dessa","dessas","desse","desses","desta","destas","deste","deste","destes","deve","devem","devendo","dever","deverá","deverão",
				"deveria","deveriam","devia","deviam","disse","disso","disto","dito","diz","dizem","do","dos","e","é","e'","ela","elas","ele","eles","em","enquanto","entre","era",
				"essa","essas","esse","esses","esta","está","estamos","estão","estas","estava","estavam","estávamos","este","estes","estou","eu","fazendo","fazer","feita","feitas","feito",
				"feitos","foi","for","foram","fosse","fossem","grande","grandes","há","isso","isto","já","la","la","lá","lhe","lhes","lo","mas","me","mesma","mesmas","mesmo","mesmos","meu",
				"meus","minha","minhas","muita","muitas","muito","muitos","na","não","nas","nem","nenhum","nessa","nessas","nesta","nestas","ninguém","no","nos","nós","nossa","nossas","nosso",
				"nossos","num","numa","nunca","o","os","ou","outra","outras","outro","outros","para","pela","pelas","pelo","pelos","pequena","pequenas","pequeno","pequenos","per","perante",
				"pode","pôde","podendo","poder","poderia","poderiam","podia","podiam","pois","por","porém","porque","posso","pouca","poucas","pouco","poucos","primeiro","primeiros","própria",
				"próprias","próprio","próprios","quais","qual","quando","quanto","quantos","que","quem","são","se","seja","sejam","sem","sempre","sendo","será","serão","seu","seus","si","sido",
				"só","sob","sobre","sua","suas","talvez","também","tampouco","te","tem","tendo","tenha","ter","teu","teus","ti","tido","tinha","tinham","toda","todas","todavia","todo","todos",
				"tu","tua","tuas","tudo","última","últimas","último","últimos","um","uma","umas","uns","vendo","ver","vez","vindo","vir","vos","vós")
		var pattern=Pattern.compile("\\p{InCombiningDiacriticalMarks}+")

		var region=LawRegion.findByDescription("Brasil").get
  		var sourceId = (xml \ "id").text
  		var prefix = (xml \ "tipoProposicao" \ "sigla").text
  		var stdCode = (xml \ "numero").text
  		var year = (xml \ "ano").text
  		var description = (xml \ "txtEmenta").text  		
  		
  		var detalhePath=PATH_PREFIX+sourceId.split("").mkString("/")+"/detalhe_"+prefix+"_"+year+"_"+stdCode+".xml"
  		var detalheFile=new File(detalhePath)
  		if (detalheFile.exists() && detalheFile.length()>0)
  		{
  			logmsg("Processing Lei id "+year+" "+prefix+" "+stdCode)
	  		var votacaoPath=PATH_PREFIX+sourceId.split("").mkString("/")+"/votacao_"+prefix+"_"+year+"_"+stdCode+".xml"
		  	var votacaoFile=new File(votacaoPath)

			var detalheStream:FileInputStream = new FileInputStream(detalheFile)
	  		val detalheXml = scala.xml.XML.load(detalheStream)	
			var lawTags=(detalheXml \ "Indexacao").text.split(",")
			var regimeTramitacao = (detalheXml \ "RegimeTramitacao").text

			// logmsg("regimeTramitacao: "+regimeTramitacao)

			if (votacaoFile.exists())
			{
				if (votacaoFile.length()>0)
				{
					var lawProposal=LawProposal.save(region, prefix, "", year.toInt, stdCode, description, mapRegimeStatus(regimeTramitacao))
					logmsg("Lei id "+lawProposal.id.get+" "+year+" "+prefix+" "+stdCode+" existe ("+detalhePath+")")
					for (tag <- lawTags)
					{	
						var cleanTag=(tag.replaceAll("[\\p{Punct}]","").split("\\s") map {(x) => StringUtils.capitalize(x.toLowerCase) + " "}).mkString("")
						var tagList=StringUtils.trim(cleanTag).split("\\s")
						for (tag <- tagList)
						{
							var lowerCaseTag=tag.toLowerCase
							if (!StringUtils.isBlank(lowerCaseTag) && !stopwords.contains(lowerCaseTag))
							{
								var normTag=pattern.matcher(Normalizer.normalize(lowerCaseTag,Normalizer.Form.NFD)).replaceAll("")		
								var tagObj=Tag.findOrCreate(tag,normTag)
								LawTag.save(tagObj,lawProposal)								
							}
						}  				
					}	
					detalheStream.close()

					
				  	logmsg(" Arquivo "+votacaoPath+" ABRE: "+(votacaoFile.exists() && votacaoFile.length()>0))
				  	if (votacaoFile.exists() && votacaoFile.length()>0)
					{  					
						// logmsg("Arquivo de votacao "+votacaoPath+": "+votacaoFile.exists()+" length: "+votacaoFile.length())
						logmsg("Votacao de Lei "+lawProposal.id.get+" "+year+" "+prefix+" "+stdCode+" existe ("+votacaoPath+")")
						var votacaoStream=new FileInputStream(votacaoFile)
			  			val votacaoXml = scala.xml.XML.load(votacaoStream)	
			  			var deputadosList=((votacaoXml \\ "Votacao").last \ "votos" \\ "Deputado").map { deputadoXml => 
							var sourceId=(deputadoXml \ "@ideCadastro").text
							var votoText=StringUtils.trim((deputadoXml \ "@Voto").text)
							var rate = votoText match{
								case "Sim" => 5
								case _ => 1
							}
							
							var congressmanObj = CongressmanInfo.findBySourceId(sourceId.toInt)

							if (congressmanObj!=None)
							{
								Vote.save(congressmanObj.get.userId.toInt, lawProposal.id.get.toInt, rate)
								LawProposal.updateVoteStatus(lawProposal.id.get, true)
							}
								  				
						}
			  			votacaoStream.close()
					}

				}
			}
			else
			{
				var lawProposal=LawProposal.save(region, prefix, "", year.toInt, stdCode, description,mapRegimeStatus(regimeTramitacao))
				logmsg("Lei id "+lawProposal.id.get+" "+year+" "+prefix+" "+stdCode+" existe ("+detalhePath+")")
				for (tag <- lawTags)
				{	
					var cleanTag=(tag.replaceAll("[\\p{Punct}]","").split("\\s") map {(x) => StringUtils.capitalize(x.toLowerCase) + " "}).mkString("")
					var tagList=StringUtils.trim(cleanTag).split("\\s")
					for (tag <- tagList)
					{
						var lowerCaseTag=tag.toLowerCase
						if (!StringUtils.isBlank(lowerCaseTag) && !stopwords.contains(lowerCaseTag))
						{
							var normTag=pattern.matcher(Normalizer.normalize(lowerCaseTag,Normalizer.Form.NFD)).replaceAll("")		
							var tagObj=Tag.findOrCreate(tag,normTag)
							LawTag.save(tagObj,lawProposal)								
						}
					}  				
				}	
				detalheStream.close()
			}
		}
					

  }

  def loadAllLaws() = Action{



	val cores = 16
	val pool = Executors.newFixedThreadPool(cores)
  	AsyncResult{

	  	var yearRange=2012 until 2014
	  	
	  	for (year <- yearRange)
		{
			def filter(fn:String) = fn.contains(year.toString) && fn.contains("lista")
			var files=for (file <- new File(PATH_PREFIX).list.filter(filter _))  yield{ PATH_PREFIX+"/"+file}
			for (xmlFile <- files)
			{
				logmsg("Opening file "+xmlFile)
				var listFile=new File(xmlFile)
				val proposicaoXML = scala.xml.XML.load(new FileInputStream(listFile))				
			
	  			(proposicaoXML \\ "proposicao").map { xml =>	  	
	  				internalLoadLaw(xml)
			  	}			  		
			}
		  	

		}
		Future(Ok("ok"))
  	}
  }


  def parseAndLoadLaws() = Action{
	val cores = 64
	val pool = Executors.newFixedThreadPool(cores)
  	AsyncResult{

  		var lawTypes=List("PEC","PL")
	  	var yearRange=2010 until 2014

	  	def filter(fn:String) = fn.endsWith("xml")		
		var files=for (file <- new File(PATH_PREFIX).list.filter(filter _))  yield{ PATH_PREFIX+"/"+file}
		for (file <- files)
		{
			var listFile=new File(file)
			logmsg("Opening file "+file)
			val proposicaoXML = scala.xml.XML.load(new FileInputStream(listFile))				
			(proposicaoXML \\ "proposicao").map { xml =>
		  		pool.submit(new SaveLaw(xml,pool))
		  	}	
		}
	  
		Future(Ok("ok"))
  	}
  }


  

  def loadAllLaws2() = Action{
	val cores = 16
	val pool = Executors.newFixedThreadPool(cores)
  	AsyncResult{

  		var lawTypes=List("PEC","PL")
	  	var yearRange=2007 until 2010
	  	
	  	for (year <- yearRange)
		{
			for (lawType <- lawTypes)
			{

			  	val proposicaoXML = scala.xml.XML.load(getClass.getResource("/data/proposicoes_"+lawType+"_"+year+".xml"))				
				
	  			(proposicaoXML \\ "proposicao").map { xml =>
	  				// pool.submit(new SaveLaw(xml, lawType))
			  	}			  	
			}  			
		}
		Future(Ok("ok"))
  	}
  }

  class VotedLaws(val year:Int, val types:List[String], val pool: java.util.concurrent.ExecutorService) extends Runnable
  {
  	def run(){
  		for (lawType <- types)
  		{
  			var votacaoPromise=WS.url("http://www.camara.gov.br/SitCamaraWS/Proposicoes.asmx/ListarProposicoesVotadasEmPlenario?ano="+year+"&tipo="+lawType).get()
  			try
  			{
  				logmsg("Download votacao list for year: "+year+" and type:"+lawType)
		  		var votacaoXml = Await.result(votacaoPromise, 10 seconds).xml
		  		var listFlag = false
		  		(votacaoXml \\ "proposicoes" \ "proposicao").map { propXml =>
		  			var sourceId = (propXml \ "codProposicao").text
		  			var nomeProposicao = (propXml \ "nomeProposicao").text

		  			val lawReg=("("+lawType+") ([0-9]+)/([0-9]{4})").r

		  			
		  			var stdCode = lawReg findFirstIn (nomeProposicao) match {
						case Some(lawReg(tipoGroup, numGroup, yearGroup)) => pool.submit(new DownloadVotacao(yearGroup.toInt, sourceId, tipoGroup, numGroup))
						case None => None
					}

					listFlag=true

		  			
		  		}
		  		// logmsg("download list: "+listFlag)
		  		if (listFlag)
		  		{
		  			pool.submit(new DownloadList(lawType,year))
		  		}

			}
			catch 
			{
				case e:Exception => errmsg("Error at votacao list for year "+year+" and type "+lawType+": "+e.getMessage)
				case e:Exception => None

			}
  		}
  		
  	}
  }

  class DownloadList(val lawType:String, val year:Int) extends Runnable
  {
  	def run()
  	{
  		try{
  			logmsg("Downloading LISTS for year: "+year+" and type: "+lawType)
  			("wget -nv http://www.camara.gov.br/SitCamaraWS/Proposicoes.asmx/ListarProposicoes?sigla="+lawType+"&numero=&ano="+year+"&datApresentacaoIni=&datApresentacaoFim=&autor=&parteNomeAutor=&siglaPartidoAutor=&siglaUFAutor=&generoAutor=&codEstado=&codOrgaoEstado=&emTramitacao= -O "+PATH_PREFIX+"/lista_"+lawType+"_"+year+".xml").!
		}catch{
			case e:Exception => errmsg("Exception with download list for year: "+year+" and type: "+lawType+": "+e.getMessage)
		}
  	}
  }

  class DownloadDesc(val year:String, val sourceId: String, val typeCode: String, val stdCode: String) extends Runnable{  	

  	def run(){
  		logmsg("Downloading DESCRIPTION "+year+" "+typeCode+" "+stdCode+" srcId:"+sourceId)
  		var path=PATH_PREFIX+sourceId.split("").mkString("/")
  		var direct=new File(path)
  		if (!direct.exists())
  		{
  			direct.mkdirs()
  		}
  		("wget -nv http://www.camara.gov.br/SitCamaraWS/Proposicoes.asmx/ObterProposicaoPorID?IdProp="+sourceId+" -O "+path+"/detalhe_"+typeCode+"_"+year+"_"+stdCode+".xml").!	  	 		  		
  	}
  }

  class DownloadVotacao(val year:Int, val sourceId: String, val typeCode: String, val stdCode: String) extends Runnable{  	  	

  	def run(){
  		logmsg("Downloading VOTATION "+year+" "+typeCode+" "+stdCode+" srcId:"+sourceId)
  		var path=PATH_PREFIX+sourceId.split("").mkString("/")
  		var direct=new File(path)
  		if (!direct.exists())
  		{
  			direct.mkdirs()
  		}
  		try{

  			("wget -nv http://www.camara.gov.br/SitCamaraWS/Proposicoes.asmx/ObterVotacaoProposicao?tipo="+typeCode+"&numero="+stdCode+"&ano="+year+" -O "+path+"/votacao_"+typeCode+"_"+year+"_"+stdCode+".xml").!
  			
  		} catch{
  			case e: java.io.IOException => errmsg("Couldnt download "+year+" "+typeCode+" "+stdCode+" srcId:"+sourceId)
  		}
  		
  	}
  }

  def loadVotes() = Action {
  	val cores = 1
	val pool = Executors.newFixedThreadPool(cores)
	var lawTypes=List("MPV","PDC","PEC","PL","PLC","PLN","PLP","PLS","PLV","PPP","PPP","PPR","PRC")
	// var lawTypes=List("PEC")
	AsyncResult{
		var yearRange= 2011 until 2014
		for (year <- yearRange)
		{
			pool.submit(new VotedLaws(year, lawTypes, pool))
		}
		Future(Ok("ok"))
	}
  }

   def trainClassifier() = Action {
  	Ok("ok")
  }

  def dadoFake() = Action {

  	AsyncResult{

  		var congressmen=User.findAllCongressman()
  		for (congressman <- congressmen)
  		{
  			var votedLaws = LawProposal.findByUser(congressman)
  			if (votedLaws.length < 50)
  			{
  				println("user has no minimum "+congressman.firstName+" = "+votedLaws.length)
  				var notVotedLaws = LawProposal.lawsNotVotedForCongressman(congressman)
  				println("not voted laws for "+congressman.firstName+": "+notVotedLaws.length)
  				//while (votedLaws.length<50)
  				//{
	  				for (law <- notVotedLaws)
	  				{
	  					Vote.save(congressman.id.get.toInt, law.id.get.toInt, scala.util.Random.nextInt(5)+1)
	  				}
	  				votedLaws = LawProposal.findByUser(congressman)
	  				println("")
  				//}
  			}
  		}
  		Future(Ok("ok"))
  	}
  	
  }

  def logmsg(message: String)
  {
  	play.api.Logger.info(message)
  }

  def errmsg(message: String)
  {
  	play.api.Logger.error(message)	
  }

  def warnmsg(message: String)
  {
  	play.api.Logger.warn(message)	
  }
}
