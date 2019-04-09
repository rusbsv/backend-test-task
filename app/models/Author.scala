package models

import play.api.libs.json.{Json, Writes}

case class Author(authorId: Int, authorName: String)

trait AuthorJson{
  implicit val autorWrites: Writes[Author] = (author: Author) => {
    Json.obj(
      "author_id" -> author.authorId,
      "author_name" -> author.authorName
    )
  }
}

object Author extends ((Int, String) => Author) with AuthorJson {
  def apply(authorId: Int, authorName: String): Author = new Author(authorId, authorName)
}
