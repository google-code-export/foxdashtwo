package com.kobaj.openglgraphics;

public abstract class BaseLight<T extends BaseLightShader>
{	
	public int color = 0xFFFFFFFF;
	public double brightness = 1.0;
	
	abstract public void applyShaderProperties();
}
