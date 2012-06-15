package com.kobaj.math;

//Vector is not the c version of vector, it is much more like the XNA (C#) version of a vector
//Or if you prefer, it is much more like the mathematics term of of a 1x2 matrix;
public class Vector2<T>
{
	public T x;
	public T y;
	
	public Vector2(T x, T y)
	{
		this.x = x;
		this.y = y;
	}
}
