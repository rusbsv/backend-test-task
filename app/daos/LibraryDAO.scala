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

    override def * =
      (bookId, authorId) <> (BooksByAuthorsRelations.tupled, BooksByAuthorsRelations.unapply)

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

  def getAllBookYearAuthors: Future[Seq[BookYearAuthors]] = {
    db.run(
      (
        for {
          ba <- booksByAuthors
          b <- books if (b.bookId === ba.bookId)
          a <- authors if (a.authorId === ba.authorId)
        } yield (b, a)
        ).result.map {
        _.groupBy(_._1).map {
          case (id, book) => BookYearAuthors(id.title, id.year, book.map(_._2.authorName))
        }.toSeq
      }
    )
  }

  def getBooksList: Future[Seq[Book]] = {
    db.run(books.result)
  }

  def getAuthorsList: Future[Seq[Author]] = {
    db.run(authors.result)
  }

  def addAuthorIfNotExist(authorName: String): Future[Int] = {
    db.run(
      authors.filter(_.authorName === authorName).result.headOption.flatMap {
        case Some(a) => DBIO.successful(a.authorId)
        case None => authors returning authors.map(_.authorId) += Author(0, authorName)
      }.transactionally
    )
  }

  def addBook(book: BookYearAuthors): Future[Int] = {
    db.run(books returning books.map(_.bookId) += Book(0, book.title, book.year)).map {
      bookId =>
        book.authors.foreach(
          addAuthorIfNotExist(_).map {
            authorId => db.run(booksByAuthors += BooksByAuthorsRelations(bookId, authorId))
          }
        )
        bookId
    }
  }

  def updateBook(bookId: Int, book: BookYearAuthors): Future[String] = {
    db.run(books.filter(_.bookId === bookId).map(b => (b.title, b.year)).update(book.title, book.year)).map {
      updatedBookRows =>
        db.run(booksByAuthors.filter(_.bookId === bookId).delete)
        book.authors.foreach(
          addAuthorIfNotExist(_).map {
            authorId => db.run(booksByAuthors += BooksByAuthorsRelations(bookId, authorId))
          }
        )
        updatedBookRows.toString
    }
  }

  def deleteBook(bookId: Int): Future[Int] = {
    db.run(books.filter(_.bookId === bookId).delete)
  }
}

