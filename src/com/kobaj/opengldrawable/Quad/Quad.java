package com.kobaj.opengldrawable.Quad;

//a lot of help from
//http://www.learnopengles.com/android-lesson-one-getting-started/
//https://developer.android.com/resources/tutorials/opengl/opengl-es20.html	

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.kobaj.loader.GLBitmapReader;
import com.kobaj.loader.GLLoadedTexture;
import com.kobaj.math.ExtendedRectF;
import com.kobaj.math.Functions;
import com.kobaj.opengldrawable.EnumDrawFrom;
import com.kobaj.openglgraphics.AmbientLightShader;
import com.kobaj.openglgraphics.BaseLightShader;

public class Quad
{
	// transformation matrix to convert from object to world space
	private float[] my_model_matrix = new float[16];
	
	// these are in shader coordinates 0 to 1
	// and placed in the exact center of the quad
	private double x_pos = 0.0;
	private double y_pos = 0.0;
	public EnumDrawFrom currently_drawn = EnumDrawFrom.center;
	
	// I would much rather extend a physics object
	// but that wouldn't really fit in with this model.
	public double x_acc = 0.0;
	public double y_acc = 0.0;
	public double x_vel = 0.0;
	public double y_vel = 0.0;
	
	// z index doesnt have to specially be set.
	// objects will only collide if on the same z index plane.
	// this shouldn't really change much actually.
	public double z_pos = -1.0f;
	
	// physics rectangle. An object can have multiple
	// rectangles so it has better 'resolution' when interacting with other
	// quads
	// phys rect is stored in shader coordinates
	public ArrayList<ExtendedRectF> phys_rect_list = new ArrayList<ExtendedRectF>();
	
	// begin by holding these
	public int width;
	public int height;
	public double shader_width;
	public double shader_height;
	public int square;
	
	// data about the quad
	private float[] my_position_matrix = new float[18];
	protected FloatBuffer my_position;
	protected FloatBuffer my_tex_coord;
	
	// camera
	private float[] my_mvp_matrix = new float[16];
	private float[] my_mv_matrix = new float[16];
	
	// handle to texture
	protected int my_texture_data_handle = -1;
	private int texture_resource = -1;
	
	// constructores
	protected Quad()
	{
		// do nothing. Assume whoever is extending knows what he/she is doing.
	}
	
	public Quad(int texture_resource, int width, int height)
	{
		// load dat texture.
		com.kobaj.loader.GLBitmapReader.loadTextureFromResource(texture_resource);
		onCreate(texture_resource, width, height);
	}
	
	public Quad(int texture_resource, Bitmap bmp, int width, int height)
	{
		com.kobaj.loader.GLBitmapReader.loadTextureFromBitmap(texture_resource, bmp);
		onCreate(texture_resource, width, height);
	}
	
	// method that will go and get the texture handle after it has been loaded
	// so that we can draw the texture!
	private boolean setTextureDataHandle()
	{
		if (my_texture_data_handle != -1)
			return true;
		
		if (texture_resource != -1)
		{
			GLLoadedTexture proposed_handle = GLBitmapReader.loaded_textures.get(texture_resource);
			if (proposed_handle != null)
			{
				my_texture_data_handle = proposed_handle.texture_id;
				return true;
			}
		}
		
		return false;
	}
	
	// actual constructor
	// width and height in screen coordinates 0 - 800
	protected void onCreate(int texture_resource, int width, int height)
	{
		// set our texture resource
		this.texture_resource = texture_resource;
		
		// position data
		setWidthHeight(width, height);
		
		// texture data
		final int tr_square_x = com.kobaj.math.Functions.nearestPowerOf2(width);
		final int tr_square_y = com.kobaj.math.Functions.nearestPowerOf2(height);
		
		square = Math.max(tr_square_x, tr_square_y);
		
		final float tex_y = (float) com.kobaj.math.Functions.linearInterpolateUnclamped(0, square, height, 0, 1);
		final float tex_x = (float) com.kobaj.math.Functions.linearInterpolateUnclamped(0, square, width, 0, 1);
		
		simpleUpdateTexCoords(tex_x, tex_y);
		
		final float tr_x = (float) (this.shader_width / 2.0);
		final float tr_y = (float) (this.shader_height / 2.0);
		
		// up next setup phys rect list. Just a default. The user can
		// set/add/remove more rectangles as needed.
		if (phys_rect_list.isEmpty())
			phys_rect_list.add(new ExtendedRectF(-tr_x, tr_y, tr_x, -tr_y));
	}
	
	// methods for calculating stuffs
	protected void simpleUpdateTexCoords(float tex_x, float tex_y)
	{
		complexUpdateTexCoords(0, tex_x, 0, tex_y);
	}
	
	// these are in shader coordinates. start_x, end_x, start_y, end_y
	protected void complexUpdateTexCoords(float one_x, float two_x, float one_y, float two_y)
	{
		// only time I use floats...
		float buffer = -0.005f;
		one_x -= buffer;
		two_x += buffer;
		one_y -= buffer;
		two_y += buffer;
		
		// S, T (or X, Y)
		// Texture coordinate data.
		final float[] cubeTextureCoordinateData = {
				// Front face
				one_x, -one_y, one_x, -two_y, two_x, -one_y, one_x, -two_y, two_x, -two_y, two_x, -one_y };
		
		if (my_tex_coord == null)
			my_tex_coord = ByteBuffer.allocateDirect(cubeTextureCoordinateData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		else
			my_tex_coord.clear();
		
		my_tex_coord.put(cubeTextureCoordinateData).position(0);
	}
	
	// do note: this doesn't change the physics bounding box.
	// this is in screen size
	public void setWidthHeight(int width, int height)
	{
		setRotationWidthHeight(width, height, 0);
	}
	
	public void setRotationZ(double degrees)
	{
		setRotationWidthHeight(width, height, degrees);
	}
	
	// Why oh why are you doing this instead of a very simple matrix rotation Jakob?
	// Thats a good question, I very well could use model * view * projection where model is scale * rotation * translation
	// however when working on android devices we have to deal with orthographic projections
	// where the ratio is /not/ one.
	// meaning a model * view * projection with rotation will end up skewed!
	// By doing vertex multiplication with compensated coords, we eliminate the skew!
	private void setRotationWidthHeight(int width, int height, double degree)
	{
		// store these for our bounding rectangle
		this.width = width;
		this.height = height;
		
		// Define points for a cube.
		this.shader_width = Functions.screenWidthToShaderWidth(width);
		this.shader_height = Functions.screenHeightToShaderHeight(height);
		
		// begin rotation data
		final double rads = (float) Math.toRadians(degree);
		
		float pos_tr_x = width / 2.0f;
		float pos_tr_y = height / 2.0f;
		
		float neg_tr_x = -pos_tr_x;
		float neg_tr_y = -pos_tr_y;
		
		// X, Y, Z
		my_position_matrix[0] = neg_tr_x; 	my_position_matrix[1] = pos_tr_y; 	my_position_matrix[2] = 0;
		my_position_matrix[3] = neg_tr_x; 	my_position_matrix[4] = neg_tr_y; 	my_position_matrix[5] = 0;
		my_position_matrix[6] = pos_tr_x; 	my_position_matrix[7] = pos_tr_y; 	my_position_matrix[8] = 0;
		my_position_matrix[9] = neg_tr_x; 	my_position_matrix[10] = neg_tr_y; 	my_position_matrix[11] = 0;
		my_position_matrix[12] = pos_tr_x; 	my_position_matrix[13] = neg_tr_y; 	my_position_matrix[14] = 0;
		my_position_matrix[15] = pos_tr_x; 	my_position_matrix[16] = pos_tr_y; 	my_position_matrix[17] = 0;
		
		// rotate and convert
		for (int i = 0; i < 18; i = i + 3)
		{
			double tr_x1 = my_position_matrix[i];
			double tr_y1 = my_position_matrix[i + 1];
			
			double tr_x2 = tr_x1 * Math.cos(rads) - tr_y1 * Math.sin(rads);
			double tr_y2 = tr_y1 * Math.cos(rads) + tr_x1 * Math.sin(rads);
		
			my_position_matrix[i] = (float) Functions.screenWidthToShaderWidth(tr_x2);
			my_position_matrix[i + 1] = (float) Functions.screenHeightToShaderHeight(tr_y2);
		}
		
		// Initialize the buffers. and store the new coords
		if (my_position == null)
			my_position = ByteBuffer.allocateDirect(my_position_matrix.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		else
			my_position.clear();
		my_position.put(my_position_matrix).position(0);
	}
	
	// these x and y are in shader space 0 to 1
	public void setPos(double x, double y, EnumDrawFrom where)
	{
		currently_drawn = where;
		
		if (where == EnumDrawFrom.top_left)
		{
			// positive x
			// negative y
			this.x_pos = x + shader_width / 2.0;
			this.y_pos = y - shader_height / 2.0;
		}
		else if (where == EnumDrawFrom.top_right)
		{
			this.x_pos = x - shader_width / 2.0;
			this.y_pos = y - shader_height / 2.0;
		}
		else if (where == EnumDrawFrom.bottom_left)
		{
			this.x_pos = x + shader_width / 2.0;
			this.y_pos = y + shader_height / 2.0;
		}
		else if (where == EnumDrawFrom.bottom_right)
		{
			
			this.x_pos = x - shader_width / 2.0;
			this.y_pos = y + shader_height / 2.0;
		}
		else
		{
			x_pos = x;
			y_pos = y;
		}
		
		// set the rectangle
		for (int i = phys_rect_list.size() - 1; i >= 0; i--)
			phys_rect_list.get(i).setPositionWithOffset(x_pos, y_pos);
	}
	
	// getters are slower than public, but more secure
	public double getXPos()
	{
		return x_pos;
	}
	
	public double getYPos()
	{
		return y_pos;
	}
	
	// methods for
	// drawing stuffs
	private <T extends BaseLightShader> void onSetupAmbient(float[] my_view_matrix, float[] my_proj_matrix, T ambient_light)
	{
		// pass in the brightness
		GLES20.glUniform1f(ambient_light.my_brightness_handle, (float) ambient_light.my_brightness);
		
		// Set the active texture unit to texture unit 0.
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		
		// Bind the texture to this unit.
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, my_texture_data_handle);
		
		// Tell the texture uniform sampler to use this texture in the shader by
		// binding to texture unit 0.
		GLES20.glUniform1i(ambient_light.my_texture_uniform_handle, 0);
		
		// set the quad up
		// Functions.setIdentity(my_model_matrix);
		Matrix.setIdentityM(my_model_matrix, 0);
		Matrix.translateM(my_model_matrix, 0, (float) x_pos, (float) y_pos, (float) z_pos);
		
		// pass in position information
		GLES20.glVertexAttribPointer(ambient_light.my_position_handle, 3, GLES20.GL_FLOAT, false, 0, my_position);
		GLES20.glEnableVertexAttribArray(ambient_light.my_position_handle);
		
		// Pass in the texture coordinate information
		GLES20.glVertexAttribPointer(ambient_light.my_tex_coord_handle, 2, GLES20.GL_FLOAT, false, 0, my_tex_coord);
		GLES20.glEnableVertexAttribArray(ambient_light.my_tex_coord_handle);
		
		// This multiplies the view matrix by the model matrix, and stores the
		// result in the MVP matrix
		// (which currently contains model * view).
		Matrix.multiplyMM(my_mv_matrix, 0, my_view_matrix, 0, my_model_matrix, 0);
		
		// This multiplies the modelview matrix by the projection matrix, and
		// stores the result in the MVP matrix
		// (which now contains model * view * projection).
		Matrix.multiplyMM(my_mvp_matrix, 0, my_proj_matrix, 0, my_mv_matrix, 0);
		
		// Pass in the combined matrix.
		GLES20.glUniformMatrix4fv(ambient_light.my_mvp_matrix_handle, 1, false, my_mvp_matrix, 0);
		
		// Clear the currently bound buffer (so future OpenGL calls do not use
		// this buffer).
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
	}
	
	// main stuffs
	private void onDraw()
	{
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
	}
	
	// ouside calls
	public void onDrawAmbient()
	{
		onDrawAmbient(com.kobaj.math.Constants.my_view_matrix, com.kobaj.math.Constants.my_proj_matrix, com.kobaj.math.Constants.ambient_light, false);
	}
	
	public void onDrawAmbient(float[] my_view_matrix, float[] my_proj_matrix, AmbientLightShader ambient_light, boolean skip_draw_check)
	{
		// if we have a handle, draw.
		if (!setTextureDataHandle())
			return;
		
		// If on screen, draw.
		if (skip_draw_check || com.kobaj.math.Functions.onShader(phys_rect_list))
		{
			onSetupAmbient(my_view_matrix, my_proj_matrix, ambient_light);
			
			// Draw the cube.
			onDraw();
		}
	}
}
