package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json.Json._
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

object UserController extends Controller with securesocial.core.SecureSocial {

  //GET
  def show() = SecuredAction(ajaxCall = true) { implicit request =>
    
    request.user match {
     case user: Identity => Ok(toJson(User.findByEmail(user.email)))
   }
  }

  //GET
  def recommendLaws() = SecuredAction(ajaxCall = true) { implicit request =>    

    request.user match {
      case user:Identity => 
        var lawRecommender = new LawRecommender()
        
        val pageParam: Option[String] = request.getQueryString("page")
        var page: Int = 0
        pageParam match {
          case Some(value) => 
            page = value.toInt
          case _ => 
            page = 1
        }
        Ok(toJson(lawRecommender.recommendLawsForUser(user,page)))      
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
            var tag = element.as[Tag]
            //TODO: Perhaps use map method and then do a match instead of a single comparison
            if ((element \ "enabled").as[Boolean] == true)
            {
              UserTag.save(User.findByEmail(user.email).get, tag)
            }else{
              UserTag.deleteByUserAndTag(User.findByEmail(user.email).get, tag)
            }
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
        Ok(toJson(Tag.findByUser(userObj.get)))
    }

  }
}
