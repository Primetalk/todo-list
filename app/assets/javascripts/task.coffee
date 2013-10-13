makeTableEditable = ()-> 	
	r = jsRoutes.controllers.Task.update()
	jQuery(".jeditable").editable({
		url  : r.url
		type : r.method
		mode : "inline"
		toggle: "dblclick"
		showbuttons: false
		onblur: "submit"
	}).prop("title", "Double click to edit...")

updateTableHtml = (pageWithTable) ->
	# location.reload()
	page = jQuery(jQuery.parseHTML(pageWithTable))
	title = page.find("#tableTitle").html()
	document.title = title+"*"
	table = page.find("#tasksTable").html()				
	jQuery("#tasksTable").html(table)
	makeTableEditable			
	
window.deleteTask = (id) ->
	r = jsRoutes.controllers.Task.delete(id)
	jQuery.ajax({
		url:r.url
		type:r.method
		success: (pageWithTable, status, jqXHR) ->
			updateTableHtml(pageWithTable)
		error: (jqXHR, textStatus, errorThrown) ->
			alert("error")
			alert("error:"+errorThrown)
			alert("status:"+textStatus)        
	})

jQuery(document).ready(() ->
	makeTableEditable()
	)

