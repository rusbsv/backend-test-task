package daos

import com.google.inject.Inject
import models._
import play.api.db.NamedDatabase
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

class BookDao @Inject()(
  @NamedDatabase("mydb") protected val dbConfigProvider: DatabaseConfigProvider
)(implicit ec: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  class BookTableDef(tag: Tag) extends Table[Book](tag, "books") {

    def bookId = column[Int]("bookId", O.PrimaryKey, O.AutoInc)

    def title = column[String]("title")

    def year = column[Int]("year")

    override def * = (bookId, title, year) <> (Book.tupled, Book.unapply)
  }

  class AuthorsTableDef(tag: Tag) extends Table[Author](tag, "authors") {

    def authorId = column[Int]("authorId", O.PrimaryKey, O.AutoInc)

    def authorName = column[String]("authorName")

    override def * = (authorId, authorName) <> (Author.tupled, Author.unapply)
  }

  class BooksByAuthorsTableDef(tag: Tag) extends Table[BooksByAuthors](tag, "booksByAuthors") {

    def bookId = column[Int]("bookId")

    def authorId = column[Int]("authorId")

    override def * = (bookId, authorId) <> (BooksByAuthors.tupled, BooksByAuthors.unapply)

    def booksFK = foreignKey("books", bookId, books)(
      _.bookId,
      onUpdate = ForeignKeyAction.Cascade,
      onDelete = ForeignKeyAction.Restrict
    )

    def authorsFK = foreignKey("authors", authorId, authors)(
      _.authorId,
      onUpdate = ForeignKeyAction.Cascade,
      onDelete = ForeignKeyAction.Restrict
    )
  }

  val books = TableQuery[BookTableDef]
  val authors = TableQuery[AuthorsTableDef]
  val booksByAuthors = TableQuery[BooksByAuthorsTableDef]

  def addAuthorAndGetAuthorId(authorName: String): Future[Int] = {

    val insertAuthorQuery = authors returning authors.map(_.authorId) += Author(0, authorName)

//    val authorIdIfExist = db.run(authors.filter(_.authorName === authorName).map(_.authorId).result.headOption)

    db.run(insertAuthorQuery)
  }

  def addBook(bookJson: BookAndAuthors): Future[Int] = {

    val insertBookQuery = books returning books.map(_.bookId) += Book(0, bookJson.title, bookJson.year)

    db.run(insertBookQuery)

  }

  def getBook(bookId: Int): Future[Option[Book]] = {
    db.run(books.filter(_.bookId === bookId).result.headOption)
  }

  def getAllBooks: Future[Seq[Book]] = {
    db.run(books.result)
  }
}

