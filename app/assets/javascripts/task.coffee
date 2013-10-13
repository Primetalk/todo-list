
window.deleteTask = (id) ->
	r = jsRoutes.controllers.Task.delete(id)
	jQuery.ajax({
		url:r.url
		type:r.method
		success: (pageWithTable, status, jqXHR) ->
			# location.reload()
			page = jQuery(jQuery.parseHTML(pageWithTable))
			title = page.find("#tableTitle").html()
			document.title = title+"*"
			table = page.find("#tasksTable").html()				
			jQuery("#tasksTable").html(table)				
		error: (jqXHR, textStatus, errorThrown) ->
			alert("error")
			alert("error:"+errorThrown)
			alert("status:"+textStatus)        
	})
