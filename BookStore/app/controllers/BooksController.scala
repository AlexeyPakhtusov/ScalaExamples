package controllers

import javax.inject._

import models.Book
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.mvc.{Action, AnyContent, MessagesAbstractController, MessagesControllerComponents}
import services.BooksService

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class BooksController @Inject()(cc: MessagesControllerComponents, booksService: BooksService)
                               (implicit context: ExecutionContext) extends MessagesAbstractController(cc) {

  private val bookForm = Form {
    mapping(
      "id" -> ignored(-1L),
      "title" -> text.verifying(nonEmpty, minLength(1), maxLength(255)),
      "price" -> number.verifying(min(1), max(Int.MaxValue)),
      "author" -> text.verifying(nonEmpty, minLength(1), maxLength(255))
    )(Book.apply)(Book.unapply)
  }

  def index(filter: String): Action[AnyContent] = Action.async { implicit request =>
    booksService.getBooks(filter).map(books => Ok(views.html.books.index(books, filter)))
  }

  def create: Action[AnyContent] = Action.async { implicit request =>
    Future(Ok(views.html.books.create(bookForm)))
  }

  def save: Action[AnyContent] = Action.async { implicit request =>
    bookForm.bindFromRequest.fold(
      formWithErrors => {
        Future(BadRequest(views.html.books.create(formWithErrors)))
      },
      book => {
        booksService.addBook(book).map {
          case 0 => InternalServerError("Failed to add new book")
          case _ => Redirect(routes.BooksController.index())
            .flashing("success" -> s"Book ${book.title} has been added")
        }
      }
    )
  }

  def edit(id: Long): Action[AnyContent] = Action.async { implicit request =>
    booksService.getBookById(id).map {
      case None => NotFound("Book not found")
      case Some(book) => Ok(views.html.books.edit(id, bookForm.fill(book)))
    }
  }

  def update(id: Long): Action[AnyContent] = Action.async { implicit request =>
    bookForm.bindFromRequest.fold(
      formWithErrors => {
        Future(BadRequest(views.html.books.edit(id, formWithErrors)))
      },
      book => {
        booksService.updateBook(id, book).map {
          case 0 => InternalServerError("Failed to update book")
          case _ => Redirect(routes.BooksController.index())
            .flashing("success" -> s"Book ${book.title} has been updated")
        }
      }
    )
  }

  def destroy(id: Long): Action[AnyContent] = Action.async {
    booksService.removeBook(id).map {
      case 0 => NotFound("Book not found")
      case _ => Ok flashing("success" -> "Book has been deleted")
    }
  }

  def show(id: Long): Action[AnyContent] = Action.async { implicit request =>
    booksService.getBookById(id).map {
      case None => NotFound("Book not found")
      case Some(book) => Ok(views.html.books.show(book))
    }
  }
}
