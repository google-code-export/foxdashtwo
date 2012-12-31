package com.kobaj.openglgraphics;

import com.kobaj.foxdashtwo.R;

public class GodRayLightShader extends BaseLightShader
{
	public GodRayLightShader()
	{
		super(R.raw.ambient_vertex_shader, R.raw.god_ray_fragment_shader);
	}
}
