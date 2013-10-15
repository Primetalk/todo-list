Todo list management application
================================

Todo list allows to maintain a flat list of tasks that have priorities and text description.

The application is deployed at (http://project-arseniy-zhizhelev.primetalk.eu.cloudbees.net/)

There are two users initially:
- user with password 123
- user2 with password 123

A user can register and log in.

After logging in the user sees todo list displayed and can manipulate the list (add/remove/modify entries).
All manipulations are performed with ajax exclusively.

There are 3 levels of priority - Low (downward arrow), Normal, High (exclamation sign).

The REST API is also provided separately.

 POST	/rest/tasks						controllers.TaskRest.list
POST	/rest/tasks/add					controllers.TaskRest.add
DELETE	/rest/tasks/:id					controllers.TaskRest.delete(id:Long)
DELETE	/rest/tasks-done				controllers.TaskRest.deleteDone
POST	/rest/tasks/update				controllers.TaskRest.update

When using REST it is necessary to provide user 'name' and 'password' as POST URL-encoded form. 