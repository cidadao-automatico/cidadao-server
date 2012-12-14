package models

import play.api.db._
import play.api.Play.current

import java.util.Date

import anorm._
import anorm.SqlParser._

case class Tag(id: Pk[Long], tag: String, count: Option[Long])

object Tag {

  val withCount = {
	(get[Pk[Long]]("projeto_lei_has_keyword.keyword_id") ~
	get[String]("tag") ~
	 get[Long]("count")
   ) map {
	  case id ~ tag ~ count =>
		Tag(id, tag, Some(count))
	}
  }

  val simple = {
	(get[Pk[Long]]("id") ~
	get[String]("tag")
   ) map {
	  case id ~ tag =>
		Tag(id, tag, None)
	}
  }

  def find(tag: String): Option[Tag] = {
	DB.withConnection { implicit connection =>
	  SQL("select * from keyword where tag={tag}").on(
		'tag -> tag
	  ).as(Tag.simple singleOpt)
	}
  }


  def findAll(): Seq[Tag] = {
	DB.withConnection { implicit connection =>
	  SQL("""SELECT count(PROJETO_LEI_ID) as count,
				  (SELECT tag FROM keyword WHERE id=keyword_id) as tag,
				  keyword_id as id
			FROM projeto_lei_has_keyword
		  GROUP BY keyword_id""").as(Tag.withCount *)
	}
  }

  def findOrCreate(tagString: String): Option[Tag] = {
	DB.withConnection { implicit connection =>
	  find(tagString) match {
		case Some(tag) => Some(tag)
		case None =>
		  val idOpt: Option[Long] = SQL("""INSERT INTO keyword
										(tag)
										VALUES
										({tag})""")
									.on(
									  'tag -> tagString
										).executeInsert()
		  idOpt.map { id => Tag(Id(id), tagString, None) }
	  }
	}
  }

}
