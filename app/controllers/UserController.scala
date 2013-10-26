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
  def addLawRegion(region_id: Long) = SecuredAction(ajaxCall = true) { implicit request => 
    request.user match {
      case user: Identity => Ok(toJson(User.findById(1)))
    }    
  }

  //POST
  def addTag(tag_id: Long) = SecuredAction(ajaxCall = true) { implicit request =>
    request.user match {
      case user: Identity => Ok(toJson(User.findById(1)))
    }    
  }
}
