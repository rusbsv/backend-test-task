package services

import com.google.inject.Inject
import daos.LibraryDAO
import models._

import scala.concurrent.Future

class LibraryService @Inject()(libraryDao: LibraryDAO) {

/*  def getAllBooksYearAuthors: Future[Seq[BookYearAuthors]] = {
    libraryDao.getAllBookYearAuthors
  }*/

  def getBooksList: Future[Seq[Book]] = {
    libraryDao.getBooksList
  }

  def getAuthorsList: Future[Seq[Author]] = {
    libraryDao.getAuthorsList
  }

  def addBook(bookJson: BookYearAuthors): Future[Int] = {
    libraryDao.addBook(bookJson)
  }



  def deleteBook(bookId: Int): Future[Int] = {
    libraryDao.deleteBook(bookId)
  }
}