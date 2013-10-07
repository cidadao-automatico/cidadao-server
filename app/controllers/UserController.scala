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

object UserController extends Controller {

  //GET
	def show(user_id: Long) = Action { implicit request =>
      Ok(Json.generate(User.findById(user_id)))
	}

    //GET
  def recommendLaws(user_id: Long) = Action { implicit request =>
    var user=User.findById(user_id)
    user match {
      case Some(user) => 
        var lawRecommender = new LawRecommender()
        Ok(Json.generate(lawRecommender.recommendLawsForUser(user)))
      case _ => Ok("")
    }
			
	}

  //POST
  def addLawRegion(user_id: Long, region_id: Long) = Action{
    Ok(Json.generate(User.findById(1)))
  }
  
  //POST
  def addTag(user_id: Long, tag_id: Long) = Action{
    Ok(Json.generate(User.findById(1)))
  }
}
