package com.kobaj.opengldrawable.Frame;

import org.simpleframework.xml.Attribute;

public class Sprite
{
	//texture info
	@Attribute(name="n")
	public final String n;
	@Attribute(name="x")
	public int x_start;
	@Attribute(name="y")
	public int y_start;
	@Attribute(name="w")
	public int width;
	@Attribute(name="h")
	public int height;
	
	public int x_end;
	public int y_end;
	
	public Sprite(@Attribute(name="n") String n, 
			@Attribute(name="x") int x,
			@Attribute(name="y") int y,
			@Attribute(name="w") int w,
			@Attribute(name="h") int h)
	{
		//first set everything
		this.n = n;
		this.x_start = x;
		this.y_start = y;
		this.width = w;
		this.height = h;
		
		//calculate width and height
		this.x_end = x_start + width;
		this.y_end = y_start + height;
	}
}
