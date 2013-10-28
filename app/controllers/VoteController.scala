package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json.Json._
import scala.io.Source
import play.api.libs.iteratee.Enumerator
import play.api.libs.concurrent.Promise
import play.api.libs.Jsonp

import models._

import anorm.NotAssigned

import scala.util.control.Exception._

import java.util.Date

import securesocial.core.{Identity, Authorization}

object VoteController extends Controller with securesocial.core.SecureSocial {

  //POST
  def vote(law_proposal_id: Long) = SecuredAction(ajaxCall = true) { implicit request =>
    
    request.user match {
     case user: Identity => 
      request.body.asJson.map { json =>
        var rate = (json \ "rate").as[Int]        
        Vote.save(User.findByEmail(user.email).get, LawProposal.findById(law_proposal_id).get, rate)
        Ok("ok")  
      }.getOrElse {
          BadRequest("Only JSON accepted")
        }
      
   }
  }

}
