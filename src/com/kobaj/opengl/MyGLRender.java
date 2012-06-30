package com.kobaj.opengl;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

import com.kobaj.math.FPSManager;
import com.kobaj.opengldrawable.Text;
import com.kobaj.openglgraphics.AmbientLightShader;
import com.kobaj.openglgraphics.PointLightShader;
import com.kobaj.openglgraphics.SpotLightShader;

public abstract class MyGLRender implements GLSurfaceView.Renderer
{
	//and fps
	FPSManager fps;
	
	//text mmm
	Text text;
	
	//three shaders
	protected PointLightShader point_light;
	protected AmbientLightShader ambient_light;
	protected SpotLightShader spot_light;
	
	// camera
	protected float[] my_view_matrix = new float[16];
	protected float[] my_proj_matrix = new float[16];
	
	public void onSurfaceCreated(GL10 unused, EGLConfig config)
	{
		//gotta reset
		com.kobaj.loader.GLBitmapReader.resetLoadedTextures();
		
		// Set the background frame color
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		
		// Use culling to remove back faces.
		GLES20.glEnable(GLES20.GL_CULL_FACE);
		
		// disable depth testing
		GLES20.glDisable(GLES20.GL_DEPTH_TEST);
		
		// Enable texture mapping
		GLES20.glEnable(GLES20.GL_TEXTURE_2D);
		
		//mmm blending
		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE);
		
		// shaders
		point_light = new PointLightShader();
		ambient_light = new AmbientLightShader();
		spot_light = new SpotLightShader();
		
		//fps
		fps = new FPSManager();
		
		//text setup
		text = new Text(unused, ambient_light, my_view_matrix, my_proj_matrix);
		
		onInitialize(unused);
	}
	
	abstract void onInitialize(GL10 gl);
	
	public void onSurfaceChanged(GL10 unused, int width, int height)
	{
		
		
		GLES20.glViewport(0, 0, width, height);
		
		float ratio = (float) width / height;
		
		// this projection matrix is applied to object coodinates
		// in the onDrawFrame() method
		Matrix.orthoM(my_proj_matrix, 0, -ratio, ratio, -1, 1, .99999999f, 2);
		Matrix.setLookAtM(my_view_matrix, 0, 
				0, 0, 0, 
				0f, 0f, -5.0f, 
				0f, 1.0f, 0.0f);
	}
	
	public void onSurfaceDestroyed()
	{
		
	}
	
	public void onDrawFrame(GL10 unused)
	{
		onUpdateFrame();
		
		// Redraw background color
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
		
		onDraw();
	}
	
	abstract void onDraw();
	
	public void onUpdateFrame()
	{
		//might put fps here.
		fps.onUpdate(SystemClock.uptimeMillis());
		
		onUpdate(fps.getDelta());
	}
	
	abstract void onUpdate(double delta);
}