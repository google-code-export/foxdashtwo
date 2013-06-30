package com.kobaj.openglgraphics;

import com.kobaj.foxdashtwo.R;

public class GodRayLightShader extends BaseLightShader
{
	public GodRayLightShader()
	{
		super(R.raw.shader_vertex_ambient, R.raw.shader_fragment_god_ray);
	}
}
