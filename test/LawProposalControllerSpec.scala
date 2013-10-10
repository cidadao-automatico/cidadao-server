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
            //User.save("Gustavo", "Salazar Torres", Option.apply("tavoaqp@gmail.com"), "912871983712","Facebook",Option.apply("Peru"), Option.apply("Lima"), Option.apply("Lima"), Option.apply("29733722"),User.NORMAL_TYPE)
            User.save("Gustavo", "Salazar Torres", Option("tavoaqp@gmail.com"), "912871983712","Facebook",Option("Peru"), Option("Lima"), Option("Lima"), Option("29733722"),User.NORMAL_TYPE)
            var user = User.findById(1)
            LawType.save("MUNICIPAL")
            var lawType=LawType.findById(1)
            LawStatus.save("VOTADA")
            var lawStatus=LawStatus.findById(1)
            LawPriority.save("URGENTE")
            var lawPriority=LawPriority.findById(1)
            LawRegion.save("Brasil","PAIS", None)
            var lawRegion=LawRegion.findById(1)
            LawAuthor.save("Gustavo Steinberg")
            var lawAuthor=LawAuthor.findById(1)            
            LawProposal.save(lawPriority, lawAuthor, lawRegion, lawType, lawStatus, url, createdAt, stdCode)
            var lawProposal=LawProposal.findById(1)
            var response=route(FakeRequest(GET, "/law_proposal/1/show"))
            var lawProposalJson=toJson(lawProposal).toString()

            status(response.get) must equalTo(200)
            contentAsString(response.get) must equalTo(lawProposalJson)


        }
    }
  }
 
}