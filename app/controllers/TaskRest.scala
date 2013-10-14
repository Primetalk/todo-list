package controllers

import play.api.mvc.Action
import dao.TaskDao
import dao.UserDao
import play.api.data.Form
import play.api.data.Forms._
import models.Priority
import models.User
import play.api.mvc.RequestHeader
import models.FieldUpdate
import models.Login
import play.api.mvc.Request
import play.api.mvc.Result
import play.api.libs.json.Json
import play.api.libs.json.JsValue
import play.api.mvc.EssentialAction

object TaskRest extends SecuredController {
	val simpleAuthForm = Form(
		mapping(
				"name" -> nonEmptyText(minLength=1),
				"password" -> nonEmptyText				
				)(Login.apply)(Login.unapply)
			verifying ("Password can not be empty", _.password != "")
			verifying ("Invalid user name or password", UserDao.authenticate(_).isEmpty)
	)
		
//	private
//	def authenticateFromRequest(implicit request:Request[_]):Option[User] = 
//		simpleAuthForm.bindFromRequest.fold(f=> None, login => UserDao.authenticate(login))
	private
	def authenticateFromRequest2(f:User => Result)(implicit request:Request[_]): Result = 
		simpleAuthForm.bindFromRequest.fold(f=> None, login => UserDao.authenticate(login)).
			map(f).getOrElse(BadRequest)
	
	private
	def jsonOk(json:JsValue):Result = 
		Ok(Json.stringify(json)).as("application/json")
		
	def list = 
		Action{ implicit request =>
			authenticateFromRequest2{user =>
				val tasks = UserDao.tasks(user).all.map(_.toJson)
				jsonOk(Json.toJson(tasks ))
			}
		}
		
		
		
	def add = 
		Action{ implicit request =>
			authenticateFromRequest2{user =>
				controllers.Task.taskForm.bindFromRequest.fold(
					formWithErrors => {
						BadRequest(views.html.tasks(UserDao.tasks(user).all, formWithErrors, user))}
					,
					task => {
						UserDao.tasks(user).create(task)
						Ok
					})
			}
		}
		
	def delete(id:Long) =
		Action { implicit request ⇒
			authenticateFromRequest2{user =>
				UserDao.tasks(user).delete(id)
				Ok
			}
		}
	def deleteDone = 
		Action { implicit request ⇒
			authenticateFromRequest2{user =>
				UserDao.tasks(user).deleteDone()
				Ok
			}
		}
	
	
	// TODO validation
	val fieldUpdate = Form(
			mapping(
				"pk" -> number,
				"name" -> text,
				"value" -> text
			)(FieldUpdate.apply)(FieldUpdate.unapply)
			)
	
	def update = 
		Action { implicit request ⇒
			authenticateFromRequest2{user =>
				val form = fieldUpdate.bindFromRequest
				form.fold(
						formWithErrors => BadRequest("Invalid field value"), 
						fu =>
							UserDao.tasks(user).updateField(fu))			
				Ok
			}
		}
		
}