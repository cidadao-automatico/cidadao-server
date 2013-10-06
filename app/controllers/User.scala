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

object User extends Controller {

  //GET
	def show(id: Long) = Action { implicit request =>
    User.findById(id).map { user =>
      Ok(Json.generate(user))
    }.getOrElse(NotFound)
	}

    //GET
  def recommend_laws(user_id: Long) = Action { implicit request =>
		User.findById(user_id).map{ user =>
			Ok(Json.generate(user.recommend_laws))
		}.getOrElse(NotFound)
	}

  //POST
  def add_law_region(user_id: Long) = Action{

  }
  
  //POST
  def add_tag(tag_id: Long) = Action{

  }
}
