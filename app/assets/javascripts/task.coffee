makeTableEditable = ()-> 	
	r = jsRoutes.controllers.Task.update()
	jQuery(".jeditable").editable({
		url        : r.url
		type       : r.method
		mode       : "inline"
		toggle     : "dblclick"
		showbuttons: false
		onblur     : "submit"
	}).prop("title", "Double click to edit...")	
	jQuery(".jeditable-select").editable({
		url        : r.url
		type       : r.method
		#"data-source"      : "{'0':' ','1':'!','-1':'<'}", #"{'0':'&nbsp;','1':'&#x2762','-1':'&#xe094'}",     	
		style      : "color:green"
		mode       : "inline"
		toggle     : "dblclick"
		showbuttons: false
		#style      : "font-family: 'Glyphicons Halflings', Arial"
		cssclass   : 'prioritySelect'
		onblur     : "submit"
	}).prop("title", "Double click to edit...")
	jQuery("input.task-status").click( (eventData) ->
		pk = @getAttribute("data-pk")
		checked = jQuery(@).prop("checked")
		status = if checked then 1 else 0
		r = jsRoutes.controllers.Task.update()
		jQuery.post(
			r.url,
			"pk="+pk+"&name=status&value="+status,
			(data) -> updateTableHtml(data)
			)			
	)
	jQuery(".task-delete").click( (eventData) ->
		pk = @getAttribute("data-pk")
		jsRoutes.controllers.Task.delete(pk).ajax({
			success : (pageWithTable, status, jqXHR) ->
						updateTableHtml(pageWithTable)
			error   : (jqXHR, textStatus, errorThrown) -> alert(jqXHR.responseText)						
						#TODO put in the last table row        
		})
	)
	jQuery("#task-delete-done").click( (eventData) ->
		jsRoutes.controllers.Task.deleteDone().ajax({
			success : (pageWithTable, status, jqXHR) ->
						updateTableHtml(pageWithTable)
			error   : (jqXHR, textStatus, errorThrown) -> alert(jqXHR.responseText)        
		})
	)
	jQuery("#task-add").click( (eventData) ->		
		newTaskUrlEncoded = $(".new-task").serializeArray();
		jsRoutes.controllers.Task.add().ajax({
			data	: newTaskUrlEncoded			
			success : (pageWithTable, status, jqXHR) ->
						updateTableHtml(pageWithTable)
			error   :(jqXHR, textStatus, errorThrown) -> alert(jqXHR.responseText) 
		})
	)
	

updateTableHtml = (pageWithTable) ->
	# location.reload()
	page = jQuery(jQuery.parseHTML(pageWithTable))
	title = page.find("#tableTitle").html()
	document.title = title #+"*"
	jQuery("#titleLink").html(title)
	table = page.find("#tasksTable").html()				
	jQuery("#tasksTable").html(table)
	makeTableEditable()

	
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

