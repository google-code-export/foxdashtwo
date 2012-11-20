function getTextWidth(text, font)
{
	context.font = typeof font == 'undefined' ? default_font : font;
	return context.measureText(text).width;
}

//draws text to screen, font is optional
function drawText(x, y, text, color, font)
{
	x = parseInt(x);
	y = parseInt(y);
	
	if(pointOnScreen(x, y))
	{
		context.fillStyle = color;
		context.font = typeof font == 'undefined' ? default_font : font;
		context.textBaseline = 'top';
		context.fillText(text, x, y);
	}
};

function drawTextYFix(x, y, text, color, font)
{
	x = parseInt(x);
	y = parseInt(y);
	
	//set position to be relative
	x = x - world_coords.x;
	y = y - world_coords.y;
	
	//fix y
	y = window.height - y - 15;
	
	drawText(x, y, text, color, font);
};

function strokeText(x, y, text, color, font)
{
	x = parseInt(x);
	y = parseInt(y);
	
	if(pointOnScreen(x, y))
	{
		context.strokeStyle = color;
		context.font = typeof font == 'undefined' ? default_font : font;
		context.lineWidth = 9;
		context.textBaseline = 'bottom';

		context.fillStyle = whiteFill;
		context.lineWidth = 3;
		context.fillText(text, x, y);
	
		context.strokeText(text, x, y);
	}
};

/*nifty helpful functions in swapping canvases*/
function saveToNewCanvas()
{
	var backCanvas = document.createElement('canvas');
	backCanvas.width = canvas.width;
	backCanvas.height = canvas.height;
	var backCtx = backCanvas.getContext('2d');
	
	backCtx.drawImage(canvas,0,0);
	
	return backCanvas;
}

function drawCanvasToCurrentCanvas(storedCanvas, alpha)
{
	var bkCanvas = storedCanvas;
	var bkContext = bkCanvas.getContext('2d');
	
	context.drawImage(bkCanvas,0,0);
}

/*main draw function*/
function redraw()
{		
	//draw black
	context.fillStyle = blackFill;
	context.fillRect(0,0,width,height);
	context.fill();
	
	//draw objects
	if($('#object_check').is(':checked'))
	for(var i = 0; i < objects_array.length; i++)
		objects_array[i].draw();
	
	//draw lights
	if($('#light_check').is(':checked'))
	for(var i = 0; i < lights_array.length; i++)
		lights_array[i].draw();
	
	//draw events
	if($('#event_check').is(':checked'))
	for(var i = 0; i < events_array.length; i++)
		events_array[i].draw();
	
	//draw the player
	player.draw();
	
	//draw the map limits
	if($('#labelcheck').is(':checked'))
		drawTextYFix($('#left-limit').val(), $('#top-limit').val(), "Level Limits", lightgreyFill);
	drawBox($('#left-limit').val(),
			 $('#top-limit').val(),
			 $('#right-limit').val(),
			 $('#bottom-limit').val(), lightgreyFill);
	
	//draw the phone overlay
	if($('#phonecheck').is(':checked'))
	{
		if($('#labelcheck').is(':checked'))
			drawText(0, 305, "Phone Overlay", lightredFill);
		drawBoxwh(world_coords.x, world_coords.y, 800, 480, lightredFill);
	}
	
	//draw fps
	if($('#fpscheck').is(':checked'))
		drawText(10, 10, "fps: " + fps, whiteFill);
	
	//draw the mouse pointer
	if($('#mousecheck').is(':checked'))
	{
		//drag, world, real
		var local_color;
		if(mouse_down)
			local_color = greenFill;
		else
			local_color = lightgreenFill;
		
		drawText(drag_point.x, drag_point.y - 45, 'Drag (' + drag_delta.x + ', ' + drag_delta.y + ')', local_color);
		drawText(drag_point.x, drag_point.y - 30, 'World (' + world_drag_point.x + ', ' + world_drag_point.y + ')', blueFill);
		drawText(drag_point.x, drag_point.y - 15, 'Screen (' + drag_point.x + ', ' + drag_point.y + ')', whiteFill);
	}
};

function drawArrow(fromx, fromy, tox, toy, fill){
    var headlen = 10;   // length of head in pixels
    var angle = Math.atan2(toy-fromy,tox-fromx);
	context.beginPath()
	context.lineWidth = 3;
	context.strokeStyle = fill;
    context.moveTo(fromx, fromy);
    context.lineTo(tox, toy);
    context.lineTo(tox-headlen*Math.cos(angle-Math.PI/6),toy-headlen*Math.sin(angle-Math.PI/6));
    context.moveTo(tox, toy);
    context.lineTo(tox-headlen*Math.cos(angle+Math.PI/6),toy-headlen*Math.sin(angle+Math.PI/6));
	context.stroke();
}

//generic draw rectangle function
function drawRect(x1, y1, x2, y2, color)
{
	var width = x2 - x1;
	var height = y2 - y1;
	
	drawRectwh(x1, y1, width, height, color);
}

function drawBox(x1, y1, x2, y2, color)
{
	var width = x2 - x1;
	var height = y2 - y1;
	
	drawBoxwh(x1, y1, width, height, color);
}

function drawRectwh(x1, y1, width, height, color, degree, scale)
{	
	if(degree == undefined)
		degree = 0;
	if(scale == undefined)
		scale = 1;
	
	width = width * scale;
	height = height * scale;
	
	var origx = x1;
	var origy = y1;
	
	//set position to be relative
	x1 = x1 - world_coords.x;
	y1 = y1 - world_coords.y;
	
	//fix y
	y1 = window.height - y1;
	
	//begin rotation
	context.save();
	context.translate(x1 + width / 2.0 + width * (1 - scale), y1 - height / 2.0 - height * (1 - scale));
	context.rotate(degree * Math.PI / 180);
	
	// draw your object
	
	//draw
	if(squareOnScreen(x1, y1, width, height))
	{
		context.beginPath();
		context.fillStyle = color;
		context.fillRect(- width / 2.0, height / 2.0 ,width,-height);
		context.fill();
		context.closePath();
	}
	
	if($('#outlines').is(":checked"))
	{
		var send_y = window.height -  (height / 2.0) + world_coords.y;
		drawBoxwh( - width / 2.0  + world_coords.x, send_y, width,height, darkgreyFill)
	}
		
	//restore
	context.restore();
};

//only used for boss health outline
function drawBoxwh(x1,y1, width, height, color)
{	
	//set position to be relative
	x1 = x1 - world_coords.x;
	y1 = y1 - world_coords.y;
	
	//fix y
	y1 = window.height - y1;
	
	//draw
	if(squareOnScreen(x1, y1, width, height))
	{
		context.beginPath();
		context.strokeStyle = color;
		context.strokeRect(x1,y1,width,-height);
		context.stroke();
		context.closePath();
	}
};

//draw an image
function drawMyImageRotation(img, x1, y1, width, height, degree, scale)
{
	if(img == null)
		return;
	
	if(degree == undefined)
		degree = 0;
	if(scale == undefined)
		scale = 1;
	
	width = width * scale;
	height = height * scale;
	
	var origx = x1;
	var origy = y1;
	
	//set position to be relative
	x1 = x1 - world_coords.x;
	y1 = y1 - world_coords.y;
	
	//fix y
	y1 = window.height - y1;
	
	//begin rotation
	context.save();
	context.translate(x1 + width / 2.0 + width * (1 - scale), y1 - height / 2.0 - height * (1 - scale));
	context.rotate(degree * Math.PI / 180);
	
	if(squareOnScreen(x1, y1, width, height))
		context.drawImage(img, - width / 2.0 , (height / 2.0) - height, width, height);
	
	//restore
	context.restore();
}

function drawMyImage(img, x1, y1, width, height)
{
	if(img == null)
		return;
	
	//set position to be relative
	x1 = x1 - world_coords.x;
	y1 = y1 - world_coords.y;
	
	//fix y
	y1 = window.height - y1;
	
	if(squareOnScreen(x1, y1, width, height))
		context.drawImage(img,x1,y1 - height);
}

//only used for drawing the weapon type
function drawCircle(x,y,radius,color)
{
	drawArc(x, y, radius, 0, Math.PI * 2, color);
}

function drawArc(x, y, radius, arc_start, arc_end, color)
{
	//set position to be relative
	x = x - world_coords.x;
	y = y - world_coords.y;
	
	//fix y
	y = window.height - y;
	
	//draw
	context.beginPath();
	context.lineWidth = 10;
	context.strokeStyle = color;
	context.arc(x, y, radius, arc_start, arc_end,false);
	context.stroke();
	context.closePath();
	
	context.lineWidth = 1;
}

function drawLamp(x, y, radius, degree, closewidth, farwidth, color)
{
	//calculate maxes
	if(closewidth / 2.0 > radius)
		closewidth = radius * 2;
	if(farwidth / 2.0 > radius)
		farwidth = radius * 2;
	if(closewidth > farwidth)
		closewidth = farwidth;
	
	//set position to be relative
	x = x - world_coords.x;
	y = y - world_coords.y;
	
	//fix y
	y = window.height - y;
	
	//save canvas and rotate
	context.save();
	context.translate(x + radius, y - radius);
	context.rotate(degree * Math.PI / 180);
	// draw your object
	
	//draw
	context.beginPath();
	context.lineWidth = 10;
	context.strokeStyle = color;
	context.moveTo(0,0);
	context.lineTo(0, closewidth / 2.0);
	context.lineTo(radius, farwidth / 2.0);
	context.lineTo(radius, -farwidth / 2.0);
	context.lineTo(0, -closewidth / 2.0);
	context.lineTo(0,0);
	context.stroke();
	context.closePath();
	
	//restore
	context.restore();

	context.lineWidth = 1;
}

function pointOnScreen(x, y)
{
	return (x <= width && x >= 0 &&
			(y <= height && y >= 0));
}

function squareOnScreen(x, y, my_width, my_height)
{
	if(my_height < 0)
		return (x <= width && 0 <= x + my_width && y - my_height >= 0 && height >= y);
		
	return (x <= width && 0 <= x + my_width && y >= 0 && height >= y - my_height);
}