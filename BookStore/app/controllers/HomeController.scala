package controllers

import javax.inject._
import play.api.mvc._

@Singleton
class HomeController @Inject()(cc: MessagesControllerComponents) extends MessagesAbstractController(cc) {

  def index = Action { implicit request =>
    Ok(views.html.index())
  }
}
