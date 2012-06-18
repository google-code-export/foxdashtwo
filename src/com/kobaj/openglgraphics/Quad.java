package com.kobaj.openglgraphics;

//a lot of help from
//http://www.learnopengles.com/android-lesson-one-getting-started/
//https://developer.android.com/resources/tutorials/opengl/opengl-es20.html	

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.Matrix;

public class Quad
{
	//transformation matrix to convert from object to world space 
	private float[] my_model_matrix = new float[16];
		
	//data about the quad
	private FloatBuffer my_position;
	private FloatBuffer my_color;
	private FloatBuffer my_normal;
	private FloatBuffer my_tex_coord;
	
	//camera
	private float[] my_mvp_matrix = new float[16];
	
	//handle to texture
	private int my_texture_data_handle;
	
	public Quad(GL10 gl, int texture_resource)
	{
		// Define points for a cube.		
		
		// X, Y, Z
		final float[] cubePositionData =
			{
				// In OpenGL counter-clockwise winding is default. This means that when we look at a triangle, 
				// if the points are counter-clockwise we are looking at the "front". If not we are looking at
				// the back. OpenGL has an optimization where all back-facing triangles are culled, since they
				// usually represent the backside of an object and aren't visible anyways.
				
				// Front face
				-1.0f, 1.0f, 0.0f,				
				-1.0f, -1.0f, 0.0f,
				1.0f, 1.0f, 0.0f, 
				-1.0f, -1.0f, 0.0f, 				
				1.0f, -1.0f, 0.0f,
				1.0f, 1.0f, 0.0f
				
			};	
		
		// R, G, B, A
		final float[] cubeColorData =
			{				
				// Front face (white)
				1.0f, 1.0f, 1.0f, 1.0f,				
				1.0f, 1.0f, 1.0f, 1.0f,
				1.0f, 1.0f, 1.0f, 1.0f,
				1.0f, 1.0f, 1.0f, 1.0f,				
				1.0f, 1.0f, 1.0f, 1.0f,
				1.0f, 1.0f, 1.0f, 1.0f
				
			};
		
		// X, Y, Z
		// The normal is used in light calculations and is a vector which points
		// orthogonal to the plane of the surface. For a cube model, the normals
		// should be orthogonal to the points of each face.
		final float[] cubeNormalData =
			{												
				// Front face
				0.0f, 0.0f, 1.0f,				
				0.0f, 0.0f, 1.0f,
				0.0f, 0.0f, 1.0f,
				0.0f, 0.0f, 1.0f,				
				0.0f, 0.0f, 1.0f,
				0.0f, 0.0f, 1.0f
			};
		
		// S, T (or X, Y)
		// Texture coordinate data.
		// Because images have a Y axis pointing downward (values increase as you move down the image) while
		// OpenGL has a Y axis pointing upward, we adjust for that here by flipping the Y axis.
		// What's more is that the texture coordinates are the same for every face.
		final float[] cubeTextureCoordinateData =
			{												
				// Front face
				0.0f, 0.0f, 				
				0.0f, -1.0f,
				1.0f, 0.0f,
				0.0f, -1.0f,
				1.0f, -1.0f,
				1.0f, 0.0f
			};
		
		// Initialize the buffers.
		my_position = ByteBuffer.allocateDirect(cubePositionData.length * 4)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();							
		my_position.put(cubePositionData).position(0);		
		
		my_color = ByteBuffer.allocateDirect(cubeColorData.length * 4)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();							
		my_color.put(cubeColorData).position(0);
		
		my_normal = ByteBuffer.allocateDirect(cubeNormalData.length * 4)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();							
		my_normal.put(cubeNormalData).position(0);
		
		my_tex_coord = ByteBuffer.allocateDirect(cubeTextureCoordinateData.length * 4)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();
		my_tex_coord.put(cubeTextureCoordinateData).position(0);
		
		//load dat texture.
		my_texture_data_handle = com.kobaj.loader.GLBitmapReader.loadTexture(gl, com.kobaj.math.Constants.context, texture_resource);
	}
	
	public void onDrawPoint(float[] my_view_matrix, float[] my_proj_matrix, PointLight point_light)
	{
		// Set the active texture unit to texture unit 0.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        
        // Bind the texture to this unit.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, my_texture_data_handle);
        
        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        GLES20.glUniform1i(point_light.my_texture_uniform_handle, 0);  
	
        //set the quad up
        Matrix.setIdentityM(my_model_matrix, 0);
        Matrix.translateM(my_model_matrix, 0, 0.0f, 0.0f, -1.0f);
        //Matrix.rotateM(my_model_matrix, 0, angleInDegrees, 1.0f, 1.0f, 0.0f);
        
        //pass in position information
        my_position.position(0);		
        GLES20.glVertexAttribPointer(point_light.my_position_handle, 3, GLES20.GL_FLOAT, false,
        		0, my_position);        
                
        GLES20.glEnableVertexAttribArray(point_light.my_position_handle);        
        
        // Pass in the color information
        my_color.position(0);
        GLES20.glVertexAttribPointer(point_light.my_color_handle, 4, GLES20.GL_FLOAT, false,
        		0, my_color);        
        
        GLES20.glEnableVertexAttribArray(point_light.my_color_handle);
        
        // Pass in the normal information
        my_normal.position(0);
        GLES20.glVertexAttribPointer(point_light.my_normal_handle, 3, GLES20.GL_FLOAT, false, 
        		0, my_normal);
        
        GLES20.glEnableVertexAttribArray(point_light.my_normal_handle);
        
        // Pass in the texture coordinate information
        my_tex_coord.position(0);
        GLES20.glVertexAttribPointer(point_light.my_tex_coord_handle, 2, GLES20.GL_FLOAT, false, 
        		0, my_tex_coord);
        
        GLES20.glEnableVertexAttribArray(point_light.my_tex_coord_handle);
        
        // This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
        // (which currently contains model * view).
        Matrix.multiplyMM(my_mvp_matrix, 0, my_view_matrix, 0, my_model_matrix, 0);   
        
        // Pass in the modelview matrix.
        GLES20.glUniformMatrix4fv(point_light.my_mv_matrix_handle, 1, false, my_mvp_matrix, 0);                
        
        // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
        // (which now contains model * view * projection).
        Matrix.multiplyMM(my_mvp_matrix, 0, my_proj_matrix, 0, my_mvp_matrix, 0);

        // Pass in the combined matrix.
        GLES20.glUniformMatrix4fv(point_light.my_mvp_matrix_handle, 1, false, my_mvp_matrix, 0);
        
        // Pass in the light position in eye space.        
        GLES20.glUniform3f(point_light.my_light_pos_handle , point_light.my_light_eye_space[0], point_light.my_light_eye_space[1], point_light.my_light_eye_space[2]);
        
        // Draw the cube.
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);    
	}
}
