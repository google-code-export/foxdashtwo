package com.kobaj.screen;

public abstract class BaseScreen implements Runnable
{
	public EnumScreenState current_state = EnumScreenState.not_started;
	
	//constructor should be used to initialize things, but not load them.
	
	public void onInitialize()
	{
		new Thread(this).start();
	}
	
	public void run()
	{
		current_state = EnumScreenState.loading;
		onLoad();
		current_state = EnumScreenState.running;
	}
	
	//loading things on a seperate thread
	public abstract void onLoad();
	
	//update the main thread login
	public abstract void onUpdate(double delta);
	
	//draw most everything
	public abstract void onDrawObject();
	public abstract void onDrawLight();
	public abstract void onDrawConstant(); //put drawtext here
	
	//draw a loading screen. only time we do math in draw
	public abstract void onDrawLoading(double delta); 
}
