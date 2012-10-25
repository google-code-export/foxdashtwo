package com.kobaj.opengl;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

import com.kobaj.audio.Music;
import com.kobaj.audio.MusicPlayList;
import com.kobaj.audio.Sound;
import com.kobaj.math.Constants;
import com.kobaj.math.FPSManager;
import com.kobaj.math.Functions;
import com.kobaj.math.Physics;
import com.kobaj.opengldrawable.Text;
import com.kobaj.openglgraphics.AmbientLightShader;

public abstract class MyGLRender implements GLSurfaceView.Renderer
{
	//and fps
	protected FPSManager fps;
	
	public void onSurfaceCreated(GL10 unused, EGLConfig config)
	{
		// Set the background frame color
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		
		// Use culling to remove back faces.
		GLES20.glEnable(GLES20.GL_CULL_FACE);
		
		// disable depth testing
		GLES20.glDisable(GLES20.GL_DEPTH_TEST);
		
		//mmm blending
		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA); // no see thru
		
		// shaders
		Constants.ambient_light = new AmbientLightShader();
		
		//fps
		fps = new FPSManager();
		
		//sound and audio setup
		Constants.music_play_list = new MusicPlayList(new Music());
		Constants.sound = new Sound();

		//physics setup
		Constants.physics = new Physics();
	}
	
	protected abstract void onInitialize();
	
	public void onSurfaceChanged(GL10 unused, int width, int height)
	{
		//gotta reset
		com.kobaj.loader.GLBitmapReader.resetLoadedTextures();
		
		GLES20.glViewport(0, 0, width, height);
		
		Constants.width = width;
		Constants.height = height;
		
		float ratio = (float) (width / height);
		Constants.ratio = ratio;
		Constants.shader_width = ratio * 2.0;
		
		// this projection matrix is applied to object coodinates
		// in the onDrawFrame() method
		
		Matrix.frustumM(Constants.my_proj_matrix, 0, -ratio, ratio, -1, 1, .9999999999f, 2);
		//Matrix.orthoM(Constants.my_proj_matrix, 0, -ratio, ratio, -1, 1, .99999999f, 2);
		Matrix.setLookAtM(Constants.my_view_matrix, 0, //this is the identity...
				0, 0, 0, 
				0f, 0f, -5.0f, 
				0f, 1.0f, 0.0f);
		
		Constants.x_shader_translation = 0;
		Constants.y_shader_translation = 0;
		
		//finish setup
		Constants.text = new Text();
		
		onInitialize();
		
		//after all that, see if we have any opengl errors
		Functions.checkGlError();
	}
	
	public void onSurfaceDestroyed()
	{
		//empty for now
	}
	
	public void onDrawFrame(GL10 unused)
	{	
		onUpdateFrame();
		
		// Redraw background color
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
		
		onDraw();
	}
	
	protected abstract void onDraw();
	
	public void onUpdateFrame()
	{
		//might put fps here.
		fps.onUpdate(SystemClock.uptimeMillis());
		
		onUpdate(fps.getDelta());
	}
	
	protected abstract void onUpdate(double delta);
	
	public void onScreenPause()
	{
		onPause();
	}
	
	protected abstract void onPause();
}