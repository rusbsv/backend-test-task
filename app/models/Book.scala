package models

import play.api.libs.json.Json

case class Book(bookId: Int, title: String, year: Int)

trait BookJson {

  implicit val writes = Json.writes[Book]
}

object Book extends ((Int, String, Int) => Book) with BookJson {
  def apply(bookId: Int, title: String, year: Int): Book = new Book(bookId, title, year)
}