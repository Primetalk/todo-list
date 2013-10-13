package controllers

import dao.UserDao
import models.Login
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.data.Forms.nonEmptyText
import play.api.mvc.Action

/**
 * Authentication controller. Manages:
 *  - sing up.
 *  - login
 *  - logout
 */

object Auth extends SecuredController {
	
	val loginForm = Form(
		mapping(
			"name" -> nonEmptyText,
			"password" -> nonEmptyText)(Login.apply)(Login.unapply))
			
	def login = Action { implicit request =>
		Ok(views.html.login(loginForm)).flashing("success"->"Default user - 'user', with password - '123'")
	}
	
	def authenticate =
		Action { implicit request =>
			loginForm.bindFromRequest.fold(
				formWithErrors =>
					BadRequest(views.html.login(formWithErrors)).flashing(request.flash),
				login => {
					val redirect =
						request.flash.get("redirect").
							map(Redirect(_)).
							getOrElse(Redirect(routes.Application.index))
					redirect.withSession("userId" -> UserDao.authenticate(login).get.id.toString)
				})
		}
	
	def signUp = Action { implicit request =>
		Ok
	}
	def signUpPost = Action { implicit request =>
		Ok
	}
	def logout = withUser(user => Action {
		Redirect(routes.Application.index).withNewSession.
			flashing("success" -> "You've been logged out")
	})

}