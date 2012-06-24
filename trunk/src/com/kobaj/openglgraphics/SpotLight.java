package com.kobaj.openglgraphics;

public class SpotLight extends BaseLight<SpotLightShader>
{
	
	//these are in shader coordinates 0 to 1
	public double x_pos = 0.0;
	public double y_pos = 0.0;
	
	public double direction_y = 1.0;
	public double direction_x = 0.0;
	
	//degrees
	public double angle = 45.0;
	
	//intensity/focus
	//1 is right up again the texture, thus the brightest
	//0 is very far away and diffuse
	public double focus = 0.99f;
	
	public SpotLight(SpotLightShader pls_reference, float[] vm_reference)
	{
		super(pls_reference, vm_reference);
		this.brightness = 10.0;
	}
	
	//this is the method that applies position, color, intensity, etc to the shader
	public void applyShaderProperties()
	{
		pls_reference.setPosition(x_pos, y_pos, vm_reference);
		pls_reference.setColor(color);
		pls_reference.my_brightness = brightness;
		pls_reference.my_focus = focus;
		
		pls_reference.my_angle = angle;
		pls_reference.my_direction_x = direction_x;
		pls_reference.my_direction_y = direction_y;
	}
	
	//angle is in degrees
	public void lookAtAngle(double angle)
	{
		double rads = Math.toRadians(angle);
		direction_x = Math.cos(rads);
		direction_y = Math.sin(rads);
	}
}
