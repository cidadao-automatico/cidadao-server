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


package test

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._
import models._
import java.util.Date
import java.util.Calendar
import scala.concurrent._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.json.util._
import play.api.libs.json.Writes._
import play.api.libs.functional.syntax._
import play.api.libs.json.Json._

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
class LawProposalControllerSpec extends Specification with Results {
  
  "LawProposalController" should {

    trait trees extends After {                

                def after = {
                    User.deleteById(1)
                    LawType.deleteById(1)
                    LawStatus.deleteById(1)
                    LawPriority.deleteById(1)
                    LawRegion.deleteById(1)
                    LawAuthor.deleteById(1)
                    LawProposal.deleteById(1)
                }
            }

    "return a LawProposal object in JSON format" in {
        running(FakeApplication()) {
            var stdCode="PLasdasda"
            var createdAt=Calendar.getInstance().getTime()
            var url="http://asdasdasd"
            
            var user = User.save("Gustavo", "Salazar Torres", Option("tavoaqp@gmail.com"), 
                "912871983712","Facebook",Option("Peru"), Option("Lima"), Option("Lima"), Option("29733722"),User.NORMAL_TYPE)
            
            var lawType=LawType.save("MUNICIPAL")
            var lawStatus=LawStatus.save("VOTADA")
            var lawPriority=LawPriority.save("URGENTE")            
            var lawRegion=LawRegion.save("Brasil","PAIS", None)
            var lawAuthor=LawAuthor.save("Gustavo Steinberg")

            var lawProposal=LawProposal.save(lawAuthor, lawRegion, lawType,  url, 2013, stdCode, "askdhaksdjahkdjahkdjahkdjahdskjh")
            
            var response=route(FakeRequest(GET, "/law_proposal/"+lawProposal.id+"/show"))
            var lawProposalJson=toJson(lawProposal).toString()

            status(response.get) must equalTo(200)
            contentAsString(response.get) must equalTo(lawProposalJson)


        }
    }
  }
 
}