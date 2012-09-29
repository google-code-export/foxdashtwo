package com.kobaj.math;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.RectF;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

public class Functions
{
	// input is related to x scale
	// output is related to y scale
	public static final double linearInterpolate(double minX, double maxX, double value, double minY, double maxY)
	{
		if (minX == maxX)
			return minY;
		
		if (value < minX)
			return minY;
		
		if (value > maxX)
			return maxY;
		
		return linearInterpolateUnclamped(minX, maxX, value, minY, maxY);
	}
	
	// method that is unclamped.
	public static final double linearInterpolateUnclamped(double minX, double maxX, double value, double minY, double maxY)
	{
		return minY * (value - maxX) / (minX - maxX) + maxY * (value - minX) / (maxX - minX);
	}
	
	public static final double clamp(double max, double value, double min)
	{
		return Math.max(Math.min(value, max), min);
	}
	
	// makes up down and down up for screen coordinates.
	public static final double fix_y(double input)
	{
		return Constants.height - input;
	}
	
	// input 0 to 255, output 0 to 1
	// really helpful for color transformations
	private static double[] byte_to_shader_lookup;
	public static final double byteToShader(int input)
	{
		if (input < 0)
			return 0;
		else if(input > 255)
			return 1;
		
		// small optimization
		if (byte_to_shader_lookup == null)
		{
			byte_to_shader_lookup = new double[256];
			for (int i = 0; i <= 255; i++)
				byte_to_shader_lookup[i] = linearInterpolate(0, 255, i, 0, 1);
		}
		
		return byte_to_shader_lookup[input];
	}
	
	// used to translate screen widths to shader widths
	// for example, screen width is 0 to 800, shader is 0 to 1 ish
	public static final double screenWidthToShaderWidth(double input_x)
	{
		return linearInterpolateUnclamped(0, Constants.width, input_x, 0, Constants.shader_width);
	}
	
	public static final double screenHeightToShaderHeight(double input_y)
	{
		return linearInterpolateUnclamped(0, Constants.height, input_y, 0, Constants.shader_height);
	}
	
	// used to translate screen coordinates to shader coordinates
	// for example, screen width is 0 to 800px, shader is -1 to 1.
	public static final double screenXToShaderX(double input_x)
	{
		return linearInterpolateUnclamped(0, Constants.width, input_x, -Constants.ratio, Constants.ratio);
	}
	
	public static final double screenYToShaderY(double input_y)
	{
		return linearInterpolateUnclamped(0, Constants.height, input_y, -1, 1);
	}
	
	// used to translate shader coordinates to screen coordinates
	// for example, screen width is 0 to 800px, shader is -1 to 1.
	// really you should not go from shader to screen
	public static final double shaderXToScreenX(double input_x)
	{
		return linearInterpolateUnclamped(-Constants.ratio, Constants.ratio, input_x, 0, Constants.width);
	}
	
	public static final double shaderYToScreenY(double input_y)
	{
		return linearInterpolateUnclamped(-1, 1, input_y, 0, Constants.height);
	}
	
	public static final double shaderWidthToScreenWidth(double input_x)
	{
		return linearInterpolateUnclamped(0, Constants.shader_width, input_x, 0, Constants.width);
	}
	
	public static final double shaderHeightToScreenHeight(double input_y)
	{
		return linearInterpolateUnclamped(0, Constants.shader_height, input_y, 0, Constants.height);
	}
	
	// random between two values
	public static final double randomDouble(double min, double max)
	{
		return min + (Math.random() * ((max - min) + 1));
	}
	
	public static final int randomInt(int min, int max)
	{
		return (int) randomDouble(min, max);
	}
	
	//see if something is inside of a rectangle
	//be sure to match rectf and x and y coords to shader or screen
	public static final boolean inRectF(RectF rectangle, double x, double y)
	{
		return (x <= rectangle.right && x >= rectangle.left &&
				((-y <= rectangle.top && -y >= rectangle.bottom) || (-y >= rectangle.top && -y <= rectangle.bottom)));
	}
	
	// helpful to see whats on screen
	public static final boolean onScreen(int x, int y)
	{
		return onShader(screenXToShaderX(x), screenYToShaderY(y));
	}
	
	public static final boolean onShader(double x, double y)
	{
		if (x > -Constants.ratio + Constants.x_shader_translation && x < Constants.ratio + Constants.x_shader_translation)
			if (y > -1 + Constants.y_shader_translation && y < 1 + Constants.y_shader_translation)
				return true;
		
		return false;
	}
	
	public static final boolean onShader(ArrayList<ExtendedRectF> objects)
	{
		final double left = (-Constants.ratio - Constants.x_shader_translation);
		final double top = (1 + -Constants.y_shader_translation);
		final double right = (Constants.ratio - Constants.x_shader_translation);
		final double bottom = (-1 + -Constants.y_shader_translation);
		
		for (int i = objects.size() - 1; i >= 0; i--)
			if (equalIntersects(objects.get(i).main_rect, left, top, right, bottom))
				return true;
		
		return false;
	}
	
	// helpful method
	// strangly, it is programmed different than RectF.intersects...
	public static final boolean equalIntersects(RectF a, RectF b)
	{
		return equalIntersects(a, b.left, b.top, b.right, b.bottom);
	}
	
	public static final boolean equalIntersects(RectF a, double left, double top, double right, double bottom)
	{
		return (a.left <= right && left <= a.right && ((a.top >= bottom && top >= a.bottom) || (a.top <= -bottom && -top <= a.bottom)));
	}
	
	public static final RectF setEqualIntersects(RectF a, RectF b)
	{
		return setEqualIntersects(a, b.left, b.top, b.right, b.bottom);
	}
	
	public static final RectF setEqualIntersects(RectF a, double left, double top, double right, double bottom)
	{
		if (equalIntersects(a, left, top, right, bottom))
		{
			RectF send_back = new RectF();
			send_back.left = (float) (Math.max(a.left, left));
			
			// correct orientation
			if (a.top < a.bottom && top < bottom)
				send_back.top = (float) (Math.max(a.top, top));
			else
				send_back.top = (float) (Math.min(a.top, top));
			
			send_back.right = (float) (Math.min(a.right, right));
			
			if (a.top < a.bottom && top < bottom)
				send_back.bottom = (float) (Math.min(a.bottom, bottom));
			else
				send_back.bottom = (float) (Math.max(a.bottom, bottom));
			
			return send_back;
		}
		return null;
	}
	
	// radius stuff.
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
	
	// not really a math function, but we need a static error check for open gl
	public static void checkGlError()
	{
		int error;
		while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR)
			Log.e("gl_error", ": checkGlError " + error);
	}
	
	// thanks to http://www.gamedev.net/topic/229831-nearest-power-of-2/
	public static int nearestPowerOf2(int x)
	{
		--x;
		x |= x >> 1;
		x |= x >> 2;
		x |= x >> 4;
		x |= x >> 8;
		x |= x >> 16;
		return ++x;
	}
	
	// just one simple place to update all the camera variables
	// this is in shader coordinates
	public static void addCamera(double x_camera, double y_camera)
	{
		Matrix.translateM(Constants.my_view_matrix, 0, (float) x_camera, (float) y_camera, 0);
		Constants.x_shader_translation += x_camera;
		Constants.y_shader_translation += y_camera;
	}
	
	public static void setCamera(double x_camera, double y_camera)
	{
		Matrix.setIdentityM(Constants.my_view_matrix, 0);
		Matrix.translateM(Constants.my_view_matrix, 0, (float) x_camera, (float) y_camera, 0);
		Constants.x_shader_translation = x_camera;
		Constants.y_shader_translation = y_camera;
	}
	
	// when needing to blur something
	public static Bitmap fastBlur(Bitmap sentBitmap, int radius)
	{
		// Stack Blur Algorithm by Mario Klingemann <mario@quasimondo.com>
		
		Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
		
		if (radius < 1)
			return bitmap;
		
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		
		int[] pix = new int[w * h];
		bitmap.getPixels(pix, 0, w, 0, 0, w, h);
		
		int wm = w - 1;
		int hm = h - 1;
		int wh = w * h;
		int div = radius + radius + 1;
		
		int a[] = new int[wh];
		int r[] = new int[wh];
		int g[] = new int[wh];
		int b[] = new int[wh];
		int asum, rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
		int vmin[] = new int[Math.max(w, h)];
		
		int divsum = (div + 1) >> 1;
		divsum *= divsum;
		int dv[] = new int[256 * divsum];
		for (i = 0; i < 256 * divsum; i++)
			dv[i] = (i / divsum);
		
		yw = yi = 0;
		
		int[][] stack = new int[div][4];
		int stackpointer;
		int stackstart;
		int[] sir;
		int rbs;
		int r1 = radius + 1;
		int aoutsum, routsum, goutsum, boutsum;
		int ainsum, rinsum, ginsum, binsum;
		
		for (y = 0; y < h; y++)
		{
			ainsum = rinsum = ginsum = binsum = aoutsum = routsum = goutsum = boutsum = asum = rsum = gsum = bsum = 0;
			for (i = -radius; i <= radius; i++)
			{
				p = pix[yi + Math.min(wm, Math.max(i, 0))];
				sir = stack[i + radius];
				
				sir[0] = (p >> 16) & 0xFF;
				sir[1] = (p >> 8) & 0xFF;
				sir[2] = (p & 0xFF);
				sir[3] = p >>> 24;
				
				rbs = r1 - Math.abs(i);
				
				rsum += sir[0] * rbs;
				gsum += sir[1] * rbs;
				bsum += sir[2] * rbs;
				asum += sir[3] * rbs;
				
				if (i > 0)
				{
					rinsum += sir[0];
					ginsum += sir[1];
					binsum += sir[2];
					ainsum += sir[3];
				}
				else
				{
					routsum += sir[0];
					goutsum += sir[1];
					boutsum += sir[2];
					aoutsum += sir[3];
				}
			}
			stackpointer = radius;
			
			for (x = 0; x < w; x++)
			{
				r[yi] = dv[rsum];
				g[yi] = dv[gsum];
				b[yi] = dv[bsum];
				a[yi] = dv[asum];
				
				rsum -= routsum;
				gsum -= goutsum;
				bsum -= boutsum;
				asum -= aoutsum;
				
				stackstart = stackpointer - radius + div;
				sir = stack[stackstart % div];
				
				routsum -= sir[0];
				goutsum -= sir[1];
				boutsum -= sir[2];
				aoutsum -= sir[3];
				
				if (y == 0)
					vmin[x] = Math.min(x + radius + 1, wm);
				
				p = pix[yw + vmin[x]];
				
				sir[0] = (p >> 16) & 0xFF;
				sir[1] = (p >> 8) & 0xFF;
				sir[2] = (p & 0xFF);
				sir[3] = p >>> 24;
				
				rinsum += sir[0];
				ginsum += sir[1];
				binsum += sir[2];
				ainsum += sir[3];
				
				rsum += rinsum;
				gsum += ginsum;
				bsum += binsum;
				asum += ainsum;
				
				stackpointer = (stackpointer + 1) % div;
				sir = stack[(stackpointer) % div];
				
				routsum += sir[0];
				goutsum += sir[1];
				boutsum += sir[2];
				aoutsum += sir[3];
				
				rinsum -= sir[0];
				ginsum -= sir[1];
				binsum -= sir[2];
				ainsum -= sir[3];
				
				yi++;
			}
			yw += w;
		}
		
		for (x = 0; x < w; x++)
		{
			ainsum = rinsum = ginsum = binsum = aoutsum = routsum = goutsum = boutsum = asum = rsum = gsum = bsum = 0;
			yp = -radius * w;
			for (i = -radius; i <= radius; i++)
			{
				yi = Math.max(0, yp) + x;
				
				sir = stack[i + radius];
				
				sir[0] = r[yi];
				sir[1] = g[yi];
				sir[2] = b[yi];
				sir[3] = a[yi];
				
				rbs = r1 - Math.abs(i);
				
				rsum += r[yi] * rbs;
				gsum += g[yi] * rbs;
				bsum += b[yi] * rbs;
				asum += a[yi] * rbs;
				
				if (i > 0)
				{
					rinsum += sir[0];
					ginsum += sir[1];
					binsum += sir[2];
					ainsum += sir[3];
				}
				else
				{
					routsum += sir[0];
					goutsum += sir[1];
					boutsum += sir[2];
					aoutsum += sir[3];
				}
				
				if (i < hm)
					yp += w;
				
			}
			
			yi = x;
			stackpointer = radius;
			
			for (y = 0; y < h; y++)
			{
				pix[yi] = (dv[asum] << 24) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];
				
				rsum -= routsum;
				gsum -= goutsum;
				bsum -= boutsum;
				asum -= aoutsum;
				
				stackstart = stackpointer - radius + div;
				sir = stack[stackstart % div];
				
				routsum -= sir[0];
				goutsum -= sir[1];
				boutsum -= sir[2];
				aoutsum -= sir[3];
				
				if (x == 0)
					vmin[y] = Math.min(y + r1, hm) * w;
				
				p = x + vmin[y];
				
				sir[0] = r[p];
				sir[1] = g[p];
				sir[2] = b[p];
				sir[3] = a[p];
				
				rinsum += sir[0];
				ginsum += sir[1];
				binsum += sir[2];
				ainsum += sir[3];
				
				rsum += rinsum;
				gsum += ginsum;
				bsum += binsum;
				asum += ainsum;
				
				stackpointer = (stackpointer + 1) % div;
				sir = stack[stackpointer];
				
				routsum += sir[0];
				goutsum += sir[1];
				boutsum += sir[2];
				aoutsum += sir[3];
				
				rinsum -= sir[0];
				ginsum -= sir[1];
				binsum -= sir[2];
				ainsum -= sir[3];
				
				yi += w;
			}
		}
		
		bitmap.setPixels(pix, 0, w, 0, 0, w, h);
		
		return bitmap;
	}
	
	//setup the constants
	public static void adjustConstantsToScreen()
	{
		Constants.gravity = -Functions.screenHeightToShaderHeight(Constants.gravity_default);
		Constants.max_y_velocity = Functions.screenHeightToShaderHeight(Constants.max_y_velocity_default);
		Constants.max_x_velocity = Functions.screenWidthToShaderWidth(Constants.max_x_velocity_default);
		Constants.normal_acceleration = Functions.screenWidthToShaderWidth(Constants.normal_acceleration_default);
		Constants.normal_reverse_acceleration = Functions.screenWidthToShaderWidth(Constants.normal_reverse_acceleration_default);
		Constants.collision_detection_height = Functions.screenHeightToShaderHeight(Constants.collision_detection_height_default);
		Constants.jump_velocity = Functions.screenHeightToShaderHeight(Constants.jump_velocity_default);
		Constants.jump_limiter = Functions.screenHeightToShaderHeight(Constants.jump_limiter_default);
	}
}
