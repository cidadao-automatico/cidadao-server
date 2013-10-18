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

object LawProposalController extends Controller {

  def show(law_id: Long) = Action { implicit request =>
    var lawProposal = LawProposal.findById(law_id)
    Ok(toJson(lawProposal.get))
    // lawProposal match{
    //   case Some(lawProposal) => Ok(toJson(lawProposal))
    //   case _ => Ok("")
    // }   
  }

  def randomLaw() = Action { implicit request =>
    LawProposal.findRandom().map { lawProposal =>
      Ok(toJson(lawProposal))
    }.getOrElse(NotFound)
  }
}
