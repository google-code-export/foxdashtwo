function my_objects(x, y, width, height, type, i)
{	
	this.id = i;
	this.x = x;
	this.y = y;
	this.z_plane = 5;
	this.width = parseInt(width);
	this.height = parseInt(height);
	this.type = type;
	this.selected = true;
	this.draggable = false;
	
	this.object_name_id = '000x0';
}

my_objects.prototype.draw = function()
{
	//draw bounding box
	if(this.selected)
		drawBoxwh(this.x - 5, this.y - 5, this.width + 10, this.height + 10, purpleFill);
	
	drawRectwh(this.x, this.y, this.width, this.height, lightblueFill);
	
	if($('#labelcheck').is(':checked'))
	{
		drawTextYFix(this.x, 5 + this.y + this.height, "my_objects(" + this.type + "): " + this.object_name_id, whiteFill);
		drawTextYFix(this.x, this.y + this.height - 20, "z: " + this.z_plane, blackFill);
	}
}

my_objects.prototype.contains = function(v2)
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

//somewhat local functions that affect objects
function move_object(change_vector)
{
	for(var i = 0; i < objects_array.length; i++)
		if(objects_array[i].selected)
		{
			objects_array[i].x += change_vector.x;
			objects_array[i].y += change_vector.y;

			//setup the interface
			setup_objects_interface(objects_array[i]);
			
			//and exit
			break;
		}	
}

function delete_object()
{
	for(var i = 0; i < objects_array.length; i++)
		if(objects_array[i].selected)
		{
			remove_from_objects_array(objects_array[i]);
			break;
		}	
}

function mouse_move_object(click_point, previously_selected)
{
	$('#object_drop.down').val('');
	var something_selected = false;
	
	//previous check first
	if(previously_selected != null && previously_selected.contains(click_point))
	{
		//setup the local stats
		previously_selected.draggable = true;
		previously_selected.selected = true;
		//setup the interface
		setup_objects_interface(previously_selected);
		
		something_selected = true;
	}
	
	//select an object		
	if(!something_selected)
	for(var i = 0; i < objects_array.length; i++)
		if(objects_array[i].contains(click_point))
		{
			//setup the local stats
			objects_array[i].draggable = true;
			objects_array[i].selected = true;
			//setup the interface
			setup_objects_interface(objects_array[i]);
			
			something_selected = true;
			
			//exit
			break; // only one
		}
	
	if(!something_selected)
	{
		//add an object to our array.
		var selected_type = $('#type_drop_down').val();
		objects_array.push(new my_objects(world_drag_point.x,
			world_drag_point.y,
			$('option[value="' + selected_type + '"]').attr('my_width'),
			$('option[value="' + selected_type + '"]').attr('my_height'),
			selected_type,
			objects_array.length));
		
		var id = objects_array.length - 1;
		
		//add object to select
		$('#object_drop_down').append($("<option></option>")
		         .attr("value", id)
		         .text(objects_array[id].object_name_id + ': ' + id));  
		
		//setup our interface
		setup_objects_interface(objects_array[id]);
	}	
}

//global functions that affect objects
function setup_objects_interface(selected_object)
{
	//find selected
	if(selected_object == null)
	for(var i = 0; i < objects_array.length; i++)
		if(objects_array[i].selected)
		{
			selected_object = objects_array[i];
			break;
		}
	
	if(selected_object == null)
		return;
	
	//set all values
	$('#object_drop_down').val(selected_object.id);
	$('#type_drop_down').val(selected_object.type);
	$('#object_x').val(selected_object.x);
	$('#object_y').val(selected_object.y);
	$('#object_z').val(selected_object.z_plane);
}

function remove_from_objects_array(selected_object)
{
	//remove from our array
	var temporary_array = Array();
	for(var i = 0; i < objects_array.length; i++)
	{
		if(i < selected_object.id)
			temporary_array[i] = objects_array[i];
		else if(i > selected_object.id)
		{
			temporary_array[i - 1] = objects_array[i];
			
			//reset id 
			temporary_array[i - 1].id = i - 1;
		}
	}
	
	//set the array
	objects_array = temporary_array;
	
	//reset select
	$('#object_drop_down').html('');
	$('#object_drop_down').append($("<option></option>")
	         .attr("value", "")
	         .text("None")); 
	for(var i = 0; i < objects_array.length; i++)
	$('#object_drop_down').append($("<option></option>")
	         .attr("value", i)
	         .text(objects_array[i].object_name_id +': '+ i)); 
	
}

//and the initialization
function initialize_object_interface()
{
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
	//change name
	$('#object_name_id').change(function(){
		for(var i = 0; i < objects_array.length; i++)
			if(objects_array[i].selected)
			{
				objects_array[i].object_name_id = $(this).val();
				break;
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
		{
			objects_array[i].x = parseInt($(this).val());
			break;
		}
	});
	$('#object_y').change(function(){
		for(var i = 0; i < objects_array.length; i++)
		if(objects_array[i].selected)
		{
			objects_array[i].y = parseInt($(this).val());
			break;
		}
	});
	$('#object_z').change(function(){
		for(var i = 0; i < objects_array.length; i++)
			if(objects_array[i].selected)
			{
				objects_array[i].z_plane = parseInt($(this).val());
				break;
			}
	});	
}

function object_mouse_up(click_point)
{
	//remove draggability
	for(var i = 0; i < objects_array.length; i++)
		objects_array[i].draggable = false;
}

function more_object_stuff()
{
	for(var i = 0; i < objects_array.length; i++)
		if(objects_array[i].selected)
		{
			//set object coords
			objects_array[i].x -= drag_delta.x;
			objects_array[i].y -= drag_delta.y;
			
			//set interface
			setup_objects_interface(objects_array[i]);
			
			//exit
			break;
		}	
}