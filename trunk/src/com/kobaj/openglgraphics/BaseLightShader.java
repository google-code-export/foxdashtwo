package com.kobaj.openglgraphics;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.graphics.Color;
import android.opengl.GLES20;
import android.util.Log;

public abstract class BaseLightShader
{
	// shader (point light)
	public int my_shader;
	
	// handles to everything in the shader
	public int my_position_handle;
	public int my_color_handle;
	public int my_tex_coord_handle;
	public int my_texture_uniform_handle;
	public int my_mvp_matrix_handle;
	public int my_mv_matrix_handle;
	public int my_brightness_handle;
	
	//effects
	public double my_brightness;
	public FloatBuffer my_color;
	
	public BaseLightShader()
	{
		my_color = ByteBuffer.allocateDirect(6/*rows*/ * 4/*colums*/ * 4/*size of float*/).order(ByteOrder.nativeOrder()).asFloatBuffer();	
		
		setColor(Color.WHITE);
	}
	
	public void setColor(int color)
	{
		int alpha = Color.alpha(color);
		int red = Color.red(color);
		int blue = Color.blue(color);
		int green = Color.green(color);
		
		float tr_alpha = (float) com.kobaj.math.Functions.byteToShader(alpha);
		float tr_red = (float) com.kobaj.math.Functions.byteToShader(red);
		float tr_green = (float) com.kobaj.math.Functions.byteToShader(green);
		float tr_blue = (float) com.kobaj.math.Functions.byteToShader(blue);
		
		// R, G, B, A
				final float[] cubeColorData =
					{				
						// Front face (white)
						tr_red, tr_green, tr_blue, tr_alpha,				
						tr_red, tr_green, tr_blue, tr_alpha,
						tr_red, tr_green, tr_blue, tr_alpha,
						tr_red, tr_green, tr_blue, tr_alpha,				
						tr_red, tr_green, tr_blue, tr_alpha,
						tr_red, tr_green, tr_blue, tr_alpha
						
					};
										
		my_color.put(cubeColorData).position(0);
	}
	
	protected void onInitializeShaders(int r_vertex_shader, int r_fragment_shader)
	{
		// initialize shaders
		String vertex_shader_code = com.kobaj.loader.RawTextReader.readRawTextFile(com.kobaj.math.Constants.context, r_vertex_shader);
		String fragment_shader_code = com.kobaj.loader.RawTextReader.readRawTextFile(com.kobaj.math.Constants.context, r_fragment_shader);
		
		// shaders
		int vertex_shader = loadShader(GLES20.GL_VERTEX_SHADER, vertex_shader_code);
		int fragment_shader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragment_shader_code);
		
		my_shader = GLES20.glCreateProgram(); // create empty OpenGL Program
		GLES20.glAttachShader(my_shader, vertex_shader); // add the vertex
															// shader
															// to program
		GLES20.glAttachShader(my_shader, fragment_shader); // add the fragment
															// shader to program
		GLES20.glLinkProgram(my_shader); // creates OpenGL program executables
		
		// get handle to the vertex shader's vPosition member
		my_position_handle = GLES20.glGetAttribLocation(my_shader, "a_Position");
		my_color_handle = GLES20.glGetAttribLocation(my_shader, "a_Color");
		my_tex_coord_handle = GLES20.glGetAttribLocation(my_shader, "a_TexCoordinate");
		
		my_brightness_handle = GLES20.glGetUniformLocation(my_shader, "u_Brightness");
		
		my_mvp_matrix_handle = GLES20.glGetUniformLocation(my_shader, "u_MVPMatrix");
		my_mv_matrix_handle = GLES20.glGetUniformLocation(my_shader, "u_MVMatrix");
		my_texture_uniform_handle = GLES20.glGetUniformLocation(my_shader, "u_Texture");
	}
	
	private int loadShader(int type, String shaderCode)
	{
		int shader = GLES20.glCreateShader(type);
		
		if (shader != 0)
		{
			// add the source code to the shader and compile it
			GLES20.glShaderSource(shader, shaderCode);
			GLES20.glCompileShader(shader);
			
			// Get the compilation status.
			final int[] compileStatus = new int[1];
			GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
			
			// If the compilation failed, delete the shader.
			if (compileStatus[0] == 0)
			{
				//small bit of info
			
				String type_name = "Vertex Shader";
				if(type == GLES20.GL_FRAGMENT_SHADER)
					type_name = "Fragment Shader";
				
				//returns absolutely nothing :(.
				String infoLog = GLES20.glGetShaderInfoLog( shader );
					
				Log.e("kobaj_shader_error", "Error compiling shader: " + type_name + " " + infoLog);
				
				GLES20.glDeleteShader(shader);
				shader = 0;
			}
		}
		
		if (shader == 0)
		{
			throw new RuntimeException("Error creating shader.");
		}
		
		return shader;
	}
}