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
import scala.xml._
import java.util.ArrayList

import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.classifiers.bayes.NaiveBayesUpdateable;

object Application extends Controller {

	def loadPL = Action{
		
		Ok("done")
	}

  //GET
  def regionList() = Action {
  		var data = toJson(LawRegion.findAll())
     	Ok(data)
  }

  //GET
  def tagList() = Action {
     Ok(toJson(Comission.findAll()))
  }

  def congressmanList = Action {
  	var jsonArray = new JsArray()
  	var congressmanList=User.findFirst100Congressman()
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
