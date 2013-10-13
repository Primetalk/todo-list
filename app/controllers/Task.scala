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

object Task extends SecuredController {
	val taskForm = Form(
		mapping(
			"id" -> number,
			"status" -> number,
			"priority" -> number,
			"text" -> nonEmptyText
		)((id: Int, status: Int, priority:Int, text: String) ⇒ models.Task(id, status, Priority(priority), text)) {
				case task: models.Task ⇒ Some((task.id.toInt, task.status: Int, task.priority.level, task.text))
				case _ ⇒ None
			}
	)
	
	private 
	def tasksOk(user:User)(implicit request:RequestHeader) = 
		Ok(views.html.tasks(UserDao.tasks(user).all, taskForm, user))
	
	def list = withUser(user =>
		Action{ implicit request =>
			tasksOk(user)
		})
		
		
	def add = withUser(user =>
		Action{ implicit request =>
			taskForm.bindFromRequest.fold(
				formWithErrors => {
					println(formWithErrors)
					BadRequest(views.html.tasks(UserDao.tasks(user).all, formWithErrors, user))}
				,
				task => {
					println(task)
					UserDao.tasks(user).create(task)
					val redirect =
						request.flash.get("redirect").
							map(Redirect(_)).
							getOrElse(Redirect(routes.Application.index))
					tasksOk(user)
				})
			
		})
		
	def delete(id:Long) = withUser(user ⇒
		Action { implicit request ⇒
			println("delete:"+id)
			UserDao.tasks(user).delete(id)
			tasksOk(user)
		}
	)
	
	
	// TODO validation
	val fieldUpdate = Form(
			mapping(
				"pk" -> number,
				"name" -> text,
				"value" -> text
			)(FieldUpdate.apply)(FieldUpdate.unapply)
			)
	
	def update = withUser(user =>
		Action { implicit request ⇒
			val form = fieldUpdate.bindFromRequest
			form.fold(
					formWithErrors => BadRequest("Invalid field value"), 
					fu =>
						UserDao.tasks(user).updateField(fu))			
			tasksOk(user)
		}
	)
		
}