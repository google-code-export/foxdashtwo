function my_lights(x, y, type, i)
{	
	this.id = i;
	this.x = x;
	this.y = y;
	this.type = type;
	
	this.height = 100; //equal to this.width in all cases
	this.color = 'rgba(100,100,100,1)';
	this.throw_length = 100;
	this.closewidth = 10;
	this.farwidth = 100;
	this.degree = 0;
	this.bloom = false;
	this.active = true;
	
	this.selected = true;
	this.draggable = false;
}

my_lights.prototype.draw = function()
{
	if(this.type == 'ambient')
		var this_height_thing = this.height;
	else
		var this_height_thing = this.throw_length * 2;
	
	//draw bounding box
	if(this.selected)
		drawBoxwh(this.x - 5, this.y - 5, this_height_thing + 10, this_height_thing + 10, purpleFill);
	
	var statement = "A";
	if(this.type == 'ambient')
	{
		drawRectwh(this.x, this.y, this.height, this.height, this.color);	
	}
	else if(this.type == 'point')
	{
		statement = "P";
		var half_width = this.throw_length;
		drawCircle(this.x + half_width, this.y + half_width, half_width, this.color);
	}
	else if(this.type == 'spot')
	{
		statement = "S";
		drawLamp(this.x, this.y, this.throw_length, this.degree, this.closewidth, this.farwidth, this.color);
	}
	
	var text_width = getTextWidth(statement);
	drawTextYFix(this.x + (this_height_thing / 2.0) - (text_width / 2.0), this.y + (this_height_thing / 2.0) - 7.5, statement, blackFill);
	
	if($('#labelcheck').is(':checked'))
		drawTextYFix(this.x, 5 + this.y + this_height_thing, "my_lights(" + this.type + "): " + this.id, lightblueFill);
}

my_lights.prototype.contains = function(v2)
{
	var x = this.x;
	var y = this.y;
	
	if(this.type == 'ambient')
	{
		var x2 = x + this.height;
		var y2 = y + this.height;
	}
	else
	{
		var x2 = x + this.throw_length * 2;
		var y2 = y + this.throw_length * 2;
	}
	
	var mouse_y = window.height - v2.y + world_coords.y;
	var mouse_x = v2.x + world_coords.x;

	if(mouse_x >= x && mouse_x <= x2 && mouse_y <= y2 && mouse_y >= y)
		return true;
	
	return false;
}

//somewhat local functions that affect lights
function move_light(change_vector)
{
	for(var i = 0; i < lights_array.length; i++)
		if(lights_array[i].selected)
		{
			lights_array[i].x += change_vector.x;
			lights_array[i].y += change_vector.y;

			//setup the interface
			setup_lights_interface(lights_array[i]);
			
			//and exit
			break;
		}	
}

function delete_light()
{
	for(var i = 0; i < lights_array.length; i++)
		if(lights_array[i].selected)
		{
			remove_from_lights_array(lights_array[i]);
			break;
		}	
}

function mouse_move_light(click_point)
{
	$('#light_drop.down').val('');
	
	var something_selected = false;
	
	//select an light		
	for(var i = 0; i < lights_array.length; i++)
		if(lights_array[i].contains(click_point))
		{
			//setup the local stats
			lights_array[i].draggable = true;
			lights_array[i].selected = true;
			something_selected = true;
			
			//setup the interface
			setup_lights_interface(lights_array[i]);
			
			//exit
			break; // only one
		}
	
	if(!something_selected)
	{
		//add an light to our array.
		var selected_type = $('#light_type_drop_down').val();
		lights_array.push(new my_lights(world_drag_point.x,
			world_drag_point.y,
			selected_type,
			lights_array.length));
		
		var id = lights_array.length - 1;
		
		//add light to select
		$('#light_drop_down').append($("<option></option>")
		         .attr("value", id)
		         .text(id)); 
		
		//setup our interface
		setup_lights_interface(lights_array[id]);
	}	
}

//global functions that affect lights
function remove_from_lights_array(selected_light)
{
	//remove from our array
	var temporary_array = Array();
	for(var i = 0; i < lights_array.length; i++)
	{
		if(i < selected_light.id)
			temporary_array[i] = lights_array[i];
		else if(i > selected_light.id)
		{
			temporary_array[i - 1] = lights_array[i];
			
			//reset id 
			temporary_array[i - 1].id = i - 1;
		}
	}
	
	//set the array
	lights_array = temporary_array;
	
	//reset select
	$('#light_drop_down').html('');
	$('#light_drop_down').append($("<option></option>")
	         .attr("value", "")
	         .text("None")); 
	for(var i = 0; i < lights_array.length; i++)
	$('#light_drop_down').append($("<option></option>")
	         .attr("value", i)
	         .text(i)); 
	
}

function setup_lights_interface(selected_light)
{
	//find selected
	if(selected_light == null)
	for(var i = 0; i < lights_array.length; i++)
		if(lights_array[i].selected)
		{
			selected_light = lights_array[i];
			break;
		}
	
	if(selected_light == null)
		return;
	
	//set all values
	$('#light_drop_down').val(selected_light.id);
	$('#light_type_drop_down').val(selected_light.type);
	$('#light_x').val(selected_light.x);
	$('#light_y').val(selected_light.y);
	
	$('#light_color').val(selected_light.color);
	$('#light_throw').val(selected_light.throw_length);
	$('#light_closewidth').val(selected_light.closewidth);
	$('#light_farwidth').val(selected_light.farwidth);
	$('#light_degree').val(selected_light.degree);
	
	if(selected_light.bloom)
		$('#light_bloom').attr('checked', true);
	else
		$('#light_bloom').attr('checked', false);
		
	if(selected_light.active)
		$('#light_active').attr('checked', true);
	else
		$('#light_active').attr('checked', false);
}

//and the initialization
//horribly inefficient
function initialize_light_interface()
{
	//handle light selection
	$('#light_drop_down').change(function(){
		for(var i = 0; i < lights_array.length; i++)
			lights_array[i].selected = false;
		
		if($(this).val() != '')
		{	
			//setup active light
			var e = $(this).val();
			lights_array[e].selected = true;
			setup_lights_interface(lights_array[e]);
		}
	});
	//changing type
	$('#light_type_drop_down').change(function(){
		
		var selected_type = $(this).val();
		
		// show the right info
		if(selected_type == 'ambient')
		{
			$('#light_bloom_wrap').css('display', 'none');
			$('.light_spot_show_wrap').css('display', 'none');
			$('#light_throw_wrap').css('display', 'none');
		}
		else if(selected_type == 'point')
		{
			$('#light_bloom_wrap').css('display', 'inline-block');
			$('.light_spot_show_wrap').css('display', 'none');
			$('#light_throw_wrap').css('display', 'inline-block');
		}
		else if(selected_type == 'spot')
		{
			$('#light_bloom_wrap').css('display', 'inline-block');
			$('.light_spot_show_wrap').css('display', 'inline-block');
			$('#light_throw_wrap').css('display', 'inline-block');
		}
		
		for(var i = 0; i < lights_array.length; i++)
		if(lights_array[i].selected)
		{
			lights_array[i].type = selected_type;
			
			break;
		}
	});
	
	//change x and y
	$('#light_x').change(function(){
		for(var i = 0; i < lights_array.length; i++)
		if(lights_array[i].selected)
		{
			lights_array[i].x = parseInt($(this).val());
			break;
		}
	});
	$('#light_y').change(function(){
		for(var i = 0; i < lights_array.length; i++)
		if(lights_array[i].selected)
		{
			lights_array[i].y = parseInt($(this).val());
			break;
		}
	});
	
	//throw length
	$('#light_throw').change(function(){
		for(var i = 0; i < lights_array.length; i++)
		if(lights_array[i].selected)
		{
			lights_array[i].throw_length = parseInt($(this).val());
			break;
		}
	});
	
	//degree
	$('#light_degree').change(function(){
		for(var i = 0; i < lights_array.length; i++)
		if(lights_array[i].selected)
		{
			lights_array[i].degree = parseInt($(this).val());
			break;
		}
	});
	//color
	$('#light_color').change(function(){
		for(var i = 0; i < lights_array.length; i++)
		if(lights_array[i].selected)
		{
			lights_array[i].color = $(this).val();
			break;
		}
	});
	//closewidth
	$('#light_closewidth').change(function(){
		for(var i = 0; i < lights_array.length; i++)
		if(lights_array[i].selected)
		{
			lights_array[i].closewidth = parseInt($(this).val());
			break;
		}
	});
	//farwidth
	$('#light_farwidth').change(function(){
		for(var i = 0; i < lights_array.length; i++)
		if(lights_array[i].selected)
		{
			lights_array[i].farwidth = parseInt($(this).val());
			break;
		}
	});
	
	// bloom
	$('#light_bloom').change(function(){
		for(var i = 0; i < lights_array.length; i++)
		if(lights_array[i].selected)
		{
			if($(this).is(':checked'))
				lights_array[i].bloom = true;
			else
				lights_array[i].bloom = false;
			
			break;
		}
	});
	// active
	$('#light_active').change(function(){
		for(var i = 0; i < lights_array.length; i++)
		if(lights_array[i].selected)
		{
			if($(this).is(':checked'))
				lights_array[i].active = true;
			else
				lights_array[i].active = false;
			
			break;
		}
	});
}

function light_mouse_up(click_point)
{
	//remove draggability
	for(var i = 0; i < lights_array.length; i++)
		lights_array[i].draggable = false;
}

function more_light_stuff()
{
	for(var i = 0; i < lights_array.length; i++)
		if(lights_array[i].selected)
		{
			//set light coords
			lights_array[i].x -= drag_delta.x;
			lights_array[i].y -= drag_delta.y;
			
			//set interface
			setup_lights_interface(lights_array[i]);
			
			//exit
			break;
		}	
}