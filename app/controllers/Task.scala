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
import play.api.i18n.Messages

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
					val errors = formWithErrors.errors.map(err => Messages(err.message, err.args: _*)).mkString("\n")
//					println(errors)					
					BadRequest(errors)
				},
				task => {
					println(task)
					UserDao.tasks(user).create(task)
					request.flash.get("redirect").
						map(Redirect(_)).
						getOrElse(Redirect(routes.Application.index))
					tasksOk(user)
				})
			
		})
		
	def delete(id:Long) = withUser(user ⇒
		Action { implicit request ⇒
			UserDao.tasks(user).delete(id)
			tasksOk(user)
		}
	)
	def deleteDone = withUser(user ⇒
		Action { implicit request ⇒
			println("deleteDone"+request.body)
			UserDao.tasks(user).deleteDone()
			tasksOk(user)
		}
	)
	
	val fieldUpdate = Form(
			mapping(
				"pk" -> number,
				"name" -> text,
				"value" -> nonEmptyText
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