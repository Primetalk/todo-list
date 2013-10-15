import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import play.api.test.Helpers._

@RunWith(classOf[JUnitRunner])
class RestSpec extends Specification {

	private val credentials = List("name" -> "user", "password" -> "123")
	
	"REST" should {

		"return tasks for user" in new WithApplication {
			val taskList = route(FakeRequest(POST, "/rest/tasks")
					.withFormUrlEncodedBody(credentials :_*))
					.get

			status(taskList) must equalTo(OK)
			contentType(taskList) must beSome.which(_ == "application/json")
			(contentAsJson(taskList)\\"id") must haveSize(2)
		}
		
		"delete a task" in new WithApplication {
			val deleteTask = route(FakeRequest(DELETE, "/rest/tasks/1")
					.withFormUrlEncodedBody(credentials :_*))
					.get
			status(deleteTask) must equalTo(OK)
			
			val taskList = route(FakeRequest(POST, "/rest/tasks")
					.withFormUrlEncodedBody(credentials :_*))
					.get

			status(taskList) must equalTo(OK)
			contentType(taskList) must beSome.which(_ == "application/json")
			(contentAsJson(taskList)\\"id") must haveSize(1)
		}
		
		"add a task" in new WithApplication {
			val addTask = route(FakeRequest(POST, "/rest/tasks/add")
					.withFormUrlEncodedBody((
							("priority" -> "1") 
							:: ("text" -> "Created from REST")
							:: credentials):_*))
					.get
			status(addTask) must equalTo(OK)
			
			val taskList = route(FakeRequest(POST, "/rest/tasks")
					.withFormUrlEncodedBody(credentials :_*))
					.get

			status(taskList) must equalTo(OK)
			contentType(taskList) must beSome.which(_ == "application/json")
			(contentAsJson(taskList)\\"id") must haveSize(3)
			(contentAsJson(taskList)\\"text").map(_.as[String]) must contain("Created from REST")
		}
		
		"not add an empty task" in new WithApplication {
			val addTask = route(FakeRequest(POST, "/rest/tasks/add")
					.withFormUrlEncodedBody((
							("priority" -> "1") 
							:: ("text" -> "")
							:: credentials):_*))
					.get
			status(addTask) must not equalTo(OK)
		}
	}
	
}