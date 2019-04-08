package models

import play.api.libs.json.Json

case class BookAndAuthors(title: String, year: Int, authors: List[String])

trait BookAndAuthorsJson {
  implicit val reads = Json.reads[BookAndAuthors]
}

object BookAndAuthors extends BookAndAuthorsJson
