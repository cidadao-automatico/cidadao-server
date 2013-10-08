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

object VoteController extends Controller {

  //POST
  def vote(law_proposal_id: Long) = Action{    
    var lawProposal=LawProposal.findById(law_proposal_id)
    //check if user is logged in
    var user=User.findById(1)
    //get this var from request
    var rate=1
    user match {
      case Some(user) => 
        lawProposal match {
          case Some(lawProposal) =>
            Vote.save(user, lawProposal, rate)
            Ok("")  
          case _ => Ok("")
        }
        
      case _  => Ok("")
    }
    
    
  }

}
