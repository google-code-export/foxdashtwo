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
		e.override = true;
		intervalId = setInterval(function(){keypressed(e);}, 20);
	}).mouseup(function() {
	  clearInterval(intervalId);
	});
	
	//setup objects interface
	initialize_object_interface();
	initialize_light_interface();
}
