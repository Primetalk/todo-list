package controllers

import models.User
import play.api.mvc._
import dao.UserDao

abstract class SecuredController extends Controller {

	private 
	def currentUserOpt(request: RequestHeader): Option[User] = 
		request.session.
			get("userId").
			map (id=>UserDao.getById(id.toLong))	

	/** 
	 *  Chained Action for those methods that require a valid user.
	 */
	def withUser(f: User => EssentialAction): EssentialAction =
		Security.Authenticated(
				currentUserOpt, 
				r => Redirect(routes.Auth.login).flashing("redirect" -> r.uri))(f)
}