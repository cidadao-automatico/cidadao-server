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

import org.apache.mahout.math.Vector;
import org.apache.mahout.classifier.sgd._
import org.apache.mahout.math.RandomAccessSparseVector;

object TrainingController extends Controller {

	val PATH_PREFIX="/home/gustavo/camara"	 
  val firstTagId=Tag.findFirstId()

   def convertToVector(tagIndexedList: List[(Int,Long)]):Vector={
      var vector:Vector = new RandomAccessSparseVector(tagIndexedList.length)
      for (tagCount <- tagIndexedList){
        vector.setQuick(tagCount._1-firstTagId,tagCount._2)
      }
      return vector
   }
   def trainClassifier() = Action {
   	AsyncResult{
      var documentFreq=LawTag.getCountPairs()
      var vectorFreq=convertToVector(documentFreq)

      
      
      var documentCount=LawProposal.countAllVoted()
      var tagCount=Tag.countAll()
      var parties=Party.all
      for (party <- parties)
      {
        var congressmen = CongressmanInfo.findUsersByParty(party)
        var partyVectors = Set[Vector]()
        for (congressman <- congressmen)
        {
          var manualVotes = Vote.findManualByUser(congressman)
          var vectorLaws = manualVotes.map { vote => (convertToVector(Tag.findCountsByLawId(vote.lawProposalId.toInt)),vote.rate)}
          var congressmanModel=new OnlineLogisticRegression(5,tagCount.toInt, new L1())
          for (vector <- vectorLaws)
          {
              logmsg("size: "+vector._1.size()+" nota:"+vector._2)
          }

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
