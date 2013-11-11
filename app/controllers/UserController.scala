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
import scala.io.Source
import play.api.libs.iteratee.Enumerator
import play.api.libs.concurrent.Promise
import play.api.libs.Jsonp
import play.api.libs.json.JsArray
import play.api.libs.json.JsObject

import models._
import utils.ModelJson._

import anorm.NotAssigned

import scala.util.control.Exception._

import java.util.Date

import securesocial.core.{Identity, Authorization}
import play.api.libs.ws.WS
import scala.concurrent.duration._
import scala.concurrent._
import play.api.libs.concurrent.Execution.Implicits._

import java.io.File
import java.io.FileOutputStream
import java.io.DataOutputStream
import java.io.FileInputStream
import java.io.DataInputStream

import org.apache.mahout.math.Vector;
import org.apache.mahout.classifier.sgd._
import org.apache.mahout.math.RandomAccessSparseVector;
import org.apache.mahout.vectorizer.TFIDF;

object UserController extends Controller with securesocial.core.SecureSocial {

  val PATH_PREFIX="/vigiapolitico/models/staging/models"
  val USER_PREFIX="/users"
  val PARTY_PREFIX="/parties"
  val MODEL_FILE="model.data"
  val firstTagId=Tag.findFirstId()
  var numCategories=6
  var tagCount=Tag.countAll()
  var tfidf = new TFIDF();
  var docFreq=convertToVector(LawTag.getCountPairs())      
  var documentCount=LawProposal.countAllVoted()

  //GET
  def show() = SecuredAction(ajaxCall = true) { implicit request =>
    
    request.user match {
     case user: Identity => 
      var userJson = toJson(User.findByEmail(user.email))
      Ok(userJson)
   }
  }

  def convertToVector(tagIndexedList: List[(Int,Long)]):Vector={
      var vector:Vector = new RandomAccessSparseVector(tagIndexedList.length)
      for (tagCount <- tagIndexedList){
        vector.setQuick(tagCount._1-firstTagId,tagCount._2)
      }
      return vector
   }

   def generatePathFromId(id: Long):String = {
    id.toString().split("").mkString("/")
  }

  //GET
  def recommendLaws() = SecuredAction(ajaxCall = true) { implicit request =>    

    request.user match {
      case user:Identity => 
        AsyncResult
        {
          var dbUser:User=User.findByEmail(user.email).get
          var modelInputPath=PATH_PREFIX+USER_PREFIX+generatePathFromId(dbUser.id.get)+"/"+MODEL_FILE
          var modelFile=new File(modelInputPath)
          var isModelCreated=User.findModelPath(dbUser.id.get)
          if (isModelCreated==None)
          {
            var userModel=new OnlineLogisticRegression(numCategories,tagCount.toInt, new L1())
            var relativeModelOutputPath=USER_PREFIX+generatePathFromId(dbUser.id.get)
            var modelParentPath=PATH_PREFIX+relativeModelOutputPath
            var modelParentFile=new File(modelParentPath)
            modelParentFile.mkdirs()

            var manualVotes = Vote.findManualByUser(dbUser)
            var lawTuples = manualVotes.map { vote => (vote.lawProposalId,vote.rate.get,convertToVector(Tag.findCountsByLawId(vote.lawProposalId.toInt)))}
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
            }
            for (lawTuple <- lawTuples)
            {
              userModel.train(lawTuple._1,lawTuple._2, lawTuple._3)
            }
            
            var outputFilePath=modelParentPath+"/"+MODEL_FILE
            var outputFile=new File(outputFilePath)
            logmsg("Saving model for user "+dbUser.id.get+" at "+outputFilePath)
            var fileOutStream=new FileOutputStream(outputFile)
            var dataOutStream=new DataOutputStream(fileOutStream)
            userModel.write(dataOutStream)
            dataOutStream.close()
            fileOutStream.close()

            User.updateModelPath(dbUser.id.get, Option(relativeModelOutputPath))
          }
        
          var existingModel=new OnlineLogisticRegression(numCategories,tagCount.toInt, new L1())
          var fileInputStream=new FileInputStream(modelInputPath)
          var dataInputStream=new DataInputStream(fileInputStream)
          existingModel.readFields(dataInputStream)

          val pageParam: Option[String] = request.getQueryString("page")
          var page: Int = 0
          pageParam match {
            case Some(value) => 
              page = value.toInt
            case _ => 
              page = 1
          }

          var jsonArray = new JsArray()
          var lawProposals=LawProposal.findByUserConfiguration(dbUser, page)
          for (lawProposal <- lawProposals)
          {
            var lawVector=convertToVector(Tag.findCountsByLawId(lawProposal.id.get.toInt))
            var probs=existingModel.classify(lawVector)
            var predictedRate=probs.maxValueIndex()+1            
            var representativesRates=congressmanRatePrediction(lawProposal, dbUser)
            var representativesJson = new JsArray()
            for (rateTuple <- representativesRates)
            {
              var voteJson = Json.obj( "rate" -> "", "predictedRate" -> rateTuple._2)  
              var congressmanJson = toJson(CongressmanInfo.findByUser(rateTuple._1))
              var finalJson = Json.obj( "vote" -> voteJson, "congressmanInfo" -> congressmanJson)
              representativesJson=representativesJson :+ finalJson
            }

            var partiesJson=new JsArray()
            // var partiesRates=partiesRatePrediction(lawProposal)
            // for (partyTuple <- partiesRates)
            // {
            //   var voteJson = Json.obj( "rate" -> "", "predictedRate" -> partyTuple._2)  
            //   var partyJson = toJson(Party.findById(partyTuple._1.id.get))
            //   var finalJson = Json.obj( "vote" -> voteJson, "congressmanInfo" -> partyJson)
            //   partiesJson=partiesJson :+ finalJson 
            // }

            var lawProposalJson = toJson(lawProposal)
            var voteJson = Json.obj( "rate" -> "", "predictedRate" -> predictedRate)

            var finalJson = Json.obj( "lawProposal" -> lawProposalJson, "vote" -> voteJson, "representatives" -> representativesJson, "parties" -> partiesJson)


            jsonArray=jsonArray :+ finalJson
          }     
          
          Future(Ok(jsonArray))
          
        }
        
    }

  }

  def congressmanRatePrediction(lawProposal: LawProposal, user: User):Array[(User,Int)]={
    var representatives=UserRepresentative.findByUser(user).map{ rep => rep.congressman_id }

    var rateCongressmen=Array[(User,Int)]() 
    for (representative <- representatives)
    {
      var userObj:User = User.findById(representative).get     
      var predictedRate:Int = Vote.findByLawAndUserIds(lawProposal.id.get, userObj.id.get).get.predictedRate.get
      var rateTuple=(userObj,predictedRate)
      rateCongressmen=rateCongressmen :+ rateTuple
    }
    rateCongressmen.sortBy(_._2)
    
    var otherCongressmen=Array[(User,Int)]() 
    var congressmen=CongressmanInfo.all().map{ cong => cong.userId }
    congressmen=congressmen.diff(representatives)
    for (congressman <- congressmen)
    {
      var userObj:User = User.findById(congressman).get
      var predictedRate:Int = Vote.findByLawAndUserIds(lawProposal.id.get, userObj.id.get).get.predictedRate.get
      var rateTuple=(userObj,predictedRate)
      otherCongressmen=otherCongressmen :+ rateTuple 
    }
        
    otherCongressmen.sortBy(_._2)
    rateCongressmen ++ otherCongressmen
  }

  def partiesRatePrediction(lawProposal: LawProposal):Array[(Party,Int)]={
    var parties:Seq[Party]=Party.all()

    var partiesRate=Array[(Party,Int)]() 
    var lawVector=convertToVector(Tag.findCountsByLawId(lawProposal.id.get.intValue))
    for (party <- parties)
    {
      var modelInputPath=PATH_PREFIX+PARTY_PREFIX+generatePathFromId(party.id.get)+"/"+MODEL_FILE      
      var existingModel=new OnlineLogisticRegression(numCategories,tagCount.toInt, new L1())

      var fileInputStream=new FileInputStream(modelInputPath)
      var dataInputStream=new DataInputStream(fileInputStream)
      existingModel.readFields(dataInputStream)
      
      var probs=existingModel.classify(lawVector)
      dataInputStream.close()
      fileInputStream.close()
      var predictedRate=probs.maxValueIndex()+1            
      var rateTuple=(party,predictedRate)
      partiesRate=partiesRate :+ rateTuple
    }
        
    partiesRate.sortBy(_._2)
  }

  def predictRateForCongressman() = SecuredAction(ajaxCall = true) { implicit request =>    

    request.user match {
      case user:Identity => 
        AsyncResult
        {
          request.body.asJson.map { json =>
            // var lawProposal= (json \ "lawProposal").as[LawProposal]
            // val pageParam: Option[String] = request.getQueryString("lawProposal")
            // var dbUser:User=User.findByEmail(user.email).get
            
            
            // var jsonArray = new JsArray()
            // for (rateTuple <- rateCongressman)
            // {
            //   var voteJson = Json.obj( "rate" -> "", "predictedRate" -> rateTuple._2)  
            //   var congressmanJson = toJson(CongressmanInfo.findByUser(rateTuple._1.asInstanceOf[User]))
            //   var finalJson = Json.obj( "vote" -> voteJson, "congressmanInfo" -> congressmanJson)
            // }           
            
            // Future(Ok(jsonArray))  
            Future(Ok("ok"))
          }.getOrElse{
            Future(BadRequest("Only JSON accepted"))
          }
            
          
          
        }
        
    }

  }

  //POST
  def addLawRegions() = SecuredAction(ajaxCall = true) { implicit request => 
    request.user match {
      case user: Identity =>
        request.body.asJson.map { json =>

          (json \ "regions").as[List[JsObject]].map { element =>
            var region = element.as[LawRegion]
            //TODO: Perhaps use map method and then do a match instead of a single comparison
            if ((element \ "enabled").as[Boolean] == true)
            {
              UserLawRegion.save(User.findByEmail(user.email).get,region)              
            }else{
              UserLawRegion.deleteByUserAndRegion(User.findByEmail(user.email).get,region)
            }
          }
          
          Ok("Ok")          
        }.getOrElse {
          BadRequest("Only JSON accepted")
        }
    }    
  }

  //POST
  def addLawTags() = SecuredAction(ajaxCall = true) { implicit request =>
    request.user match {
      case user: Identity =>
        request.body.asJson.map { json =>

          (json \ "tags").as[List[JsObject]].map { element =>
            var comission = element.as[Comission]
            //TODO: Perhaps use map method and then do a match instead of a single comparison
            if ((element \ "enabled").as[Boolean] == true)
            {
              UserComission.save(User.findByEmail(user.email).get, comission)
            }else{
              UserComission.deleteByUserAndComission(User.findByEmail(user.email).get, comission)
            }
          }
          
          Ok("Ok")          
        }.getOrElse {
          BadRequest("Only JSON accepted")
        }
    }
  }

  //POST
  def addRepresentatives() = SecuredAction(ajaxCall = true) { implicit request =>
    request.user match {
      case user: Identity =>
        request.body.asJson.map { json =>
          var dbUser:User=User.findByEmail(user.email).get
          UserRepresentative.deleteByUser(dbUser)
          (json \ "congressmanList").as[List[JsObject]].map { container =>            
            var congressman = (container \ "user").as[User]            
            UserRepresentative.save(dbUser, congressman)            
          }
          
          Ok("Ok")          
        }.getOrElse {
          BadRequest("Only JSON accepted")
        }
    }
  }

  def selectedRegions() = SecuredAction(ajaxCall = true) { implicit request =>
    request.user match {
      case user: Identity => 
        var userObj : Option[User] = User.findByEmail(user.email)
        Ok(toJson(LawRegion.findByUser(userObj.get)))
    }

  }

  def selectedTags() = SecuredAction(ajaxCall = true) { implicit request =>
    request.user match {
      case user: Identity => 
        var userObj : Option[User] = User.findByEmail(user.email)
        Ok(toJson(Comission.findByUser(userObj.get)))
    }
  }

  def selectedRepresentatives() = SecuredAction(ajaxCall = true) { implicit request =>
    request.user match {
      case user: Identity => 
        var userRepresentatives = UserRepresentative.findByUser(User.findByEmail(user.email).get)
        var jsonArray = new JsArray()
        for (userRepresentative <- userRepresentatives)
        {
          //TODO: this should be refactored to a proper object
          var userObj = User.findById(userRepresentative.congressman_id)
          var userJson = toJson(userObj)
          var congressmanJson = toJson(CongressmanInfo.findByUser(userObj.get))
          var finalJson = Json.obj( "user" -> userJson, "congressmanInfo" -> congressmanJson)
          jsonArray=jsonArray :+ finalJson
        }
        Ok(jsonArray)
    }
  }

  def updateConfigured() = SecuredAction(ajaxCall = true) { implicit request =>
    request.user match {
      case user: Identity =>
        var userObj = User.findByEmail(user.email).get
        userObj.configured=true
        User.updateConfigured(userObj)
        Ok(toJson(userObj))
    }
  }


  def votes() = SecuredAction(ajaxCall = true) { implicit request =>
    request.user match {
      case user: Identity =>
        var userObj = User.findByEmail(user.email).get        
        Ok(toJson(Vote.findByUser(userObj)))
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
