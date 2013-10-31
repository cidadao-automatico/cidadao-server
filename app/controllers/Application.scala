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

object Application extends Controller {

	def loadPL = Action{
		var lawType=LawType.save("PEC")

		var brRegion=LawRegion.save("Brasil",LawRegion.COUNTRY,None)
		LawRegion.save("São Paulo",LawRegion.STATE,LawRegion.findByDescription("Brasil"))
		LawRegion.save("Rio de Janeiro",LawRegion.COUNTRY,LawRegion.findByDescription("Brasil"))

		Party.save("DEM","")
		Party.save("PCdoB","")
		Party.save("PDT","")
		Party.save("PEN","")
		Party.save("PHS","")
		Party.save("PMDB","")
		Party.save("PMN","")
		Party.save("PP","")
		Party.save("PPS","")
		Party.save("PR","")
		Party.save("PRB","")
		Party.save("PRP","")
		Party.save("PRTB","")
		Party.save("PSB","")
		Party.save("PSC","")
		Party.save("PSD","")
		Party.save("PSDB","")
		Party.save("PSL","")
		Party.save("PSOL","")
		Party.save("PT","")
		Party.save("PTB","")
		Party.save("PTC","")
		Party.save("PTdoB","")
		Party.save("PV","")

		User.save("Abelardo","Lupion",Option("email1@email.com"),"","",None,None,None,None,User.CONGRESS_TYPE);
		User.save("Acelino","Popó",Option("email2@email.com"),"","",None,None,None,None,User.CONGRESS_TYPE);
		User.save("Ademir","Camilo",Option("email3@email.com"),"","",None,None,None,None,User.CONGRESS_TYPE);
		User.save("Adrian","",Option("email4@email.com"),"","",None,None,None,None,User.CONGRESS_TYPE);
		User.save("Aelton","Freitas",Option("email5@email.com"),"","",None,None,None,None,User.CONGRESS_TYPE);
		User.save("Afonso","Hamm",Option("email6@email.com"),"","",None,None,None,None,User.CONGRESS_TYPE);
		User.save("Aguinaldo","Ribeiro",Option("email7@email.com"),"","",None,None,None,None,User.CONGRESS_TYPE);
		User.save("Alberto","Filho",Option("email8@email.com"),"","",None,None,None,None,User.CONGRESS_TYPE);
		User.save("Alberto","Mourão",Option("email9@email.com"),"","",None,None,None,None,User.CONGRESS_TYPE);
		User.save("Alceu","Moreira",Option("email10@email.com"),"","",None,None,None,None,User.CONGRESS_TYPE);
		User.save("Alessandro","Molon",Option("email11@email.com"),"","",None,None,None,None,User.CONGRESS_TYPE);
		User.save("Alex","Canziani",Option("email12@email.com"),"","",None,None,None,None,User.CONGRESS_TYPE);
		User.save("Alexandre","Leite",Option("email13@email.com"),"","",None,None,None,None,User.CONGRESS_TYPE);
		User.save("Alexandre","Roso",Option("email14@email.com"),"","",None,None,None,None,User.CONGRESS_TYPE);
		User.save("Alexandre","Santos",Option("email15@email.com"),"","",None,None,None,None,User.CONGRESS_TYPE);
		User.save("Alfredo","Kaefer",Option("email16@email.com"),"","",None,None,None,None,User.CONGRESS_TYPE);
		User.save("Alfredo","Sirkis",Option("email17@email.com"),"","",None,None,None,None,User.CONGRESS_TYPE);
		User.save("Alice","Portugal",Option("email18@email.com"),"","",None,None,None,None,User.CONGRESS_TYPE);
		User.save("Aline","Corrêa",Option("email19@email.com"),"","",None,None,None,None,User.CONGRESS_TYPE);
		User.save("Almeida","Lima",Option("email20@email.com"),"","",None,None,None,None,User.CONGRESS_TYPE);

		CongressmanInfo.save(User.findByEmail("email1@email.com").get, Party.findByName("DEM").get,"","")
		CongressmanInfo.save(User.findByEmail("email2@email.com").get, Party.findByName("PRB").get,"","")
		CongressmanInfo.save(User.findByEmail("email3@email.com").get, Party.findByName("PSD").get,"","")
		CongressmanInfo.save(User.findByEmail("email4@email.com").get, Party.findByName("PMDB").get,"","")
		CongressmanInfo.save(User.findByEmail("email5@email.com").get, Party.findByName("PR").get,"","")
		CongressmanInfo.save(User.findByEmail("email6@email.com").get, Party.findByName("PP").get,"","")
		CongressmanInfo.save(User.findByEmail("email7@email.com").get, Party.findByName("PP").get,"","")
		CongressmanInfo.save(User.findByEmail("email8@email.com").get, Party.findByName("PMDB").get,"","")
		CongressmanInfo.save(User.findByEmail("email9@email.com").get, Party.findByName("PSDB").get,"","")
		CongressmanInfo.save(User.findByEmail("email10@email.com").get, Party.findByName("PMDB").get,"","")
		CongressmanInfo.save(User.findByEmail("email11@email.com").get, Party.findByName("PT").get,"","")
		CongressmanInfo.save(User.findByEmail("email12@email.com").get, Party.findByName("PTB").get,"","")
		CongressmanInfo.save(User.findByEmail("email13@email.com").get, Party.findByName("DEM").get,"","")
		CongressmanInfo.save(User.findByEmail("email14@email.com").get, Party.findByName("PSB").get,"","")
		CongressmanInfo.save(User.findByEmail("email15@email.com").get, Party.findByName("PMDB").get,"","")
		CongressmanInfo.save(User.findByEmail("email16@email.com").get, Party.findByName("PSDB").get,"","")
		CongressmanInfo.save(User.findByEmail("email17@email.com").get, Party.findByName("PV").get,"","")
		CongressmanInfo.save(User.findByEmail("email18@email.com").get, Party.findByName("PCdoB").get,"","")
		CongressmanInfo.save(User.findByEmail("email19@email.com").get, Party.findByName("PP").get,"","")
		CongressmanInfo.save(User.findByEmail("email20@email.com").get, Party.findByName("PPS").get,"","")

		
		var lawAuthor1 = LawAuthor.save("Francisco Praciano")
		
		LawProposal.save(lawAuthor1, brRegion, lawType,"",2013,"329","Altera a forma de composição dos Tribunais de Contas; submete os membros do" +
			"Ministério Público de Contas ao Conselho Nacional do Ministério Público - CNMP e os Conselheiros e Ministros dos Tribunais de Contas ao "+
			"Conselho Nacional de Justiça - CNJ e dá outras providências.")

		var lawAuthor2 = LawAuthor.save("Nilson Leitão")

		LawProposal.save(lawAuthor2, brRegion, lawType,"",2013,"328","Dá nova redação ao art. 14 da Constituição Federal, para tornar facultativo o voto para todos os eleitores maiores de dezesseis anos e dá outras providências.")

		var lawAuthor3 = LawAuthor.save("Lira Maia")

		LawProposal.save(lawAuthor3, brRegion, lawType,"",2013,"327","Altera a redação do §3º do Art. 18 da Constituição Federal.")

		var lawAuthor4 = LawAuthor.save("Izalci")

		LawProposal.save(lawAuthor4, brRegion, lawType,"",2013,"326","Acrescenta parágrafos ao art. 17 da Constituição, para definir o caráter nacional como condição para o registro dos partidos políticos no Tribunal Superior Eleitoral.")

		var lawAuthor5 = LawAuthor.save("Mendonça Prado")

		LawProposal.save(lawAuthor5, brRegion, lawType,"",2013,"325","Altera os artigos nºs 145, 146, 146 A, 147, 148, 150, 151, 152, 153, 154, 155, 156, 157, 158 e 159, da Constituição da República Federativa do Brasil, instituindo Imposto Único e repartição de Receitas Tributárias no Estado Brasileiro.")

		var lawAuthor6 = LawAuthor.save("Félix Mendonça Júnior")

		LawProposal.save(lawAuthor6, brRegion, lawType,"",2013,"324","Altera os arts. 119, inciso II e 120, § 1º, inciso III da Consituição Federal, para dispor sobre a escolha de advogados na composição do Tribunal Superior Eleitoral e dos Tribunais Regionais Eleitorais. ")

		var lawAuthor7 = LawAuthor.save("Alberto Filho")

		LawProposal.save(lawAuthor7, brRegion, lawType,"",2013,"323","Acrescenta parágrafo ao art. 28, inclui inciso no art. 29 e institui parágrafo único no art. 82 da Constituição Federal, estabelecendo a obrigatoriedade da criação da Comissão de Transição de Governo  após a eleição do Presidente da República, Governadores de Estado e Prefeitos.")

		var lawAuthor8 = LawAuthor.save("Mendonça Prado")

		LawProposal.save(lawAuthor8, brRegion, lawType,"",2013,"322","Altera o § 1º e os incisos I e II, do art. 14, da Constituição Federal, instituindo o Voto Facultativo.")

		var lawAuthor9 = LawAuthor.save("Chico Lopes")

		LawProposal.save(lawAuthor9, brRegion, lawType,"",2013,"321","Altera o art. 144 da Constituição Federal, incluindo novos órgaõs de segurança pública e dando providências correlatas.")

		var lawAuthor10 = LawAuthor.save("Nilmário Miranda")

		LawProposal.save(lawAuthor10, brRegion, lawType,"",2013,"320","Dá nova redação ao art. 45 da Constituição Federal, criando vagas especiais de Deputado Federal para as comunidades indígenas e dá outras providências.")

		Ok("done")
	}

 //  val xmlVotes = scala.xml.XML.load(getClass.getResource("/data/Votacoes_2012_merge.xml"))

 //  val PROP_REGEX = "^([a-zA-Z]{1,3}) ?([0-9]{1,4}) ?/([0-9]{4}), DO EXECUTIVO".r
 //  val format = new java.text.SimpleDateFormat("dd/MM/yyyy")

 //  def loadPL = Action {
	// def parseInt(value: String): Option[Int] = {
	//   allCatch opt { value.toInt }
	// }

	// def parseDate(value: String): Option[Date] = {
	//   allCatch opt {format.parse(value)}
	// }

	// val file = Source.fromURL(getClass.getResource("/data/small-set.txt"))(scala.io.Codec.ISO8859)
	// var cnt = 0
	// var fail = 0
	// for (line <- file.getLines.take(100)) {
	//   try {
	// 	val cols = line.split('#')
	// 	val tags: Seq[Tag] = (if(cols(5).length > 0) {
	// 	  val tagStr = cols(5).split('%')
	// 	  (for { tag <- tagStr } yield Tag.findOrCreate(tag.toLowerCase)) toSeq
	// 	} else {
	// 	  Seq()
	// 	}) flatten
	// 	val comissions: Seq[Comissoe] = (if(cols(9).length > 0) {
	// 	  val comStr = cols(9).split('%')
	// 	  (for { com <- comStr } yield {
	// 		val name_short = com.split('-')
	// 		Comissoe.findOrCreate(name_short(0).trim.toLowerCase, name_short(1).trim.toLowerCase)
	// 	  }) toSeq
	// 	} else {
	// 	  Seq()
	// 	}) flatten
	// 	val proj = ProjetoLei(NotAssigned,
	// 						  cols(1).toInt,
	// 						  cols(0),
	// 						  format.parse(cols(2)),
	// 						  cols(4).replace("%", "\n"),
	// 						  None,
	// 						  None,
	// 						  None
	// 						)
	// 	val ins = ProjetoLei.create(proj)
	// 	ins match {
	// 	  case Some(id) =>
	// 		for(tag <- tags) {
	// 		  try { ProjetoLei.addTag(id, tag) } catch {case e:Exception => System.err.println(e.getMessage) }
	// 		}
	// 	  for(com <- comissions) {
	// 		try { ProjetoLei.addComissoe(id, com) } catch {case e:Exception => System.err.println(e.getMessage) }
	// 	  }
	// 	  case None => Unit
	// 	}
	// 	cnt +=1
	//   } catch {case e:Exception =>
	// 	System.err.println(e.getMessage)
	// 	fail+=1
	//   }
	// }
	// Ok("done: O"+cnt+" X"+fail)
 //  }

 //  def getTags(callback: Option[String]) = Action {
	// val all = Tag.findAll()
	// val json = toJson(all)
	// callback match {
	//   case Some(call) =>
	// 	Ok(Jsonp(call, json))
	//   case None => Ok(json)
	// }
 //  }

 //  def getPLs(page: Option[Int], callback: Option[String]) = Action {
	// val p = page match {
	//   case Some(p) => p
	//   case None => 0
	// }
	// val all = ProjetoLei.findAll(10, p)
	// callback match {
	//   case Some(call) =>
	// 	Ok(Jsonp(call,toJson(all)))
	//   case None => 		Ok(toJson(all))
	// }
 //  }

 //  def getVotes(lawid: String, callback: Option[String]) = Action {
	// val plVotes = for {
	//   votacao <- (xmlVotes \\ "Votacao")
	//   materia <- (votacao \ "@Materia")
	//   id = PROP_REGEX findFirstIn (materia text) match {
	// 	case Some(PROP_REGEX(tipo, num, year)) => Some(tipo.toUpperCase()+num+'-'+year)
	// 	case None => None
	//   }
	//  vote <- votacao \ "Vereador" if (id map { _ equals lawid }).getOrElse(false) && ((votacao \ "@TipoVotacao" text).equals("Nominal"))
	// } yield {
	//   Map(
	// 	"nome" -> (vote \ "@NomeParlamentar" text),
	// 	"partido" -> (vote \ "@Partido" text),
	// 	"voto" -> (vote \ "@Voto" text)
	//   )
	// }
	// val json = toJson(plVotes toList)
	// callback match {
	//   case Some(call) => Ok(Jsonp(call, json))
	//   case None => Ok(json)
	// }
 //  }

  /*def getVotes(lawid: String) = Action {
    Ok(lawid)
  }*/

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
