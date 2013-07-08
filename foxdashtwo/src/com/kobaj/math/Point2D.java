package com.kobaj.math;

import org.simpleframework.xml.Element;

public class Point2D
{
	@Element
	public double x;
	
	@Element
	public double y;
	
	public void setPoint2D(double x, double y)
	{
		this.x = x;
		this.y = y;
	}
}
