package com.kobaj.screen;

public abstract class BaseScreen implements Runnable
{
	public EnumScreenState current_state = EnumScreenState.not_started;
	
	//constructor should be used to initialize things, but not load them.
	
	public final void onInitialize()
	{
		current_state = EnumScreenState.loading;
		new Thread(this).start();
	}
	
	public final void onUnInitialize()
	{
		current_state = EnumScreenState.unload;
		onUnload();
		current_state = EnumScreenState.stopped;
	}
	
	public void run()
	{
		onLoad();
		current_state = EnumScreenState.running;
	}
	
	//loading things on a seperate thread
	public abstract void onLoad();
	public abstract void onUnload();
	
	//update the main thread login
	public abstract void onUpdate(double delta);
	
	//draw most everything
	public abstract void onDrawObject();
	public abstract void onDrawLight();
	public abstract void onDrawConstant(); //put drawtext here
	
	//draw a loading screen. only time we do math in draw
	public abstract void onDrawLoading(double delta); 
	
	//do something when the system pauses
	public abstract void onPause();
}
