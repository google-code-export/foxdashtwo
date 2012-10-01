function my_objects(x, y, width, height, type, i)
{	
	this.id = i;
	this.x = x;
	this.y = y;
	this.width = parseInt(width);
	this.height = parseInt(height);
	this.type = type;
	this.selected = true;
	this.draggable = false;
}

my_objects.prototype.draw = function()
{
	//draw bounding box
	if(this.selected)
		drawBoxwh(this.x - 5, this.y - 5, this.width + 10, this.width + 10, purpleFill);
	
	if($('#labelcheck').is(':checked'))
		drawTextYFix(this.x, 5 + this.y + this.height, "my_objects(" + this.type + "): " + this.id, lightblueFill);
	
	drawRectwh(this.x, this.y, this.width, this.height, lightblueFill);
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