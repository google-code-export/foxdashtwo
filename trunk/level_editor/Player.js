function Player(width, height)
{	
	this.width = width;
	this.height = height;
	this.selected = false;
}

Player.prototype.draw = function()
{
	if($('#labelcheck').is(':checked'))
		drawTextYFix($('#player_x').val(), this.height + parseInt($('#player_y').val()), "Player", redFill);
	drawRectwh($('#player_x').val(), $('#player_y').val(), this.width, this.height, redFill);
}

Player.prototype.contains = function(v2)
{
	var x = parseInt($('#player_x').val());
	var y = parseInt($('#player_y').val());
	
	var x2 = x + this.width;
	var y2 = y + this.height;
	
	var mouse_y = window.height - v2.y + world_coords.y;
	var mouse_x = v2.x + world_coords.x;

	if(mouse_x >= x && mouse_x <= x2 && mouse_y <= y2 && mouse_y >= y)
		return true;
	
	return false;
}