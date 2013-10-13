package models

case class User(id: Long, name: String, passwordDigest: String)

case class Login(name: String, password: String) {
	lazy val passwordDigest = Digest.md5base64(password)
}

case class Signup(name: String, password: String, passwordConfirmation: String) {
	lazy val passwordDigest = Digest.md5base64(password)

	def isPasswordMatch = password == passwordConfirmation
	def toLogin = Login(name, password)
}

object Digest {
	private val md5instance = java.security.MessageDigest.getInstance("MD5")

	private val base64instance = new sun.misc.BASE64Encoder()

	def md5base64(s: String) =
		base64instance.encode(md5instance.digest(s.getBytes))

	implicit class DigestedString(val s: String) extends AnyVal {
		def digest = md5base64(s)
	}

}

