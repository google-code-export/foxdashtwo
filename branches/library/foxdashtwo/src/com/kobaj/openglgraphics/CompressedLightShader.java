package com.kobaj.openglgraphics;

import android.opengl.GLES20;

import com.kobaj.foxdashtwo.R;

public class CompressedLightShader extends BaseLightShader
{
	final public int my_alpha_uniform_handle;
	
	public CompressedLightShader()
	{
		super(R.raw.ambient_vertex_shader, R.raw.compressed_fragment_shader);
		my_alpha_uniform_handle = GLES20.glGetUniformLocation(my_shader, "u_Alpha");
	}
}
