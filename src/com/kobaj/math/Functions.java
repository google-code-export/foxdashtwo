package com.kobaj.math;

import java.util.ArrayList;

import android.graphics.RectF;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

public class Functions
{
	//input is related to x scale
	//output is related to y scale
	public static final double linearInterpolate(double minX, double maxX, double value, double minY, double maxY)
	{
		if (minX == maxX)
			return minY;
		
		if(value < minX)
			return minY;
		
		if(value > maxX)
			return maxY;
	
		return linearInterpolateUnclamped(minX, maxX, value, minY, maxY);
	}
	
	//method that is unclamped.
	public static final double linearInterpolateUnclamped(double minX, double maxX, double value, double minY, double maxY)
	{
		return minY * (value - maxX) / (minX - maxX) + maxY * (value - minX) / (maxX - minX);
	}
	
	public static final double clamp(double max, double value, double min)
	{
		return Math.max(Math.min(value, max), min);
	}
	
	//makes up down and down up for screen coordinates.
	public static final double fix_y(double input)
	{
		return Constants.height - input;
	}
	
	//input 0 to 255, output 0 to 1
	//really helpful for color transformations
	private static double[] byte_to_shader_lookup;
	public static final double byteToShader(int input)
	{
		if(input < 0 || input >255)
			return 0;
		
		//small optimization
		if(byte_to_shader_lookup == null)
		{
			byte_to_shader_lookup = new double[256];
			for(int i = 0; i <=255; i++)
				byte_to_shader_lookup[i] = linearInterpolate(0, i, input, 0, 1);
		}
		
		return byte_to_shader_lookup[input];
	}
	
	//used to translate screen widths to shader widths
	//for example, screen width is 0 to 800, shader is 0 to 1
	public static final double screenWidthToShaderWidth(double input_x)
	{
		return linearInterpolateUnclamped(0, Constants.width, input_x, 0, Constants.ratio);
	}
	
	public static final double screenHeightToShaderHeight(double input_y)
	{
		return linearInterpolateUnclamped(0, Constants.height, input_y, 0, 1);
	}
	
	//used to translate screen coordinates to shader coordinates
	//for example, screen width is 0 to 800px, shader is -1 to 1.
	public static final double screenXToShaderX(double input_x)
	{
		return linearInterpolateUnclamped(0, Constants.width, input_x, -Constants.ratio, Constants.ratio);
	}
	
	public static final double screenYToShaderY(double input_y)
	{
		return linearInterpolateUnclamped(0, Constants.height, input_y, -1, 1);
	}
	
	//random between two values
	public static final double randomDouble(double min, double max)
	{
		return min + (Math.random() * ((max - min) + 1));	
	}
	
	public static final int randomInt(int min, int max)
	{
		return (int)randomDouble(min, max);
	}
	
	//helpful to see whats on screen
	public static final boolean onScreen(int x, int y)
	{	
		return onShader(screenXToShaderX(x), screenYToShaderY(y));
	}
	
	public static final boolean onShader(double x, double y)
	{
		if(x > -Constants.ratio + Constants.x_shader_translation && x < Constants.ratio + Constants.x_shader_translation)
			if(y > -1 + Constants.y_shader_translation && y < 1 + Constants.y_shader_translation)
				return true;
		
		return false;
	}
	
	public static final boolean onShader(ArrayList<RectF> objects)
	{
		final double left = (-Constants.ratio - Constants.x_shader_translation);
		final double top = (1 + Constants.y_shader_translation);
		final double right = (Constants.ratio - Constants.x_shader_translation);
		final double bottom = (-1 + Constants.y_shader_translation);
		
		for(int i = objects.size() - 1; i >= 0; i--)
			if(equalIntersects(objects.get(i), left, top, right, bottom))
				return true;
		
		return false;
	}
	
	//helpful method
	//strangly, it is programmed different than RectF.intersects...
	public static final boolean equalIntersects(RectF a, RectF b)
	{
		return equalIntersects(a, b.left, b.top, b.right, b.bottom);
	}
	
	public static final boolean equalIntersects(RectF a, double left, double top, double right, double bottom)
	{
		return (a.left <= right && left <= a.right && ((a.top >= bottom && top >= a.bottom) ||
				   (a.top <= -bottom && -top <= a.bottom)));
	}

	
	//radius stuff.
	public static final double rectangularToRadius(double x, double y)
	{
		return Math.sqrt(x * x + y * y);
	}
	
	public static final double rectangularToDegree(double x, double y)
	{
		return Math.toDegrees(Math.atan2(y, x));
	}
	
	public static final double polarToX(double degree, double radius)
	{
		return radius * Math.sin(Math.toRadians(degree));
	}
	
	public static final double polarToY(double degree, double radius)
	{
		return radius * Math.cos(Math.toRadians(degree));
	}
	
	//not really a math function, but we need a static error check for open gl
	public static void checkGlError() 
	{
		int error;
		while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) 
			Log.e("gl_error", ": glError " + error);
	}
	
	//thanks to http://www.gamedev.net/topic/229831-nearest-power-of-2/
	public static int nearestPowerOf2( int x ) 
	{
		--x;    
		x |= x >> 1;
		x |= x >> 2;    
		x |= x >> 4;    
		x |= x >> 8;    
		x |= x >> 16;    
		return ++x;
	}
	
	//just one simple place to update all the camera variables
	//this is in shader coordinates
	public static void addCamera(double x, double y)
	{
		Matrix.translateM(Constants.my_view_matrix, 0, (float) x, (float) y, 0);
		Constants.x_shader_translation += x;
		Constants.y_shader_translation += y;
	}
	
	public static void setCamera(double x_camera, double y_camera)
	{
		Matrix.setIdentityM(Constants.my_view_matrix, 0);
		Matrix.translateM(Constants.my_view_matrix, 0, (float) x_camera, (float) y_camera, 0);
		Constants.x_shader_translation = x_camera;
		Constants.y_shader_translation = y_camera;
	}
}