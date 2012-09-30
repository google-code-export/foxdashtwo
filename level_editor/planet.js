function Planet(position, mass, radius, isGoal, isBonus)
{	
	this.position = position;
	this.mass = mass;
	this.radius = radius;
	this.isGoal = isGoal;
	this.isBonus = isBonus;
	this.isVisible = true;
}

Planet.prototype.draw = function()
{
	var fill = this.isGoal ? greenFill : this.isBonus ? purpleFill : blueFill;
	if(this.isVisible) drawCircle(this.position.x, this.position.y, this.radius, fill);
	
	if(!this.isBonus)
		drawText(this.position.x-18, this.position.y+7, "mass: " + this.mass, whiteFill, '10px sans-serif');
};