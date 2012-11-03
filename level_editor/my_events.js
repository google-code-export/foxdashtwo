function my_events(x, y, width, height, type, i)
{	
	this.id = i;
	this.x = x;
	this.y = y;
	this.width = parseInt(width);
	this.height = parseInt(height);
	this.type = type;
	this.selected = true;
	this.draggable = false;
	this.affected_strings = "";
	
	this.event_name_id = '000x0';
}

my_events.prototype.draw = function()
{
	//draw bounding box
	if(this.selected)
		drawBoxwh(this.x - 5, this.y - 5, this.width + 10, this.height + 10, purpleFill);
	
	drawRectwh(this.x, this.y, this.width, this.height, transparentGreenFill);
	
	if($('#labelcheck').is(':checked'))
	{
		drawTextYFix(this.x, 5 + this.y + this.height, "my_events(" + this.type + "): " + this.event_name_id, whiteFill);
		drawTextYFix(this.x, this.y + this.height - 20, "affects: " + this.affected_strings, blackFill);
	}
}

my_events.prototype.contains = function(v2)
{
	var x = this.x;
	var y = this.y;
	
	var x2 = x + this.width;
	var y2 = y + this.height;
	
	var mouse_y = window.height - v2.y + world_coords.y;
	var mouse_x = v2.x + world_coords.x;

	if(mouse_x >= x && mouse_x <= x2 && mouse_y <= y2 && mouse_y >= y)
		return true;
	
	return false;
}

//somewhat local functions that affect events
function move_event(change_vector)
{
	for(var i = 0; i < events_array.length; i++)
		if(events_array[i].selected)
		{
			events_array[i].x += change_vector.x;
			events_array[i].y += change_vector.y;

			//setup the interface
			setup_events_interface(events_array[i]);
			
			//and exit
			break;
		}	
}

function delete_event()
{
	for(var i = 0; i < events_array.length; i++)
		if(events_array[i].selected)
		{
			remove_from_events_array(events_array[i]);
			break;
		}	
}

function mouse_move_event(click_point, previously_selected)
{
	$('#event_drop.down').val('');
	var something_selected = false;
	
	//previous check first
	if(previously_selected != null && previously_selected.contains(click_point))
	{
		//setup the local stats
		previously_selected.draggable = true;
		previously_selected.selected = true;
		//setup the interface
		setup_events_interface(previously_selected);
		
		something_selected = true;
	}
	
	//select an event		
	if(!something_selected)
	for(var i = 0; i < events_array.length; i++)
		if(events_array[i].contains(click_point))
		{
			//setup the local stats
			events_array[i].draggable = true;
			events_array[i].selected = true;
			//setup the interface
			setup_events_interface(events_array[i]);
			
			something_selected = true;
			
			//exit
			break; // only one
		}
	
	if(!something_selected)
	{
		//add an event to our array.
		var selected_type = $('#event_type_drop_down').val();
		events_array.push(new my_events(world_drag_point.x,
			world_drag_point.y,
			200,
			200,
			selected_type,
			events_array.length));
		
		var id = events_array.length - 1;
		
		//add event to select
		$('#event_drop_down').append($("<option></option>")
		         .attr("value", id)
		         .text(events_array[id].event_name_id + ': ' + id));  
		
		//setup our interface
		setup_events_interface(events_array[id]);
	}	
}

//global functions that affect events
function setup_events_interface(selected_event)
{
	//find selected
	if(selected_event == null)
	for(var i = 0; i < events_array.length; i++)
		if(events_array[i].selected)
		{
			selected_event = events_array[i];
			break;
		}
	
	if(selected_event == null)
		return;
	
	//set all values
	$('#event_drop_down').val(selected_event.id);
	$('#event_type_drop_down').val(selected_event.type);
	$('#event_x').val(selected_event.x);
	$('#event_y').val(selected_event.y);
	$('#event_width').val(selected_event.width);
	$('#event_height').val(selected_event.height);
	$('#event_affects').val(selected_event.affected_strings);
}

function remove_from_events_array(selected_event)
{
	//remove from our array
	var temporary_array = Array();
	for(var i = 0; i < events_array.length; i++)
	{
		if(i < selected_event.id)
			temporary_array[i] = events_array[i];
		else if(i > selected_event.id)
		{
			temporary_array[i - 1] = events_array[i];
			
			//reset id 
			temporary_array[i - 1].id = i - 1;
		}
	}
	
	//set the array
	events_array = temporary_array;
	
	//reset select
	$('#event_drop_down').html('');
	$('#event_drop_down').append($("<option></option>")
	         .attr("value", "")
	         .text("None")); 
	for(var i = 0; i < events_array.length; i++)
	$('#event_drop_down').append($("<option></option>")
	         .attr("value", i)
	         .text(events_array[i].event_name_id +': '+ i)); 
	
}

//and the initialization
function initialize_event_interface()
{
	//handle event selection
	$('#event_drop_down').change(function(){
		for(var i = 0; i < events_array.length; i++)
			events_array[i].selected = false;
		
		if($(this).val() != '')
		{	
			//setup active event
			var e = $(this).val();
			events_array[e].selected = true;
			setup_events_interface(events_array[e]);
		}
	});
	//change name
	$('#event_name_id').change(function(){
		for(var i = 0; i < events_array.length; i++)
			if(events_array[i].selected)
			{
				events_array[i].event_name_id = $(this).val();
				break;
			}
	});
	//changing type
	$('#event_type_drop_down').change(function(){
		for(var i = 0; i < events_array.length; i++)
		if(events_array[i].selected)
		{
			events_array[i].type = $(this).val();
			break;
		}
	});
	//change x and y
	$('#event_x').change(function(){
		for(var i = 0; i < events_array.length; i++)
		if(events_array[i].selected)
		{
			events_array[i].x = parseInt($(this).val());
			break;
		}
	});
	$('#event_y').change(function(){
		for(var i = 0; i < events_array.length; i++)
		if(events_array[i].selected)
		{
			events_array[i].y = parseInt($(this).val());
			break;
		}
	});
	$('#event_width').change(function(){
		for(var i = 0; i < events_array.length; i++)
			if(events_array[i].selected)
			{
				events_array[i].width = parseInt($(this).val());
				break;
			}
	});	
	$('#event_height').change(function(){
		for(var i = 0; i < events_array.length; i++)
			if(events_array[i].selected)
			{
				events_array[i].height = parseInt($(this).val());
				break;
			}
	});	
	$('#event_affects').change(function(){
		for(var i = 0; i < events_array.length; i++)
			if(events_array[i].selected)
			{
				events_array[i].affected_strings = $(this).val();
				break;
			}
	});	
}

function event_mouse_up(click_point)
{
	//remove draggability
	for(var i = 0; i < events_array.length; i++)
		events_array[i].draggable = false;
}

function more_event_stuff()
{
	for(var i = 0; i < events_array.length; i++)
		if(events_array[i].selected)
		{
			//set event coords
			events_array[i].x -= drag_delta.x;
			events_array[i].y -= drag_delta.y;
			
			//set interface
			setup_events_interface(events_array[i]);
			
			//exit
			break;
		}	
}