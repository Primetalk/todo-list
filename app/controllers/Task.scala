package controllers

import play.api.mvc.Action
import dao.TaskDao
import dao.UserDao
import play.api.data.Form
import play.api.data.Forms._
import models.Priority

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
	
	
	def list = withUser(user =>
		Action{ implicit request =>
			Ok(views.html.tasks(UserDao.tasks(user).all, taskForm, user))
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
					Ok(views.html.tasks(UserDao.tasks(user).all, taskForm, user))
				})
			
		})
		
		
}