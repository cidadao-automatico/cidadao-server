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
        Vote.save(User.findByEmail(user.email).get.id.get.toInt, LawProposal.findById(law_proposal_id).get.id.get.toInt, rate)
        Ok("ok")  
      }.getOrElse {
          BadRequest("Only JSON accepted")
        }
      
   }
  }

}
