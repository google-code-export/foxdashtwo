/*
 * Thanks to Charlie Wynn 
 * http://cwynn.com/gravity
 */

/* begin with general event management*/
var shift = false;

function keypressed(e)
{
	//37 = left
	//39 = right
	//38 = up
	//40 = down
	//46 = delete
	//alert(e.keyCode);
	
	if(e.shiftKey)
		shift = true;
	
	//move tabs
	if(e.keyCode == 9 && !shift)
	{
		var current_select = parseInt($('.taboption[name="' + current_tab + '"]').attr('number'));
		var next = $('.taboption[number="'+ (current_select + 1) +'"]');
		if(next.length == 0)
			next = $('.taboption[number="1"]');
		next.trigger('click');
		return false;
	}
	else if (e.keyCode == 9)
	{
		var current_select = parseInt($('.taboption[name="' + current_tab + '"]').attr('number'));
		var next = $('.taboption[number="'+ (current_select - 1) +'"]');
		if(next.length == 0)
			next = $('.taboption').last();
		next.trigger('click');
		return false;
	}
	
	var change_vector;
	
	if($("input:focus").length == 0 || e.override)
	{
	if(e.keyCode == 37)
		change_vector = left_vector;
	else if(e.keyCode == 39)
		change_vector = right_vector;
	else if(e.keyCode == 38)
		change_vector = up_vector;
	else if(e.keyCode == 40)
		change_vector = down_vector;
	}
	
	//movement of screen
	if(change_vector != null)
	{
		if(current_tab == "level" || current_tab == null)
			world_coords = world_coords.add(change_vector);
		else if(current_tab == "player")
		{
			$('#player_x').val(parseInt($('#player_x').val()) + change_vector.x);
			$('#player_y').val(parseInt($('#player_y').val()) + change_vector.y);
		}
		else if(current_tab == 'objects')
			move_object(change_vector);
		else if(current_tab == 'lights')
			move_light(change_vector);
		else if(current_tab == 'events')
			move_event(change_vector);
	}
	else if(e.keyCode == 46)
	{
		if(current_tab == 'objects')
			delete_object();
		else if(current_tab == 'lights')
			delete_light();
		else if(current_tab == 'events')
			delete_event();
	}
	
	setLevelDefinition();
}

function keyup(e)
{
	if(!e.shiftKey)
		shift = false;
}

function mouseDown(e) {
	var clickPoint = getCursorPosition(e);
	click_point = new Vector(clickPoint[0], clickPoint[1]);

	mouse_down = true;

	//unselect everything
	var selected_object = unselect_everything(objects_array);
	$('#object_drop_down').val('');
	
	var selected_light = unselect_everything(lights_array);
	$('#light_drop_down').val('');
	
	var selected_event = unselect_everything(events_array);
	$('#event_drop_down').val('');
	
	//select one object
	if(current_tab == "player")
		player.selected = player.contains(click_point);
	else if(current_tab == "objects")
		mouse_move_object(click_point, selected_object);
	else if(current_tab == 'lights')
		mouse_move_light(click_point, selected_light);
	else if(current_tab == 'events')
		mouse_move_event(click_point, selected_event);
	
	setLevelDefinition();
}

function unselect_everything(something_array)
{
	var selected_something;
	for(var i = 0; i < something_array.length; i++)
	{
		if(something_array[i].selected)
			selected_something = something_array[i];
		
		something_array[i].draggable = false;
		something_array[i].selected = false;
	}
	return selected_something
}

function mouseMove(e) {
	var clickPoint = getCursorPosition(e);
	drag_point = new Vector(clickPoint[0], clickPoint[1]);

	world_drag_point.x = drag_point.x + world_coords.x;
	world_drag_point.y = window.height - drag_point.y + world_coords.y;
}

function mouseUp(e) {
	var clickPoint = getCursorPosition(e);
	click_point = new Vector(clickPoint[0], clickPoint[1]);
	
	mouse_down = false;
	
	//unselect everything
	player.selected = false;
	
	object_mouse_up(click_point);
	light_mouse_up(click_point);
	
	setLevelDefinition();
}

/*helpful methads for events above*/
function getCursorPosition(e) {
	var x, y;
	if (e.pageX || e.pageY)
	{
	  x = e.pageX;
	  y = e.pageY;
	}
	else {
	  x = e.clientX + document.body.scrollLeft + document.documentElement.scrollLeft;
	  y = e.clientY + document.body.scrollTop + document.documentElement.scrollTop;
	}
	
	x -= document.getElementById("theCanvas").offsetLeft;
	y -= document.getElementById("theCanvas").offsetTop;
	return [x,y];
}

function byte2Hex(n) {
    var nybHexString = "0123456789ABCDEF";
    return String(nybHexString.substr((n >> 4) & 0x0F,1)) + nybHexString.substr(n & 0x0F,1);
}
 
/*program entry and loading point*/
function didload()
{
	reallyLoad();
}
function reallyLoad()
{
	//setup everything
	document.getElementById("canvas").innerHTML = '<canvas id="theCanvas" width="'+width+'" height="'+height+'" >You need to use Firefox, Google Chrome or IE 9 to Play</canvas>';
	canvas = document.getElementById("theCanvas");
	context = canvas.getContext("2d");
	document.onkeydown = keypressed;
	document.onkeyup = keyup;
	canvas.addEventListener('mousedown', mouseDown, false);
	canvas.addEventListener('mousemove', mouseMove, false);
	canvas.addEventListener('mouseup',   mouseUp, false);
	
	//begin looping
	setLevelDefinition();
	updateGame();
}


//this is called for every frame to update the location of the objects in the game
var lastTime;
var reset;
function updateGame()
{
	//fps management
	var nowTime = new Date().getTime();;
	
	var delta = nowTime - lastTime;
	
	if (reset < 1000)
		reset += delta;
	else
	{
		fps = 1.0 / delta * 1000.0;
		reset = 0;
	}
	
	lastTime = nowTime;
	
	//start drag delta
	current_drag_pos = drag_point;
	drag_delta = new Vector(old_drag_pos.x - current_drag_pos.x,
							current_drag_pos.y - old_drag_pos.y);
	old_drag_pos = current_drag_pos;
	
	//main update section
	mainUpdateGame();
	
	//dont touch below
	redraw();
	
	setTimeout( function(){	 updateGame(); }, 30 - (lastTime - nowTime));
};


var current_drag_pos = new Vector(0, 0);
var old_drag_pos 	 = new Vector(0, 0)
function mainUpdateGame()
{	
	if(mouse_down)
	{
		//move the screen
		if(current_tab == "level" || current_tab == null)
			world_coords = world_coords.add(drag_delta);
		else if(current_tab == "player" && player.selected)
		{
			$('#player_x').val(parseInt($('#player_x').val()) - drag_delta.x);
			$('#player_y').val(parseInt($('#player_y').val()) - drag_delta.y);
		}
		else if(current_tab == "objects")
			more_object_stuff();
		else if(current_tab == 'lights')
			more_light_stuff();
		else if(current_tab == 'events')
			more_event_stuff();
	}
}

function reset()
{
	
}