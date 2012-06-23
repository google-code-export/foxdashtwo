package com.kobaj.openglgraphics;

public class PointLight
{
	private PointLightShader pls_reference;
	private final float[] vm_reference;
	
	//these are in shader coordinates 0 to 1
	public double x_pos = 0.0;
	public double y_pos = 0.0;
	
	public int color = 0xFFFFFFFF;
	
	//TODO intensity/focus
	
	public PointLight(PointLightShader pls_reference, float[] vm_reference)
	{
		this.pls_reference = pls_reference;
		this.vm_reference = vm_reference;
	}
	
	//this is the method that applies position, color, intensity, etc to the shader
	public void applyShaderProperties()
	{
		pls_reference.setPosition(x_pos, y_pos, vm_reference);
		pls_reference.setColor(color);
	}
}
