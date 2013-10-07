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

import com.codahale.jerkson.Json

object LawProposalController extends Controller {

	def show(law_id: Long) = Action { implicit request =>
   LawProposal.findById(law_id).map { lawProposal =>
     Ok(Json.generate(lawProposal))
     }.getOrElse(NotFound)
   }

  def randomLaw() = Action { implicit request =>
    LawProposal.findRandom().map{ lawProposal =>
     Ok(Json.generate(lawProposal))
     }.getOrElse(NotFound)
   }
 }
