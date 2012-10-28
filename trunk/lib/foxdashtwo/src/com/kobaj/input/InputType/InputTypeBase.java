package com.kobaj.input.InputType;


public abstract class InputTypeBase
{
	public abstract boolean getPressedJump();
	public abstract boolean getReleasedJump();
	public abstract boolean getTouchedLeft();
	public abstract boolean getTouchedRight();
	
	public boolean getLeftOrRight()
	{
		return (getTouchedLeft() || getTouchedRight());
	}
	public boolean getLeftXorRight()
	{
		return (getTouchedLeft() != getTouchedRight());
	}
	
	public abstract void onInitialize();
	public abstract void onDraw();
}
