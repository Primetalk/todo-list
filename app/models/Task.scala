package models

sealed trait TaskStatus {
	def toggle:TaskStatus 
}
case object ToBeDone extends TaskStatus {
	def toggle:TaskStatus = Completed  
}
case object Completed extends TaskStatus {
	def toggle:TaskStatus = ToBeDone  
}

object TaskStatus {
	val statuses = Array(ToBeDone, Completed)
	implicit def intToStatus(statusId: Int) = statuses(statusId)
	implicit def statusToInt(status:TaskStatus): Int = statuses.indexOf(status)
}

case class Priority(level:Int) {
	def toLabel = 
		Priority.prioritiesLabels(level)
} 

object Priority {
	val Low     = Priority(-1)
	val Normal  = Priority(0)
	val High    = Priority(1)
	val priorities = Array(Low, Normal, High)
	val Default = Normal
	
	private val prioritiesLabels = Map(-1 -> "Low", 0 -> "Normal", 1 -> "High")
}

case class Task (id:Long, status:TaskStatus, priority:Priority, text:String)
