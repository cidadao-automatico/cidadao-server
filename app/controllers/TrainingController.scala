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
import java.io.FileOutputStream
import java.io.DataOutputStream
import java.io.FileInputStream
import java.io.DataInputStream
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
import org.apache.mahout.vectorizer.TFIDF;

object TrainingController extends Controller {

	val PATH_PREFIX="/home/gustavo/camara/models"
  val USER_PREFIX="/users"
  val PARTY_PREFIX="/parties"
  val MODEL_FILE="model.data"
  val firstTagId=Tag.findFirstId()
  var numCategories=6
  var tagCount=Tag.countAll()

   def convertToVector(tagIndexedList: List[(Int,Long)]):Vector={
      var vector:Vector = new RandomAccessSparseVector(tagIndexedList.length)
      for (tagCount <- tagIndexedList){
        vector.setQuick(tagCount._1-firstTagId,tagCount._2)
      }
      return vector
   }

   def trainClassifier() = Action {
   	AsyncResult{
      var tfidf = new TFIDF();
      var docFreq=convertToVector(LawTag.getCountPairs())      
      
      var documentCount=LawProposal.countAllVoted()
      var parties=Party.all
      for (party <- parties)
      {
        var congressmen = CongressmanInfo.findUsersByParty(party)
        var partyTupleVectors = Array[(Long,Int,Vector)]()
        for (congressman <- congressmen)
        {
          var manualVotes = Vote.findManualByUser(congressman)
          
          var lawTuples = manualVotes.map { vote => (vote.lawProposalId,vote.rate,convertToVector(Tag.findCountsByLawId(vote.lawProposalId.toInt)))}
          for (lawTuple <- lawTuples)
          {
            var wordCount=Tag.totalTagsByLawId(lawTuple._1.intValue())
            var lawVector=lawTuple._3
            for (vecIdx <- 0 to lawVector.size()-1){
              var count=lawVector.get(vecIdx)
              var freq=docFreq.get(vecIdx)
              var tfIdfValue = tfidf.calculate(count.intValue(), freq.intValue(), wordCount.intValue(), documentCount.intValue());              
              lawVector.set(vecIdx, tfIdfValue)
            }
            partyTupleVectors=partyTupleVectors :+ lawTuple
          }

          var congressmanModel=new OnlineLogisticRegression(numCategories,tagCount.toInt, new L1())
          logmsg("Training model for congressman "+congressman+" with "+lawTuples.length+" instances")
          for (lawTuple <- lawTuples)
          {
            congressmanModel.train(lawTuple._1,lawTuple._2, lawTuple._3)
          }          
          
          var relativeModelOutputPath=USER_PREFIX+generatePathFromId(congressman.id.get)
          var modelOutputPath=PATH_PREFIX+relativeModelOutputPath
          var outputPath=new File(modelOutputPath)
          if (!outputPath.exists())
          {
            outputPath.mkdirs()
          }

          var outputFilePath=modelOutputPath+"/"+MODEL_FILE
          var outputFile=new File(outputFilePath)
          logmsg("Saving model for user "+congressman.id.get+" at "+outputFilePath)
          var fileOutStream=new FileOutputStream(outputFile)
          var dataOutStream=new DataOutputStream(fileOutStream)
          congressmanModel.write(dataOutStream)
          dataOutStream.close()
          fileOutStream.close()

          User.updateModelPath(congressman.id.get, Option(relativeModelOutputPath))

        }
        logmsg("Training model for party "+party+" with "+partyTupleVectors.size+" instances")
        var partyModel=new OnlineLogisticRegression(5,tagCount.toInt, new L1())
        for (lawTuple <- partyTupleVectors){
          partyModel.train(lawTuple._1,lawTuple._2-1, lawTuple._3)
        }

        var relativeModelOutputPath=PARTY_PREFIX+generatePathFromId(party.id.get)
        var modelOutputPath=PATH_PREFIX+relativeModelOutputPath
        var outputPath=new File(modelOutputPath)
        if (!outputPath.exists())
        {
          outputPath.mkdirs()
        }

        var outputFilePath=modelOutputPath+"/"+MODEL_FILE
        var outputFile=new File(outputFilePath)
        logmsg("Saving model for party "+party.id.get+" at "+outputFilePath)
        var fileOutStream=new FileOutputStream(outputFile)
        var dataOutStream=new DataOutputStream(fileOutStream)
        partyModel.write(dataOutStream)
        dataOutStream.close()
        fileOutStream.close()
        Party.updateModelPath(party.id.get, relativeModelOutputPath)          
        
      }

   		Future(Ok("ok"))
   	}
  }

  def generatePathFromId(id: Long):String = {
    id.toString().split("").mkString("/")
  }

  class CongressmanClassifyTask(val congressman: CongressmanInfo) extends Runnable{

    def run(){
      var modelInputPath=PATH_PREFIX+USER_PREFIX+generatePathFromId(congressman.userId)+"/"+MODEL_FILE
      var fileInputStream=new FileInputStream(modelInputPath)
      var dataInputStream=new DataInputStream(fileInputStream)
      
      var partyModel=new OnlineLogisticRegression(numCategories,tagCount.toInt, new L1())
      partyModel.readFields(dataInputStream)

      var notVotedLaws=LawProposal.all(None)
      for (lawProposal <- notVotedLaws)
      {        
        var lawVector=convertToVector(Tag.findCountsByLawId(lawProposal.id.get.toInt))
        
        var probs=partyModel.classify(lawVector)
        var predictedRate=probs.maxValueIndex()+1
        logmsg("Classified Law "+ lawProposal.id.get +" for congressman "+congressman.shortName+" with rate: "+predictedRate)
        Vote.saveWithoutRate(congressman.userId.intValue(),lawProposal.id.get.intValue(),predictedRate)
        // var existingVote=Vote.findByLawAndUserIds(lawProposal.id.get, congressman.userId)
        // existingVote match{
        //   case Some(vote) => Vote.updateRates(vote.userId.intValue(),vote.lawProposalId.intValue(), vote.rate, predictedRate)
        //   case _ => Vote.saveWithoutRate(congressman.userId.intValue(),lawProposal.id.get.intValue(),predictedRate)
        // }
      }
      dataInputStream.close()
      fileInputStream.close()
    }
  }

  def classifyLaws() = Action{

    val cores = 4
    val pool = Executors.newFixedThreadPool(cores)

        
    var congressmen=CongressmanInfo.all()
    for (congressman <- congressmen)
    {
      pool.submit(new CongressmanClassifyTask(congressman))
      

    }

    
    //FIXME: Para cada modelo de usuario iterar sobre as leis não votadas manualmente
    //extrair o vetor da lei e passar pro classificador. Logo criar o voto. Fazer campo de voto manual nulo.
    Ok("ok")
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
