package com.kobaj.opengldrawable;

//a lot of help from
//http://www.learnopengles.com/android-lesson-one-getting-started/
//https://developer.android.com/resources/tutorials/opengl/opengl-es20.html	

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import com.kobaj.loader.GLLoadedTexture;
import com.kobaj.openglgraphics.BaseLightShader;
import com.kobaj.openglgraphics.PointLightShader;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.Matrix;

public class Quad
{
	// transformation matrix to convert from object to world space
	private float[] my_model_matrix = new float[16];
	// these are in shader coordinates 0 to 1
	public double x_pos = 0.0;
	public double y_pos = 0.0;
	
	//TODO add a bounding rectangle for stuffs
	//begin by holding these
	public int width;
	public int height;
	public double shader_width;
	public double shader_height;
	
	// data about the quad
	private FloatBuffer my_position;
	private FloatBuffer my_normal;
	private FloatBuffer my_tex_coord;
	
	// camera
	private float[] my_mvp_matrix = new float[16];
	
	// handle to texture
	private int my_texture_data_handle;
	
	public Quad(GL10 gl, int texture_resource)
	{
		this(gl, texture_resource, -1, -1);
	}
	
	public Quad(GL10 gl, int texture_resource, int width, int height)
	{
		// load dat texture.
		my_texture_data_handle = com.kobaj.loader.GLBitmapReader.loadTextureFromResource(gl, com.kobaj.math.Constants.context, texture_resource);
		onCreate(gl, texture_resource, width, height);
	}
	
	public Quad(GL10 gl, int texture_resource, Bitmap bmp)
	{
		this(gl, texture_resource, bmp, -1, -1);
	}
	
	public Quad(GL10 gl, int texture_resource, Bitmap bmp, int width, int height)
	{
		my_texture_data_handle = com.kobaj.loader.GLBitmapReader.loadTextureFromBitmap(gl, com.kobaj.math.Constants.context, texture_resource, bmp);
		onCreate(gl, texture_resource, width, height);
	}
	
	private void onCreate(GL10 gl, int texture_resource, int width, int height)
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
		float tr_x = (float) com.kobaj.math.Functions.screenWidthToShaderWidth(width);
		float tr_y = (float) com.kobaj.math.Functions.screenHeightToShaderHeight(height);
		
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
		
		// X, Y, Z
		// The normal is used in light calculations and is a vector which points
		// orthogonal to the plane of the surface. For a cube model, the normals
		// should be orthogonal to the points of each face.
		final float[] cubeNormalData = {
				// Front face
				0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f };
		
		// S, T (or X, Y)
		// Texture coordinate data.
		final float[] cubeTextureCoordinateData = {
				// Front face
				0.0f, 0.0f, 0.0f, -1.0f, 1.0f, 0.0f, 0.0f, -1.0f, 1.0f, -1.0f, 1.0f, 0.0f };
		
		// Initialize the buffers.
		my_position = ByteBuffer.allocateDirect(cubePositionData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		my_position.put(cubePositionData).position(0);
		
		my_normal = ByteBuffer.allocateDirect(cubeNormalData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		my_normal.put(cubeNormalData).position(0);
		
		my_tex_coord = ByteBuffer.allocateDirect(cubeTextureCoordinateData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		my_tex_coord.put(cubeTextureCoordinateData).position(0);
	}
	
	//methods for
	//calculating stuffs
	//these x and y are in shader space 0 to 1
	public void setPos(double x, double y, DrawFrom where)
	{
		if(where == DrawFrom.top_left)
		{
			//positive x
			//negative y
			this.x_pos = x + shader_width / 2.0;
			this.y_pos = y - shader_height / 2.0;	
		}
		else if(where == DrawFrom.top_right)
		{
			this.x_pos = x - shader_width / 2.0;
			this.y_pos = y - shader_height / 2.0;
		}
		else if(where == DrawFrom.bottom_left)
		{
			this.x_pos = x + shader_width / 2.0;
			this.y_pos = y + shader_height / 2.0;	
		}
		else if(where == DrawFrom.bottom_right)
		{

			this.x_pos = x - shader_width / 2.0;
			this.y_pos = y + shader_height / 2.0;			
		}
		else
		{
			x_pos = x;
			y_pos = y;
		}
	} 
	
	// methods for
	// drawing stuffs
	private void onSetupBase(float[] my_view_matrix, float[] my_proj_matrix, BaseLightShader ambient_light)
	{
		// Set the active texture unit to texture unit 0.
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		
		// Bind the texture to this unit.
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, my_texture_data_handle);
		
		// Tell the texture uniform sampler to use this texture in the shader by
		// binding to texture unit 0.
		GLES20.glUniform1i(ambient_light.my_texture_uniform_handle, 0);
		
		// set the quad up
		Matrix.setIdentityM(my_model_matrix, 0);
		Matrix.translateM(my_model_matrix, 0, (float) x_pos, (float) y_pos, -1.0f);
		// Matrix.rotateM(my_model_matrix, 0, angleInDegrees, 1.0f, 1.0f, 0.0f);
		
		// pass in position information
		my_position.position(0);
		GLES20.glVertexAttribPointer(ambient_light.my_position_handle, 3, GLES20.GL_FLOAT, false, 0, my_position);
		GLES20.glEnableVertexAttribArray(ambient_light.my_position_handle);
		
		// Pass in the color information
		ambient_light.my_color.position(0);
		GLES20.glVertexAttribPointer(ambient_light.my_color_handle, 4, GLES20.GL_FLOAT, false, 0, ambient_light.my_color);
		GLES20.glEnableVertexAttribArray(ambient_light.my_color_handle);
		
		// Pass in the texture coordinate information
		my_tex_coord.position(0);
		GLES20.glVertexAttribPointer(ambient_light.my_tex_coord_handle, 2, GLES20.GL_FLOAT, false, 0, my_tex_coord);
		GLES20.glEnableVertexAttribArray(ambient_light.my_tex_coord_handle);
		
		// pass in the brightness
		GLES20.glUniform1f(ambient_light.my_brightness_handle, ambient_light.my_brightness);
		
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
	
	public void onDrawAmbient(float[] my_view_matrix, float[] my_proj_matrix, BaseLightShader ambient_light)
	{
		onSetupBase(my_view_matrix, my_proj_matrix, ambient_light);
		
		// Draw the cube.
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
	}
	
	public void onDrawPoint(float[] my_view_matrix, float[] my_proj_matrix, PointLightShader point_light)
	{
		onSetupBase(my_view_matrix, my_proj_matrix, point_light);
		
		// Pass in the normal information
		my_normal.position(0);
		GLES20.glVertexAttribPointer(point_light.my_normal_handle, 3, GLES20.GL_FLOAT, false, 0, my_normal);
		GLES20.glEnableVertexAttribArray(point_light.my_normal_handle);
		
		// Pass in the light position in eye space.
		GLES20.glUniform3f(point_light.my_light_pos_handle, point_light.my_light_eye_space[0], point_light.my_light_eye_space[1], point_light.my_light_eye_space[2]);
		
		// Draw the cube.
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
	}
}
