package models

import play.api.libs.json.{Json, Writes}

case class Book(bookId: Int, title: String, year: Int)

trait BookJson {
  implicit val bookWrites: Writes[Book] = (book: Book) => {
    Json.obj(
      "book_id" -> book.bookId,
      "title" -> book.title
    )
  }
}

object Book extends ((Int, String, Int) => Book) with BookJson {
  def apply(bookId: Int, title: String, year: Int): Book = new Book(bookId, title, year)
}