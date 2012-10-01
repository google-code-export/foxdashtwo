/*
 * Thanks to Charlie Wynn 
 * http://cwynn.com/gravity
 */

/* begin with general event management*/
function keypressed(e)
{
	//37 = left
	//39 = right
	//38 = up
	//40 = down
	//alert(e.keyCode);
	
	//movement of screen
	if(e.keyCode == 37)
		world_coords = world_coords.add(left_vector);
	else if(e.keyCode == 39)
		world_coords = world_coords.add(right_vector);
	else if(e.keyCode == 38)
		world_coords = world_coords.add(up_vector);
	else if(e.keyCode == 40)
		world_coords = world_coords.add(down_vector);
	
	setLevelDefinition();
}

function mouseDown(e) {
	var clickPoint = getCursorPosition(e);
	click_point = new Vector(clickPoint[0], clickPoint[1]);

	mouse_down = true;

	var something_selected = false;
	
	//unselect everything
	for(var i = 0; i < objects_array.length; i++)
	{
		objects_array[i].draggable = false;
		objects_array[i].selected = false;
	}
	$('#object_drop_down').val('');
	
	//select one object
	if(current_tab == "player")
		player.selected = player.contains(click_point);
	else if(current_tab == "objects")
	{
		$('#object_drop.down').val('');
		
		//select an object		
		for(var i = 0; i < objects_array.length; i++)
			if(objects_array[i].contains(click_point))
			{
				//setup the local stats
				objects_array[i].draggable = true;
				objects_array[i].selected = true;
				something_selected = true;
				
				//setup the interface
				setup_objects_interface(objects_array[i]);
				
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
			         .text(id)); 
			
			//setup our interface
			setup_objects_interface(objects_array[id]);
		}
	}
	
	setLevelDefinition();
}

function mouseMove(e) {
	var clickPoint = getCursorPosition(e);
	drag_point = new Vector(clickPoint[0], clickPoint[1]);

	world_drag_point.x = drag_point.x + world_coords.x;
	world_drag_point.y = window.height - drag_point.y + world_coords.y;
	
	setLevelDefinition();
}

function mouseUp(e) {
	var clickPoint = getCursorPosition(e);
	click_point = new Vector(clickPoint[0], clickPoint[1]);
	
	mouse_down = false;
	
	//unselect everything
	player.selected = false;
	
	for(var i = 0; i < objects_array.length; i++)
	{
		//remove draggability
		objects_array[i].draggable = false;
		
		//see if selected
		if(objects_array[i].contains(click_point) && current_tab == "objects")
			objects_array[i].selected = true;
		else
			objects_array[i].selected = false;
	}
	
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
	canvas.addEventListener('mousedown', mouseDown, false);
	canvas.addEventListener('mousemove', mouseMove, false);
	canvas.addEventListener('mouseup',   mouseUp, false);
	
	//begin looping
	setLevelDefinition();
	updateGame();
}

/*write the level out to the window*/
function setLevelDefinition()
{
	openLevelCode(true);
}

function openLevelCode(refresh)
{

	if(refresh)
	{
		if(loadLevelWindow)
		{
			if(!loadLevelWindow.closed)
			{
				loadLevelWindow.document.getElementById("leveldef").value = getLevelDefinition();
			}
		}
	}
	else
	{
		if(!loadLevelWindow || loadLevelWindow.closed)
		{
			loadLevelWindow=window.open('','','width=600,height=600,location=no');
			loadLevelWindow.document.write('<head><title>Load Level</title></head><textarea id="leveldef" onchange="levelChanged()" cols="70" rows="30">' + getLevelDefinition() + '</textarea><br>');
			loadLevelWindow.document.write('<center><button onClick="window.opener.levelChanged(document.getElementById(\'leveldef\').value);">Load Level</button></center>');
		}
		else
		{
			loadLevelWindow.document.getElementById("leveldef").value = getLevelDefinition();
			loadLevelWindow.focus();
		}
	}
}

/*get the level definition that will be placed in window*/
function getLevelDefinition()
{/*
	var def = '[new Player(new Vector(' + player.startPos.add(levelAdj).value() + '), new Vector(' + player.lastLaunchDir.value() + '), ' + player.mass + ', ' + player.radius + '),';
	for(p in planets)
	{
		def += '\n new Planet(new Vector(' + planets[p].position.add(levelAdj).value() + '), ' + planets[p].mass + ', ' + planets[p].radius + ', ' + planets[p].isGoal + ', ' + planets[p].isBonus + ')';
		if(p < planets.length-1)
			def += ',';
	}
	def += '];';
	return def;*/
}

/*build a level that is typed in*/
function levelChanged(level)
{ 
	
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
}

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
}

function reset()
{
	
}