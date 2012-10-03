function interfaceload()
{
	//handle tabs
	var tabs = $('.taboption');
	tabs.click(function(){
		//set some css
		tabs.css('background-color', '#ddd');
		$(this).css('background-color', 'white');
		
		//hide everything
		$('.tab_hideable').css('display', 'none');
		
		current_tab = $(this).attr('name');
		
		//display what we care about
		$('#tab_' + current_tab).css('display', 'inline-block');
	});
	
	//handle click/navigation buttons
	var intervalID;
	$('.control_scheme_button').mousedown(function() {
		var e = new Object;
		e.keyCode = $(this).attr('e');
		intervalId = setInterval(function(){keypressed(e);}, 20);
	}).mouseup(function() {
	  clearInterval(intervalId);
	});
	
	//handle object selection
	$('#object_drop_down').change(function(){
		for(var i = 0; i < objects_array.length; i++)
			objects_array[i].selected = false;
		
		if($(this).val() != '')
		{	
			//setup active object
			var e = $(this).val();
			objects_array[e].selected = true;
			setup_objects_interface(objects_array[e]);
		}
	});
	//changing type
	$('#type_drop_down').change(function(){
		for(var i = 0; i < objects_array.length; i++)
		if(objects_array[i].selected)
		{
			objects_array[i].width = parseInt($("#type_drop_down option:selected").attr('my_width'));
			objects_array[i].height = parseInt($("#type_drop_down option:selected").attr('my_height'));
			objects_array[i].type = $("#type_drop_down").val();
			break;
		}
	});
	//change x and y
	$('#object_x').change(function(){
		for(var i = 0; i < objects_array.length; i++)
		if(objects_array[i].selected)
			objects_array[i].x = parseInt($(this).val());
	});
	$('#object_y').change(function(){
		for(var i = 0; i < objects_array.length; i++)
		if(objects_array[i].selected)
			objects_array[i].y = parseInt($(this).val());
	});
	$('#object_z').change(function(){
		for(var i = 0; i < objects_array.length; i++)
			if(objects_array[i].selected)
				objects_array[i].z_plane = parseInt($(this).val());		
	});
}
