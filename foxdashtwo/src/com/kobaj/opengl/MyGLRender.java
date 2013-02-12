package com.kobaj.opengl;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;

import com.kobaj.audio.Music;
import com.kobaj.audio.MusicPlayer;
import com.kobaj.audio.Sound;
import com.kobaj.math.Constants;
import com.kobaj.math.FPSManager;
import com.kobaj.math.Functions;
import com.kobaj.math.Physics;
import com.kobaj.opengldrawable.Text;
import com.kobaj.opengldrawable.Quad.QuadRenderShell;
import com.kobaj.openglgraphics.AmbientLightShader;
import com.kobaj.openglgraphics.BlurLightShader;
import com.kobaj.openglgraphics.CompressedLightShader;
import com.kobaj.openglgraphics.GodRayLightShader;

public abstract class MyGLRender implements GLSurfaceView.Renderer
{
	// and fps
	protected FPSManager fps;
	
	private int exception_count = 0;
	final private int exception_limit = 10;
	
	public void onSurfaceCreated(GL10 unused, EGLConfig config)
	{
		// Set the background frame color
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		
		// Use culling to remove back faces.
		GLES20.glEnable(GLES20.GL_CULL_FACE);
		
		// disable depth testing
		GLES20.glDisable(GLES20.GL_DEPTH_TEST);
		// GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		
		// mmm blending
		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA); // no see thru
		
		// shaders
		Constants.ambient_light = new AmbientLightShader();
		Constants.compressed_light = new CompressedLightShader();
		Constants.god_ray_light = new GodRayLightShader();
		Constants.blur_light = new BlurLightShader();
		
		// fps
		fps = new FPSManager();
		
		// sound and audio setup
		Constants.music_player = new MusicPlayer(new Music());
		Constants.sound = new Sound();
		
		// physics setup
		Constants.physics = new Physics();
	}
	
	protected abstract void onInitialize();
	
	public void onSurfaceChanged(GL10 unused, int width, int height)
	{
		// gotta reset
		exception_count = 0;
		
		// clear out the memory
		com.kobaj.loader.GLBitmapReader.resetLoadedTextures();
		QuadRenderShell.program_update = true;
		
		// set our window
		GLES20.glViewport(0, 0, width, height);
		
		Constants.width = width;
		Constants.height = height;
		
		float ratio = (float) (width) / (float) (height);
		Constants.ratio = ratio;
		Constants.shader_width = ratio * 2.0;
		
		// this projection matrix is applied to object coodinates
		// in the onDrawFrame() method
		
		// we use a frustrum because the game utilizes 'zoom' effects via the camera
		Matrix.frustumM(Constants.my_proj_matrix, 0, -ratio, ratio, -1, 1, .9999999999f, 2);
		// Matrix.orthoM(Constants.my_proj_matrix, 0, -ratio, ratio, -1, 1, .99999999f, 2);
		Matrix.setLookAtM(Constants.my_view_matrix, 0, // this is the identity...
				0, 0, 0, // eye position/look at
				0f, 0f, -5.0f, // center/camera position
				0f, 1.0f, 0.0f); // up vector
		
		// multiply
		Matrix.multiplyMM(Constants.my_ip_matrix, 0, Constants.my_proj_matrix, 0, Constants.identity_matrix, 0);
		
		Constants.x_shader_translation = 0;
		Constants.y_shader_translation = 0;
		
		// finish setup
		Constants.text = new Text();
		
		onInitialize();
		
		// after all that, see if we have any opengl errors
		Functions.checkGlError();
	}
	
	public void onSurfaceDestroyed()
	{
		Constants.text.onUnInitialize();
	}
	
	public void onDrawFrame(GL10 unused)
	{
		// a very interesting bug requires this try catch. Allow me to explain.
		// when the app resumes from a non application.finish() state
		// it crashes with a NullPointerException. No stack trace
		// I debugged this for an hour, and could not find a null pointer
		// whats interesting, if you catch the first NPE, then let the system continue
		// it works perfectly fine. Go figure.
		
		try
		{
			onUpdateFrame();
			
			// Redraw background color
			GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
			
			// calculate vp matrix
			Matrix.multiplyMM(Constants.my_vp_matrix, 0, Constants.my_proj_matrix, 0, Constants.my_view_matrix, 0);
			
			// draw everything!!
			onDraw();
		}
		catch (NullPointerException e)
		{
			exception_count++;
			Log.e("Draw Frame Exception", e.toString());
			
			try
			{
				Thread.sleep(Constants.exception_timeout);
			}
			catch (InterruptedException e1)
			{
				Log.e("Draw Frame Timeout", e1.toString());
			}
			
			if (exception_count > exception_limit)
				throw e;
		}
	}
	
	protected abstract void onDraw();
	
	public void onUpdateFrame()
	{
		// might put fps here.
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
