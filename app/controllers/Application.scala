package controllers

import play.api.mvc.{ Action, Controller }

object Application extends SecuredController {
	
	def index = withUser(user =>
		Action {
			Ok(views.html.index.render("Hello Play Framework"))
		})
		
	def notFound(p:String) = withUser(user =>
		Action { implicit request =>
			NotFound("Not found "+request.path)
		})
		
}