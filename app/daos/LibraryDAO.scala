package daos

import com.google.inject.Inject
import models._
import play.api.db.NamedDatabase
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

class LibraryDAO @Inject()(
  @NamedDatabase("mydb") protected val dbConfigProvider: DatabaseConfigProvider
)(implicit ec: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  class BookTableDef(tag: Tag) extends Table[Book](tag, "books") {

    def bookId = column[Int]("book_id", O.PrimaryKey, O.AutoInc)

    def title = column[String]("title")

    def year = column[Int]("year")

    override def * = (bookId, title, year) <> (Book.tupled, Book.unapply)
  }

  class AuthorsTableDef(tag: Tag) extends Table[Author](tag, "authors") {

    def authorId = column[Int]("author_id", O.PrimaryKey, O.AutoInc)

    def authorName = column[String]("author_name")

    override def * = (authorId, authorName) <> (Author.tupled, Author.unapply)
  }

  class BooksByAuthorsTableDef(tag: Tag) extends Table[BooksByAuthorsRelations](tag, "books_by_authors") {

    def bookId = column[Int]("book_id")

    def authorId = column[Int]("author_id")

    override def * = (bookId, authorId) <> (BooksByAuthorsRelations.tupled, BooksByAuthorsRelations.unapply)

    def booksFK = foreignKey("books_fk", bookId, books)(
      _.bookId,
      onUpdate = ForeignKeyAction.Cascade,
      onDelete = ForeignKeyAction.Cascade
    )

    def authorsFK = foreignKey("authors_fk", authorId, authors)(
      _.authorId,
      onUpdate = ForeignKeyAction.Cascade,
      onDelete = ForeignKeyAction.Cascade
    )
  }

  val books = TableQuery[BookTableDef]
  val authors = TableQuery[AuthorsTableDef]
  val booksByAuthors = TableQuery[BooksByAuthorsTableDef]

/*  def getAllBookYearAuthors: Future[Seq[BookYearAuthors]] = {
    db.run(
      books.map {

      }
    )
  }*/

  //def getBookYearAuthorsById: Future[Seq[BookYearAuthors]] = {  }

  def getBooksList: Future[Seq[Book]] = {
    db.run(books.result)
  }

  def getBookById(bookId: Int): Future[Option[Book]] = {
    db.run(books.filter(_.bookId === bookId).result.headOption)
  }

  def getAuthorsList: Future[Seq[Author]] = {
    db.run(authors.result)
  }

  def getAuthorById(authorId: Int): Future[Option[Author]] = {
    db.run(authors.filter(_.authorId === authorId).result.headOption)
  }

  def addAuthorIfNotExist(authorName: String): Future[Int] = {
    db.run(
      authors.filter(_.authorName === authorName).result.headOption.flatMap {
        case Some(a) => DBIO.successful(a.authorId)
        case None => authors returning authors.map(_.authorId) += Author(0, authorName)
      }.transactionally
    )
  }

  def addBook(bookJson: BookYearAuthors): Future[Int] = {
    db.run(books returning books.map(_.bookId) += Book(0, bookJson.title, bookJson.year)).map {
      bookId =>
        bookJson.authors.foreach(
          addAuthorIfNotExist(_).map {
            authorId => db.run(booksByAuthors += BooksByAuthorsRelations(bookId, authorId))
          }
        )
        bookId
    }
  }

  /*  def updateBook(bookId: Int): Future[String] = {
          db.run(books.filter(_.bookId === bookId))
    }*/

  def deleteBook(bookId: Int): Future[Int] = {
    db.run(books.filter(_.bookId === bookId).delete)
  }
}

