package com.kobaj.openglgraphics;

public class AmbientLight extends BaseLight<AmbientLightShader>
{
	@Override
	public void applyShaderProperties()
	{
		com.kobaj.math.Constants.ambient_light.setColor(color);
		com.kobaj.math.Constants.ambient_light.my_brightness = brightness;
	}
}
