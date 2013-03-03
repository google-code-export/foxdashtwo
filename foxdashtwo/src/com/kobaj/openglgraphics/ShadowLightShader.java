package com.kobaj.openglgraphics;

import android.opengl.GLES20;

import com.kobaj.foxdashtwo.R;
import com.kobaj.openglgraphics.BaseLightShader;

public class ShadowLightShader extends BaseLightShader
{
	final public int my_radius_handle;
	final public int my_shadow_position_handle;
	final public int my_light_uniform_handle;
	
	public ShadowLightShader()
	{
		super(R.raw.ambient_vertex_shader, R.raw.shadow_fragment_shader);
		
		my_radius_handle = GLES20.glGetUniformLocation(my_shader, "u_radius");
		my_shadow_position_handle = GLES20.glGetUniformLocation(my_shader, "u_shadowPosition");
		my_light_uniform_handle = GLES20.glGetUniformLocation(my_shader, "u_lights");
	}
}
