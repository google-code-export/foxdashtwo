package com.kobaj.openglgraphics;

public class PointLight extends BaseLight<PointLightShader>
{
	
	//these are in shader coordinates 0 to 1
	public double x_pos = 0.0;
	public double y_pos = 0.0;
	
	//intensity/focus
	//1 is right up again the texture, thus the brightest
	//0 is very far away and diffuse
	public double focus = 0.0f;
	
	public PointLight(PointLightShader pls_reference, float[] vm_reference)
	{
		super(pls_reference, vm_reference);
	}
	
	//this is the method that applies position, color, intensity, etc to the shader
	public void applyShaderProperties()
	{
		pls_reference.setPosition(x_pos, y_pos, vm_reference);
		pls_reference.setColor(color);
		pls_reference.my_brightness = brightness;
		pls_reference.my_focus = focus;
	}
}
