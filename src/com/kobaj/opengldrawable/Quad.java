package com.kobaj.opengldrawable;

//a lot of help from
//http://www.learnopengles.com/android-lesson-one-getting-started/
//https://developer.android.com/resources/tutorials/opengl/opengl-es20.html	

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.RectF;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.kobaj.loader.GLLoadedTexture;
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
	
	//I would much rather extend a physics object
	//but that wouldn't really fit in with this model.
	public double x_acc = 0.0;
	public double y_acc = 0.0;
	public double x_vel = 0.0;
	public double y_vel = 0.0;
	
	//z index doesnt have to specially be set.
	//objects will only collide if on the same z index plane.
	//this shouldn't really change much actually.
	public double z_pos = -1.0f;
	
	// physics rectangle. An object can have multiple
	// rectangles so it has better 'resolution' when interacting with other quads
	// phys rect is stored in shader coordinates
	public ArrayList<RectF> phys_rect_list = new ArrayList<RectF>();
	
	//begin by holding these
	public int width;
	public int height;
	public double shader_width;
	public double shader_height;
	public int square;
	
	// data about the quad
	protected FloatBuffer my_position;
	protected FloatBuffer my_tex_coord;
	
	// camera
	private float[] my_mvp_matrix = new float[16];
	
	// handle to texture
	protected int my_texture_data_handle;
	
	//constructores
	protected Quad()
	{
		//do nothing. Assume whoever is extending knows what he/she is doing.
	}
	
	public Quad(int texture_resource)
	{
		this(texture_resource, -1, -1);
	}
	
	public Quad(int texture_resource, int width, int height)
	{
		// load dat texture.
		my_texture_data_handle = com.kobaj.loader.GLBitmapReader.loadTextureFromResource(texture_resource);
		onCreate(texture_resource, width, height);
	}
	
	public Quad(int texture_resource, Bitmap bmp)
	{
		this(texture_resource, bmp, -1, -1);
	}
	
	public Quad(int texture_resource, Bitmap bmp, int width, int height)
	{
		my_texture_data_handle = com.kobaj.loader.GLBitmapReader.loadTextureFromBitmap(texture_resource, bmp);
		onCreate(texture_resource, width, height);
	}
	
	//actual constructor
	//width and height in screen coordinates 0 - 800
	protected void onCreate(int texture_resource, int width, int height)
	{
		if (width == -1 && height == -1)
		{
			//optimize get
			GLLoadedTexture gl_loaded_texture = com.kobaj.loader.GLBitmapReader.loaded_textures.get(texture_resource); 
			
			width = gl_loaded_texture.width;
			height = gl_loaded_texture.height;
		}
		
		//store these for our bounding rectangle
		this.width = width;
		this.height = height;
		
		// Define points for a cube.
		final float tr_x = (float) com.kobaj.math.Functions.screenWidthToShaderWidth(width);
		final float tr_y = (float) com.kobaj.math.Functions.screenHeightToShaderHeight(height);
		
		this.shader_width = tr_x * 2.0;
		this.shader_height = tr_y * 2.0;
		
		// X, Y, Z
		final float[] cubePositionData = {
				// In OpenGL counter-clockwise winding is default. This means
				// that when we look at a triangle,
				// if the points are counter-clockwise we are looking at the
				// "front". If not we are looking at
				// the back. OpenGL has an optimization where all back-facing
				// triangles are culled, since they
				// usually represent the backside of an object and aren't
				// visible anyways.
				
				// Front face
				-tr_x, tr_y, 0.0f,
				-tr_x, -tr_y, 0.0f,
				tr_x, tr_y, 0.0f,
				-tr_x, -tr_y, 0.0f,
				tr_x, -tr_y, 0.0f,
				tr_x, tr_y, 0.0f
		
		};
		
		final int tr_square_x = com.kobaj.math.Functions.nearestPowerOf2(width);
		final int tr_square_y = com.kobaj.math.Functions.nearestPowerOf2(height);
		
		square = Math.max(tr_square_x, tr_square_y);

		final float tex_y = (float) com.kobaj.math.Functions.linearInterpolateUnclamped(0, square, height, 0, 1);
		final float tex_x = (float) com.kobaj.math.Functions.linearInterpolateUnclamped(0, square, width, 0, 1);
		
		simpleUpdateTexCoords(tex_x, tex_y);
		
		// Initialize the buffers.
		my_position = ByteBuffer.allocateDirect(cubePositionData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		my_position.put(cubePositionData).position(0);
		
		//up next setup phys rect list. Just a default. The user can set/add/remove more rectangles as needed.
		phys_rect_list.add(new RectF(-tr_x, tr_y, tr_x, -tr_y));
	}
	
	//methods for calculating stuffs
	protected void simpleUpdateTexCoords(float tex_x, float tex_y)
	{
		complexUpdateTexCoords(0, tex_x, 0, tex_y);
	}
	
	//these are in shader coordinates. start_x, end_x, start_y, end_y
	protected void complexUpdateTexCoords(float one_x, float two_x, float one_y, float two_y)
	{
		//only time I use floats...
		
		// S, T (or X, Y)
		// Texture coordinate data.
		final float[] cubeTextureCoordinateData = {
				// Front face
				one_x, -one_y,
				one_x, -two_y,
				two_x, -one_y,
				one_x, -two_y,
				two_x, -two_y,
				two_x, -one_y };
	
		// my_tex_coord.clear();
		if(my_tex_coord == null)
			my_tex_coord = ByteBuffer.allocateDirect(cubeTextureCoordinateData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		else
			my_tex_coord.clear();
		
		my_tex_coord.put(cubeTextureCoordinateData).position(0);
	}
	
	//these x and y are in shader space 0 to 1
	public void setPos(double x, double y, EnumDrawFrom where)
	{
		if(where == EnumDrawFrom.top_left)
		{
			//positive x
			//negative y
			this.x_pos = x + shader_width / 2.0;
			this.y_pos = y - shader_height / 2.0;	
		}
		else if(where == EnumDrawFrom.top_right)
		{
			this.x_pos = x - shader_width / 2.0;
			this.y_pos = y - shader_height / 2.0;
		}
		else if(where == EnumDrawFrom.bottom_left)
		{
			this.x_pos = x + shader_width / 2.0;
			this.y_pos = y + shader_height / 2.0;	
		}
		else if(where == EnumDrawFrom.bottom_right)
		{

			this.x_pos = x - shader_width / 2.0;
			this.y_pos = y + shader_height / 2.0;			
		}
		else
		{
			x_pos = x;
			y_pos = y;
		}
		
		//set the rectangle
		for(RectF rect: phys_rect_list)
		{
			final double rect_half_width = rect.width() / 2.0;
			final double rect_half_height = rect.height() / 2.0;
			
			rect.left = (float)(x_pos - rect_half_width);
			rect.top = (float)(y_pos + rect_half_height);
			rect.right = (float)(x_pos + rect_half_width);
			rect.bottom = (float)(y_pos - rect_half_height);
		}
	} 
	
	//getters are slower than public, but more secure
	public double get_x_pos()
	{
		return x_pos;
	}
	
	public double get_y_pos()
	{
		return y_pos;
	}
	
	// methods for
	// drawing stuffs
	private <T extends BaseLightShader> void onSetupAmbient(float[] my_view_matrix, float[] my_proj_matrix, T ambient_light)
	{
		// Pass in the color information
		GLES20.glUniform4f(ambient_light.my_color_handle, (float)ambient_light.my_color_r, (float)ambient_light.my_color_g, (float)ambient_light.my_color_b, 1.0f);
		
		// pass in the brightness
		GLES20.glUniform1f(ambient_light.my_brightness_handle, (float)ambient_light.my_brightness);
		
		
		// Set the active texture unit to texture unit 0.
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		
		// Bind the texture to this unit.
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, my_texture_data_handle);
		
		// Tell the texture uniform sampler to use this texture in the shader by
		// binding to texture unit 0.
		GLES20.glUniform1i(ambient_light.my_texture_uniform_handle, 0);
		
		
		// set the quad up
		Matrix.setIdentityM(my_model_matrix, 0);
		Matrix.translateM(my_model_matrix, 0, (float) x_pos, (float) y_pos, (float) z_pos);
		
		// pass in position information
		my_position.position(0);
		GLES20.glVertexAttribPointer(ambient_light.my_position_handle, 3, GLES20.GL_FLOAT, false, 0, my_position);
		GLES20.glEnableVertexAttribArray(ambient_light.my_position_handle);
		
		// Pass in the texture coordinate information
		my_tex_coord.position(0);
		GLES20.glVertexAttribPointer(ambient_light.my_tex_coord_handle, 2, GLES20.GL_FLOAT, false, 0, my_tex_coord);
		GLES20.glEnableVertexAttribArray(ambient_light.my_tex_coord_handle);
		
		
		// This multiplies the view matrix by the model matrix, and stores the
		// result in the MVP matrix
		// (which currently contains model * view).
		Matrix.multiplyMM(my_mvp_matrix, 0, my_view_matrix, 0, my_model_matrix, 0);
		
		// Pass in the modelview matrix.
		GLES20.glUniformMatrix4fv(ambient_light.my_mv_matrix_handle, 1, false, my_mvp_matrix, 0);
		
		// This multiplies the modelview matrix by the projection matrix, and
		// stores the result in the MVP matrix
		// (which now contains model * view * projection).
		Matrix.multiplyMM(my_mvp_matrix, 0, my_proj_matrix, 0, my_mvp_matrix, 0);
		
		// Pass in the combined matrix.
		GLES20.glUniformMatrix4fv(ambient_light.my_mvp_matrix_handle, 1, false, my_mvp_matrix, 0);
		
	}

	//main stuffs
	private void onDraw()
	{
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
	}
	
	//ouside calls
	public void onDrawAmbient()
	{
		onDrawAmbient(com.kobaj.math.Constants.my_view_matrix, com.kobaj.math.Constants.my_proj_matrix, com.kobaj.math.Constants.ambient_light);
	}
	
	public void onDrawAmbient(float[] my_view_matrix, float[] my_proj_matrix, AmbientLightShader ambient_light)
	{
		//If on screen, draw.
		if(com.kobaj.math.Functions.onShader(phys_rect_list))
			onSetupAmbient(my_view_matrix, my_proj_matrix, ambient_light);
		
		// Draw the cube.
		onDraw();
	}
}