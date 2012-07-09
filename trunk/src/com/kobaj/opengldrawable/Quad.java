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
import com.kobaj.openglgraphics.PointLightShader;
import com.kobaj.openglgraphics.SpotLightShader;

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
	
	// data about the quad
	protected FloatBuffer my_position;
	protected FloatBuffer my_tex_coord;
	
	// camera
	private float[] my_mvp_matrix = new float[16];
	
	// handle to texture
	private int my_texture_data_handle;
	
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
	private void onCreate(int texture_resource, int width, int height)
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
				-tr_x, tr_y, 0.0f, -tr_x, -tr_y, 0.0f, tr_x, tr_y, 0.0f, -tr_x, -tr_y, 0.0f, tr_x, -tr_y, 0.0f, tr_x, tr_y, 0.0f
		
		};
		
		// S, T (or X, Y)
		// Texture coordinate data.
		final float[] cubeTextureCoordinateData = {
				// Front face
				0.0f, 0.0f, 0.0f, -1.0f, 1.0f, 0.0f, 0.0f, -1.0f, 1.0f, -1.0f, 1.0f, 0.0f };
		
		// Initialize the buffers.
		my_position = ByteBuffer.allocateDirect(cubePositionData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		my_position.put(cubePositionData).position(0);
		
		my_tex_coord = ByteBuffer.allocateDirect(cubeTextureCoordinateData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		my_tex_coord.put(cubeTextureCoordinateData).position(0);
		
		//up next setup phys rect list. Just a default. The user can set/add/remove more rectangles as needed.
		phys_rect_list.add(new RectF(-tr_x, tr_y, tr_x, -tr_y));
	}
	
	//methods for
	//calculating stuffs
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
			double rect_half_width = rect.width() / 2.0;
			double rect_half_height = rect.height() / 2.0;
			
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
	
	//also contains method onReSetupPoint
	private <T extends PointLightShader> void onSetupPoint(T point_light)
	{
		// Pass in the light position in eye space.
		GLES20.glUniform3f(point_light.my_light_pos_handle, point_light.my_light_eye_space[0], point_light.my_light_eye_space[1], point_light.my_light_eye_space[2]);
	}

	//also contains method onReSetupSpot
	private <T extends SpotLightShader> void onSetupSpot(T spot_light)
	{
		//pass in the two directions
		GLES20.glUniform3f(spot_light.my_light_dir_handle, (float)spot_light.my_direction_x, (float)spot_light.my_direction_y, 0.0f);
		
		//pass in the angle
		GLES20.glUniform1f(spot_light.my_light_angle_handle, (float)spot_light.my_angle);
	}
	
	//main stuffs
	private void onDraw()
	{
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
	}
	
	//ouside calls
	public void onDrawAmbient(float[] my_view_matrix, float[] my_proj_matrix, AmbientLightShader ambient_light)
	{
		onSetupAmbient(my_view_matrix, my_proj_matrix, ambient_light);
		
		// Draw the cube.
		onDraw();
	}
	
	public void onDrawPoint(float[] my_view_matrix, float[] my_proj_matrix, PointLightShader point_light)
	{
		onSetupAmbient(my_view_matrix, my_proj_matrix, point_light);
		
		onSetupPoint(point_light);
		
		// Draw the cube.
		onDraw();
	}
	
	public void onDrawSpot(float[] my_view_matrix, float[] my_proj_matrix, SpotLightShader spot_light)
	{
		onSetupAmbient(my_view_matrix, my_proj_matrix, spot_light);
		
		onSetupPoint(spot_light);
		
		onSetupSpot(spot_light);
		
		// Draw the cube.
		onDraw();
	}
}