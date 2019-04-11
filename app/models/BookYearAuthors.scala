package models

import play.api.libs.json.{Json, Reads, __}

case class BookYearAuthors(title: String, year: Int, authors: Seq[String])

trait BookYearAuthorsJson {
  implicit val reads = Json.reads[BookYearAuthors]

/*  implicit val bookReads: Reads[BookYearAuthors] = (
    (__ \ "title").read[String] and
    (__ \ "year").read[Int] and
    (__ \ "authors").read[List[String]]
    )(BookYearAuthors.apply _)*/

  implicit val writes = Json.writes[BookYearAuthors]
}

object BookYearAuthors extends BookYearAuthorsJson
