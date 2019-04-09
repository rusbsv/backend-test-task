package controllers

import models._
import services.LibraryService
import javax.inject._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.json._

@Singleton
class HomeController @Inject()(
  libraryService: LibraryService
)(implicit ec: ExecutionContext) extends InjectedController {

  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(templates.html.index())
  }

  def getBooksList() = Action.async {
    libraryService.getBooksList.map {
      seqOfClassBook =>
        Ok(Json.toJson(seqOfClassBook))
    }.recover { case _ =>
      BadRequest(Json.toJson("status" -> "error", "message" -> "DB Error"))
    }
  }

  def getAuthorsList() = Action.async {
    libraryService.getAuthorsList.map {
      seqOfClassAuthor =>
        Ok(Json.toJson(seqOfClassAuthor))
    }.recover { case _ =>
        BadRequest(Json.toJson("status" -> "error", "message" -> "DB Error"))
    }
  }

  def addBook() = Action(parse.json).async { request =>
    request.body.validate[BookYearAuthors].fold(
      errors => Future.successful(BadRequest(Json.obj("status" -> "Error", "message" -> JsError.toJson(errors)))),
      { bookJSON =>
        libraryService.addBook(bookJSON).map { newId =>
          Ok(Json.obj("message" -> ("Book save with id '" + newId)))
        }.recover { case _ =>
          BadRequest(Json.toJson("status" -> "error", "message" -> "DB Error"))
        }
      }
    )
  }



  def deleteBook(bookId: Int) = Action.async {
    libraryService.deleteBook(bookId).map {
      countDel => Ok(Json.obj("message" -> (countDel + " book deleted")))
    }
  }

}
