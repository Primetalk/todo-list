package dao

import play.api.data._
import play.api.data.Forms._
import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import models._

object UserDao {

	val user = {
		get[Long]("id") ~
			get[String]("name") ~
			get[String]("password_digest") map {
				case id ~ name ~ passwordDigest => User(id, name, passwordDigest)
			}
	}

	val id = get[Long]("id")

	val countParser = get[Long]("count")

	def all(): List[User] =
		DB.withConnection { implicit c =>
			SQL("SELECT * FROM users").as(user *)
		}

	def getById(id: Long): Option[User] =
		DB.withConnection { implicit c =>
			SQL("SELECT * FROM users WHERE id = {id}").on(
				'id -> id).
				as(user.singleOpt)
		}

	def count(): Long =
		DB.withConnection { implicit c =>
			SQL("SELECT count(*) count FROM users ").
				as(countParser.single)			
		}
	def isUsernameAvailable(name: String): Boolean =
		DB.withConnection { implicit c =>
			val cnt = SQL("SELECT count(*) count FROM users WHERE name = {name}").on(
				'name -> name).
				as(countParser.single)
			cnt == 0
		}

	def create(login: Login) =
		DB.withConnection { implicit c =>
			val id =
				SQL("SELECT nextval('user_id_seq') id FROM dual").
					as(UserDao.id.single)
			SQL("INSERT INTO users (id, name, password_digest) values ({id}, {name}, {passwordDigest})").on(
				'id -> id,
				'name -> login.name,
				'passwordDigest -> login.passwordDigest).
				executeUpdate()
			getById(id).get
		}

	def authenticate(login: Login): Option[User] =
		DB.withConnection { implicit connection =>
			SQL("""
		 			|SELECT * FROM users 
		 			|WHERE name = {name} 
		 			|  AND password_digest = {passwordDigest}
		 			|""".stripMargin('|')).on(
				'name -> login.name,
				'passwordDigest -> login.passwordDigest).
				as(user.singleOpt)
		}

	def delete(id: Long) {
		DB.withConnection { implicit c =>
			SQL("DELETE FROM users WHERE id = {id}").on(
				'id -> id).
				executeUpdate()
		}
	}
	
	def tasks(user:User) = 
		new TaskDao(user.id)
}