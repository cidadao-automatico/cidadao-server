package models

import play.api.db._
import play.api.Play.current
import play.api.libs.json._
import play.api.libs.json.util._
import play.api.libs.json.Writes._
import play.api.libs.functional.syntax._

import java.util.Date

import anorm._
import anorm.SqlParser._

//case class Tag(id: Pk[Long], tag: String, count: Option[Long])
case class Tag(id: Pk[Long]=NotAssigned, name: String)

object Tag {

	implicit object PkFormat extends Format[Pk[Long]] {
        def reads(json: JsValue): JsResult[Pk[Long]] = JsSuccess (
            json.asOpt[Long].map(id => Id(id)).getOrElse(NotAssigned)
        )
        def writes(id: Pk[Long]): JsValue = id.map(JsNumber(_)).getOrElse(JsNull)
  	}

	implicit val tagWrites = Json.writes[Tag]

	

 //  val withCount = {
	// (get[Pk[Long]]("projeto_lei_has_keyword.keyword_id") ~
	// get[String]("tag") ~
	//  get[Long]("count")
 //   ) map {
	//   case id ~ tag ~ count =>
	// 	Tag(id, tag, Some(count))
	// }
 //  }

 val simple = {
 	(get[Pk[Long]]("id") ~
 		get[String]("name")
 		) map {
 		case id ~ name =>
		// Tag(id, name, None)
		Tag(id, name)
	}
}

def findByName(name: String): Option[Tag] = {
	DB.withConnection { implicit connection =>
		SQL("select * from tags where name={name}").on(
			'name -> name
			).as(Tag.simple singleOpt)
	}
}

def findAll(): Seq[Tag] = {
	DB.withConnection { implicit connection =>
		SQL("select * from tags").as(Tag.simple *)
	}
}

def save(name: String) {
	DB.withConnection{ implicit connection => 
		SQL("""
			INSERT INTO tags(name)
			VALUES({name})
			""")
		.on(
			'name -> name
			).executeInsert()
	}
}


// def findAll(): Seq[Tag] = {
// 	DB.withConnection { implicit connection =>
// 		SQL("""SELECT count(PROJETO_LEI_ID) as count,
// 			(SELECT tag FROM keyword WHERE id=keyword_id) as tag,
// 			keyword_id as id
// 			FROM projeto_lei_has_keyword
// 			GROUP BY keyword_id""").as(Tag.withCount *)
// 	}
// }

 //  def findOrCreate(name: String): Option[Tag] = {
	// DB.withConnection { implicit connection =>
	//   findByName(name) match {
	// 	case Some(tag) => Some(tag)
	// 	case None =>
	// 	  val idOpt: Option[Long] = SQL("""INSERT INTO tags
	// 									(name)
	// 									VALUES
	// 									({name})""")
	// 								.on(
	// 								  'name -> name
	// 									).executeInsert()
	// 	  idOpt.map { id => Tag(Id(id), name) }
	//   }
	// }
 //  }

}
