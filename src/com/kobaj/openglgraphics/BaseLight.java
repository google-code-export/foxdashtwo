package com.kobaj.openglgraphics;

public abstract class BaseLight<T extends BaseLightShader>
{
	//point light shader
	protected T pls_reference;
	//view matrix
	protected final float[] vm_reference;
	
	public int color = 0xFFFFFFFF;
	public double brightness = 1.0;
	
	public BaseLight(T pls_reference, float[] vm_reference)
	{
		this.pls_reference = pls_reference;
		this.vm_reference = vm_reference;
	}
	
	abstract public void applyShaderProperties();
}
