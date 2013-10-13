import play.api._
import models._
import anorm._
import dao.UserDao

object Global extends GlobalSettings {
  
  override def onStart(app: Application) {
    InitialData.insert()
  }
  
}

/**
* Initial set of data to be imported
*/
object InitialData {
  
  def insert() = {
    
    if(UserDao.count() == 0) {      
      val users = Seq(        
        Login("user", "123"),
        Login("user2", "123")
      ).map(UserDao.create)
      val Seq(user1, user2)= users
      val tasks1 = UserDao.tasks(user1) 
      Seq(
      		new Task(0, 0, Priority.High, "Do first"),
      		new Task(0, 0, Priority.Normal, "Do next")
      		).foreach(
    		  tasks1.create
    		  )
      val tasks2 = UserDao.tasks(user2) 
      Seq(
      		new Task(0, 0, Priority.High, "Do 1 (for user2)"),
      		new Task(0, 0, Priority.Normal, "Do 2 (for user2)")
      		).foreach(tasks2.create)
    }
    
  }
  
}