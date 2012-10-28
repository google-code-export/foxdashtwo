package com.kobaj.openglgraphics;

import com.kobaj.foxdashtwo.R;
import com.kobaj.openglgraphics.BaseLightShader;

public class AmbientLightShader extends BaseLightShader
{
	//really just allowing us to instantiate a base light.
	public AmbientLightShader()
	{
		super(R.raw.ambient_vertex_shader, R.raw.ambient_fragment_shader);
	}
}
