package controllers

import dao.UserDao
import models.Login
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.Action
import models.Signup
import play.api.i18n.Messages

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
				formWithErrors =>{
//					val errors = formWithErrors.errors.map(err => Messages(err.message, err.args: _*)).mkString("\n")
//					println(formWithErrors)
//					println(formWithErrors.globalError)
//					formWithErrors.errors.map(e=>e.)
					BadRequest(views.html.login(formWithErrors))//.flashing("error"-> errors)
				},
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
			formWithErrors =>{
//				val errors = formWithErrors.errors.map(err => Messages(err.message, err.args: _*)).mkString("\n")
				BadRequest(views.html.signup(formWithErrors))//.flashing("error"-> errors)
			},
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