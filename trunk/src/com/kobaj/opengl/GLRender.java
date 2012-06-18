package com.kobaj.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.kobaj.foxdashtwo.R;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;

public class GLRender implements GLSurfaceView.Renderer
{
	// test
	Quad quad;
	
	// shaders
	private String vertex_shader_code;
	private String fragment_shader_code;
	private int my_program;
	
	// handles to everything in the shader
	private int my_a_position_handle;
	private int my_a_color_handle;
	private int my_a_normal_handle;
	private int my_a_tex_coord_handle;
	
	// texture
	private int my_texture_uniform_handle;
	
	// light stuffs
	/**
	 * Used to hold a light centered on the origin in model space. We need a 4th
	 * coordinate so we can get translations to work when we multiply this by
	 * our transformation matrices.
	 */
	private final float[] mLightPosInModelSpace = new float[] { 0.0f, 0.0f, 0.0f, 6.0f };
	
	/**
	 * Used to hold the current position of the light in world space (after
	 * transformation via model matrix).
	 */
	private final float[] mLightPosInWorldSpace = new float[4];
	
	/**
	 * Used to hold the transformed position of the light in eye space (after
	 * transformation via modelview matrix)
	 */
	private final float[] mLightPosInEyeSpace = new float[4];
	private int my_light_pos_handle;
	private float[] mLightModelMatrix = new float[16];
	
	// camera
	private int my_mvp_matrix_handle;
	private int my_mv_matrix_handle;
	private float[] my_mvp_matrix = new float[16];
	private float[] my_view_matrix = new float[16];
	private float[] my_proj_matrix = new float[16];
	
	public void onSurfaceCreated(GL10 unused, EGLConfig config)
	{
		// Set the background frame color
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		
		// Use culling to remove back faces.
		GLES20.glEnable(GLES20.GL_CULL_FACE);
		
		// Enable depth testing
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		
		// Enable texture mapping
		GLES20.glEnable(GLES20.GL_TEXTURE_2D);
		
		// shaders
		onInitializeShaders();
		
		// test quad
		quad = new Quad(unused, 0);
	}
	
	private void onInitializeShaders()
	{
		// initialize shaders
		vertex_shader_code = com.kobaj.loader.RawTextReader.readRawTextFile(com.kobaj.math.Constants.context, R.raw.vertex_shader);
		fragment_shader_code = com.kobaj.loader.RawTextReader.readRawTextFile(com.kobaj.math.Constants.context, R.raw.fragment_shader);
		
		// shaders
		int vertex_shader = loadShader(GLES20.GL_VERTEX_SHADER, vertex_shader_code);
		int fragment_shader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragment_shader_code);
		
		my_program = GLES20.glCreateProgram(); // create empty OpenGL Program
		GLES20.glAttachShader(my_program, vertex_shader); // add the vertex
															// shader
															// to program
		GLES20.glAttachShader(my_program, fragment_shader); // add the fragment
															// shader to program
		GLES20.glLinkProgram(my_program); // creates OpenGL program executables
		
		// get handle to the vertex shader's vPosition member
		my_a_position_handle = GLES20.glGetAttribLocation(my_program, "a_Position");
		my_a_color_handle = GLES20.glGetAttribLocation(my_program, "a_Color");
		my_a_normal_handle = GLES20.glGetAttribLocation(my_program, "a_Normal");
		my_a_tex_coord_handle = GLES20.glGetAttribLocation(my_program, "a_TexCoordinate");
		
		my_mvp_matrix_handle = GLES20.glGetUniformLocation(my_program, "u_MVPMatrix");
		my_mv_matrix_handle = GLES20.glGetUniformLocation(my_program, "u_MVMatrix");
		my_light_pos_handle = GLES20.glGetUniformLocation(my_program, "u_LightPos");
		my_texture_uniform_handle = GLES20.glGetUniformLocation(my_program, "u_Texture");
	}
	
	private int loadShader(int type, String shaderCode)
	{
		
		// create a vertex shader type (GLES20.GL_VERTEX_SHADER)
		// or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
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
				Log.e("kobaj_shader_error", "Error compiling shader: " + GLES20.glGetShaderInfoLog(shader));
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
	
	public void onSurfaceChanged(GL10 unused, int width, int height)
	{
		GLES20.glViewport(0, 0, width, height);
		
		float ratio = (float) width / height;
		
		// this projection matrix is applied to object coodinates
		// in the onDrawFrame() method
		Matrix.orthoM(my_proj_matrix, 0, -ratio, ratio, -1, 1, .99999999f, 2);
		Matrix.setLookAtM(my_view_matrix, 0, 0, 0, 0, 0f, 0f, -5.0f, 0f, 1.0f, 0.0f);
	}
	
	public void onDrawFrame(GL10 unused)
	{
		// Redraw background color
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
		
		// Add program to OpenGL environment
		GLES20.glUseProgram(my_program);
		
		// light
		// Calculate position of the light. Rotate and then push into the
		// distance.
		// Do a complete rotation every 10 seconds.
		long time = SystemClock.uptimeMillis() % 10000L;
		float angleInDegrees = (360.0f / 10000.0f) * ((int) time);
		
		Matrix.setIdentityM(mLightModelMatrix, 0);
		Matrix.translateM(mLightModelMatrix, 0, 0.0f, 0.0f, -5.0f);
		Matrix.rotateM(mLightModelMatrix, 0, angleInDegrees, 0.0f, 1.0f, 0.0f);
		Matrix.translateM(mLightModelMatrix, 0, 0.0f, 0.0f, 2.0f);
		
		Matrix.multiplyMV(mLightPosInWorldSpace, 0, mLightModelMatrix, 0, mLightPosInModelSpace, 0);
		Matrix.multiplyMV(mLightPosInEyeSpace, 0, my_view_matrix, 0, mLightPosInWorldSpace, 0);
		
		//mmm blending
		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE);
		
		// draw a quad
		quad.onDraw(my_view_matrix, my_proj_matrix, my_mvp_matrix, mLightPosInEyeSpace, my_texture_uniform_handle, my_mv_matrix_handle, my_mvp_matrix_handle, my_a_position_handle, my_a_color_handle,
				my_a_normal_handle, my_a_tex_coord_handle, my_light_pos_handle);
	
	}
}
