package com.kobaj.opengl;

import android.content.Context;
import android.opengl.GLSurfaceView;

public class MyGLSurfaceView extends GLSurfaceView {

	public com.kobaj.opengl.MyGame my_game;
	
    public MyGLSurfaceView(Context context){
        super(context);
        
        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);
        // Set the Renderer for drawing on the GLSurfaceView
        my_game = new com.kobaj.opengl.MyGame();

        setRenderer(my_game);
    }
    
    public void onScreenPause()
    {
		super.onPause();
    	my_game.onScreenPause();
    }
}
