package controllers

import models._
import services.BookService
import javax.inject._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.json._

@Singleton
class HomeController @Inject()(
  bookService: BookService
)(implicit ec: ExecutionContext) extends InjectedController {

  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

  def addBook() = Action(parse.json).async { request =>
    request.body.validate[BookAndAuthors].fold(
      errors => Future.successful(BadRequest(Json.obj("status" -> "Error", "message" -> JsError.toJson(errors)))),
      { bookJSON =>
        bookService.addBook(bookJSON).map { newId =>
          Ok(Json.obj("message" -> ("Book save with id '" + newId)))
        }.recover { case _ =>
          BadRequest(Json.toJson("status" -> "error", "message" -> "DB Error"))
        }
      }
    )
  }

  def getAllBooks() = Action.async {
    bookService.getAllBooks.map { seqOfClassBook =>
      Ok(Json.toJson(seqOfClassBook))
    }.recover { case _ =>
      BadRequest(Json.toJson("status" -> "error", "message" -> "DB Error"))
    }
  }

}
