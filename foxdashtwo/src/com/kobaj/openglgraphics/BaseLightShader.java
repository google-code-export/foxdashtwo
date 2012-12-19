package com.kobaj.openglgraphics;

import android.opengl.GLES20;
import android.util.Log;

public abstract class BaseLightShader
{
	// shader (point light)
	final public int my_shader;
	
	// handles to everything in the shader
	final public int my_position_handle;
	final public int my_color_handle;
	final public int my_tex_coord_handle;
	final public int my_texture_uniform_handle;
	final public int my_mvp_matrix_handle;
	
	protected BaseLightShader(int r_vertex_shader, int r_fragment_shader)
	{
		// initialize shaders
		String vertex_shader_code = com.kobaj.loader.RawTextReader.readRawTextFile(com.kobaj.math.Constants.context, r_vertex_shader);
		String fragment_shader_code = com.kobaj.loader.RawTextReader.readRawTextFile(com.kobaj.math.Constants.context, r_fragment_shader);
		
		// shaders
		int vertex_shader = loadShader(GLES20.GL_VERTEX_SHADER, vertex_shader_code);
		int fragment_shader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragment_shader_code);
		
		my_shader = GLES20.glCreateProgram(); // create empty OpenGL Program
		GLES20.glAttachShader(my_shader, vertex_shader);
		GLES20.glAttachShader(my_shader, fragment_shader);
		GLES20.glLinkProgram(my_shader); // creates OpenGL program executables
		
		// get handle to the vertex shader's vPosition member
		my_position_handle = GLES20.glGetAttribLocation(my_shader, "a_Position");
		GLES20.glEnableVertexAttribArray(my_position_handle);
		my_tex_coord_handle = GLES20.glGetAttribLocation(my_shader, "a_TexCoordinate");
		GLES20.glEnableVertexAttribArray(my_tex_coord_handle);
		
		my_color_handle = GLES20.glGetUniformLocation(my_shader, "u_Color");
		
		my_mvp_matrix_handle = GLES20.glGetUniformLocation(my_shader, "u_MVPMatrix");
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
				// small bit of info
				
				String type_name = "Vertex Shader";
				if (type == GLES20.GL_FRAGMENT_SHADER)
					type_name = "Fragment Shader";
				
				// returns absolutely nothing :(.
				String infoLog = GLES20.glGetShaderInfoLog(shader);
				
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
