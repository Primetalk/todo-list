package controllers

import dao.UserDao
import models.Login
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.Action
import models.Signup

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
	
	def logout = withUser(user => Action {
		Redirect(routes.Application.index).withNewSession.
			flashing("success" -> "You've been logged out")
	})

	
	
	val signupForm = Form(
		mapping(
			"name" -> nonEmptyText,
			"password" -> text,
			"passwordConfirmation" -> text)(Signup.apply)(Signup.unapply)
			verifying ("Password can not be empty", _.password != "")
			verifying ("Passwords do not match", _.isPasswordMatch)
			verifying ("User name has already been used", s=>UserDao.isUsernameAvailable(s.name))
		)
	def signUp = Action { implicit request =>
		Ok(views.html.signup(signupForm))
	}
	def signUpPost = Action { implicit request =>
		signupForm.bindFromRequest.fold(
			formWithErrors => 
				BadRequest(views.html.signup(formWithErrors)),
			signup => {
				val user = UserDao.create(signup.toLogin)
				val redirect =
					request.flash.get("redirect").
						map(Redirect(_)).
						getOrElse(Redirect(routes.Application.index))
				redirect.withSession("userId" -> user.id.toString)
			})
	}
	
}