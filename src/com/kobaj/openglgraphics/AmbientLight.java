package com.kobaj.openglgraphics;

public class AmbientLight extends BaseLight<AmbientLightShader>
{

	//again, just basically a holder
	public AmbientLight(AmbientLightShader pls_reference, float[] vm_reference)
	{
		super(pls_reference, vm_reference);
	}

	@Override
	public void applyShaderProperties()
	{
		pls_reference.setColor(color);
		pls_reference.my_brightness = brightness;
	}
}
