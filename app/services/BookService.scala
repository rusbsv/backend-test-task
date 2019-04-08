package services

import com.google.inject.Inject
import daos.BookDao
import models._

import scala.concurrent.Future

class BookService @Inject()(bookDao: BookDao) {

  def addBook(bookJson: BookAndAuthors): Future[Int] = {
    bookDao.addBook(bookJson)
  }

  def getAllBooks(): Future[Seq[Book]] = {
    bookDao.getAllBooks
  }

}