function Vector(x, y)
{	
	this.x = x;
	this.y = y;
}

Vector.prototype.add = function(v2)
{
	return new Vector(this.x + v2.x, this.y + v2.y);
}

Vector.prototype.dist = function(v2)
{
	return Math.sqrt(Math.pow(v2.x - this.x,2) + Math.pow(v2.y - this.y,2));
}

Vector.prototype.scale = function(k)
{
	return new Vector(this.x*k, this.y*k);
}

Vector.prototype.vecTo = function(pos)
{
	return new Vector(this.x-pos.x, this.y-pos.y);
}

Vector.prototype.value = function()
{
	return this.x + ', ' + this.y;
}