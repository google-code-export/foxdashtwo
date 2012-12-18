package com.kobaj.math;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import com.kobaj.math.android.RectF;

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
	
	// lerp between two colors, usually minX is 0, maxX is 1, value is between those two.
	public static final int linearInterpolateColor(double minX, double maxX, double value, int start_color, int end_color)
	{
		// pull apart
		// we keep a copy of these functions because its faster than calling the methods.
		int pc = start_color;
		int pr = (pc >> 16) & 0xFF;
		int pg = (pc >> 8) & 0xFF;
		int pb = (pc & 0xFF);
		int pa = pc >>> 24;
		
		int sc = end_color;
		int sr = (sc >> 16) & 0xFF;
		int sg = (sc >> 8) & 0xFF;
		int sb = (sc & 0xFF);
		int sa = sc >>> 24;
		
		// lerp
		int lr = (int) Functions.linearInterpolate(minX, maxX, value, pr, sr);
		int lg = (int) Functions.linearInterpolate(minX, maxX, value, pg, sg);
		int lb = (int) Functions.linearInterpolate(minX, maxX, value, pb, sb);
		int la = (int) Functions.linearInterpolate(minX, maxX, value, pa, sa);
		
		// stick back together;
		return (la << 24) | (lr << 16) | (lg << 8) | lb;
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
		else if (input > 255)
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
		if(min == max)
			return max;
		
		return min + (Math.random() * (max - min));
	}
	
	public static final int randomInt(int min, int max)
	{
		if(min == max)
			return max;
		
		return (int) randomDouble(min, max);
	}
	
	// see if something is inside of a rectangle
	// be sure to match rectf and x and y coords to shader or screen
	public static final boolean inRectF(RectF rectangle, double x, double y)
	{
		return (x <= rectangle.right && x >= rectangle.left && (y <= rectangle.top && y >= rectangle.bottom));
	}
	
	// helpful to see whats on screen
	// screen coordinates
	public static final boolean onScreen(int x, int y)
	{
		return onShader(screenXToShaderX(x), screenYToShaderY(y));
	}
	
	// shader coordinates
	public static final boolean onShader(double x, double y)
	{
		updateShaderRectFView();
		
		if (x >= shader_rectf_view.left && x <= shader_rectf_view.right && y >= shader_rectf_view.bottom && y <= shader_rectf_view.top)
			return true;
		
		return false;
	}
	
	public static final boolean onShader(RectFExtended objects)
	{
		updateShaderRectFView();
		
		if (equalIntersects(objects.main_rect, shader_rectf_view))
			return true;
		
		return false;
	}
	
	// helper method for the above so the two onShaders are consistent.
	protected static RectF shader_rectf_view = new RectF();
	
	protected static final void updateShaderRectFView()
	{
		final double neg_zoom = Constants.ratio * Constants.z_shader_translation;
		
		shader_rectf_view.left = (float) (-Constants.ratio + Constants.x_shader_translation - neg_zoom);
		shader_rectf_view.top = (float) (1 + Constants.y_shader_translation + Constants.z_shader_translation);
		shader_rectf_view.right = (float) (Constants.ratio + Constants.x_shader_translation + neg_zoom);
		shader_rectf_view.bottom = (float) (-1 + Constants.y_shader_translation - Constants.z_shader_translation);
	}
	
	// helpful method
	// strangly, it is programmed different than RectF.intersects...
	public static final boolean equalIntersects(RectF a, RectF b)
	{
		return equalIntersects(a, b.left, b.top, b.right, b.bottom);
	}
	
	public static final boolean equalIntersects(RectF a, double left, double top, double right, double bottom)
	{
		return (a.left <= right && left <= a.right && a.top >= bottom && top >= a.bottom);
	}
	
	public static final boolean setEqualIntersects(RectF out, RectF a, RectF b)
	{
		return setEqualIntersects(out, a, b.left, b.top, b.right, b.bottom);
	}
	
	// a and the coordinates are the colliding rect
	// return true and send_back if there is a collision
	public static final boolean setEqualIntersects(RectF send_back, RectF a, double left, double top, double right, double bottom)
	{
		if (equalIntersects(a, left, top, right, bottom))
		{
			send_back.left = (float) (Math.max(a.left, left));
			send_back.top = (float) (Math.min(a.top, top));
			send_back.right = (float) (Math.min(a.right, right));
			send_back.bottom = (float) (Math.max(a.bottom, bottom));
			
			return true;
		}
		else
		{
			send_back.left = 0;
			send_back.top = 0;
			send_back.right = 0;
			send_back.bottom = 0;
		}
		
		return false;
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
	
	public static final double polarRadToX(double rads, double radius)
	{
		return radius * Math.cos(rads);
	}
	
	public static final double polarRadToY(double rads, double radius)
	{
		return radius * Math.sin(rads);
	}
	
	public static final double polarToX(double degree, double radius)
	{
		return polarRadToX(Math.toRadians(degree), radius);
	}
	
	public static final double polarToY(double degree, double radius)
	{
		return polarRadToY(toRadians(degree), radius);
	}
	
	private static final double toRadians(double degree)
	{
		return degree * Math.PI / 180.0;
	}
	
	// not really a math function, but we need a static error check for open gl
	public static final void checkGlError()
	{
		int error;
		while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR)
			Log.e("gl_error", ": checkGlError " + error);
	}
	
	// thanks to http://www.gamedev.net/topic/229831-nearest-power-of-2/
	public static final int nearestPowerOf2(int x)
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
	public static final void setCamera(double x_camera, double y_camera)
	{
		// nothing has changed
		if (x_camera == Constants.x_shader_translation && y_camera == Constants.y_shader_translation)
			return;
		
		Matrix.setIdentityM(Constants.my_view_matrix, 0);
		Matrix.translateM(Constants.my_view_matrix, 0, (float) -x_camera, (float) -y_camera, 0);
		Constants.x_shader_translation = x_camera;
		Constants.y_shader_translation = y_camera;
	}
	
	// positive values make the camera move away from objects towards player
	public static final void setCameraZ(double z_camera)
	{
		if (z_camera < 0)
			return;
		
		if (Constants.z_shader_translation == z_camera)
			return;
		
		Matrix.setIdentityM(Constants.my_view_matrix, 0);
		Matrix.translateM(Constants.my_view_matrix, 0, (float) -Constants.x_shader_translation, (float) -Constants.y_shader_translation, (float) -z_camera);
		Constants.z_shader_translation = z_camera;
	}
	
	// calculate speed from two velocities
	public static final double speed(double x_velocity, double y_velocity)
	{
		return Math.sqrt(x_velocity * x_velocity + y_velocity * y_velocity);
	}
	
	// color selection functions
	// I'm not sure why, but this runs faster than the Color.red() etc functions
	public static final int red(int input)
	{
		return (input >> 16) & 0xFF;
	}
	
	public static final int green(int input)
	{
		return (input >> 8) & 0xFF;
	}
	
	public static final int blue(int input)
	{
		return (input & 0xFF);
	}
	
	public static final int alpha(int input)
	{
		return input >>> 24;
	}
	
	// why aren't you using byte? cause.
	public static final int makeColor(int r, int g, int b, int a)
	{
		if (r > 255 || g > 255 || b > 255 || a > 255 || r < 0 || g < 0 || b < 0 || a < 0)
			return Color.WHITE;
		
		return (a << 24) | (r << 16) | (g << 8) | b;
	}
	
	// when needing to blur something
	public static final Bitmap fastBlur(Bitmap sentBitmap, int radius)
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
	
	// setup the constants
	public static final void adjustConstantsToScreen()
	{
		Constants.gravity = -Functions.screenHeightToShaderHeight(Constants.gravity_default);
		Constants.max_y_velocity = Functions.screenHeightToShaderHeight(Constants.max_y_velocity_default);
		Constants.max_x_velocity = Functions.screenWidthToShaderWidth(Constants.max_x_velocity_default);
		Constants.normal_acceleration = Functions.screenWidthToShaderWidth(Constants.normal_acceleration_default);
		Constants.normal_reverse_acceleration = Functions.screenWidthToShaderWidth(Constants.normal_reverse_acceleration_default);
		Constants.collision_detection_height = Functions.screenHeightToShaderHeight(Constants.collision_detection_height_default);
		Constants.jump_velocity = Functions.screenHeightToShaderHeight(Constants.jump_velocity_default);
		Constants.jump_limiter = Functions.screenHeightToShaderHeight(Constants.jump_limiter_default);
		
		Constants.max_speed = Functions.speed(Constants.max_x_velocity, Constants.max_y_velocity);
		
		Constants.player_downward_platform_acc = Functions.screenHeightToShaderHeight(Constants.player_downward_platform_acc_default);
	}
}
