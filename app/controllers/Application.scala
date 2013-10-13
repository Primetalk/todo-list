package controllers

import play.api.mvc.{ Action, Controller }
import play.api._
import play.api.data._
import play.api.data.Forms._
import play.api.mvc._

import models._
import views._

object Application extends SecuredController {
	
	def index = withUser(user =>
		Action {
//			Ok(views.html.index.render("Hello Play Framework"))
			Redirect(routes.Task.list)
		})
		
	def notFound(p:String) = withUser(user =>
		Action { implicit request =>
			NotFound("Not found "+request.path)
		})
		
	def javascriptRoutes = Action { implicit request =>
	  import routes.javascript._
	  Ok(
	    Routes.javascriptRouter("jsRoutes")(
	      Task.delete,
	      Task.update
	    )
	  ).as("text/javascript")
	}
		
}