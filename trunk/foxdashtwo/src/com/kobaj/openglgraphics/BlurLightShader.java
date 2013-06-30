package com.kobaj.openglgraphics;

import android.opengl.GLES20;

import com.kobaj.foxdashtwo.R;

public class BlurLightShader extends BaseLightShader
{
	final public int my_offset_handle;
	
	public BlurLightShader()
	{
		super(R.raw.shader_vertex_ambient, R.raw.shader_fragment_compressed);
		my_offset_handle = GLES20.glGetUniformLocation(my_shader, "Offset");
	}
}
