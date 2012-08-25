package com.kobaj.screen;

public abstract class BaseScreen
{
	public abstract void onInitialize();
	public abstract void onUpdate(double delta);
	public abstract void onDrawObject();
	public abstract void onDrawLight();
}
