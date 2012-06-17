package com.kobaj.input;

import android.view.KeyEvent;
import android.view.MotionEvent;

public class InputManager
{
	public final int fingerCount = 4;
	
	private float[] x;
	private float[] y;
	
	private float[] oldx;
	private float[] oldy;
	
	private float[] deltax;
	private float[] deltay;
	
	private boolean[] oldpressed;
	private boolean[] pressed;
	
	private boolean[] dpads;
	private boolean[] olddpads;
	
	public InputManager()
	{
		x = new float[fingerCount];
		y = new float[fingerCount];
		
		oldx = new float[fingerCount];
		oldy = new float[fingerCount];
		
		deltax = new float[fingerCount];
		deltay = new float[fingerCount];
		
		oldpressed = new boolean[fingerCount];
		pressed = new boolean[fingerCount];
		
		dpads = new boolean[KeyCodes.values().length];
		olddpads = new boolean[KeyCodes.values().length];
	}
	
	public void eventUpdateUp(int i, KeyEvent event)
	{
		if (i == KeyEvent.KEYCODE_DPAD_CENTER)
		{
			olddpads[KeyCodes.center.ordinal()] = true;
			dpads[KeyCodes.center.ordinal()] = false;
		}
		else if (i == KeyEvent.KEYCODE_DPAD_LEFT)
		{
			olddpads[KeyCodes.left.ordinal()] = true;
			dpads[KeyCodes.left.ordinal()] = false;
		}
		else if (i == KeyEvent.KEYCODE_DPAD_RIGHT)
		{
			olddpads[KeyCodes.right.ordinal()] = true;
			dpads[KeyCodes.right.ordinal()] = false;
		}
		else if (i == KeyEvent.KEYCODE_DPAD_UP)
		{
			olddpads[KeyCodes.up.ordinal()] = true;
			dpads[KeyCodes.up.ordinal()] = false;
		}
		else if (i == KeyEvent.KEYCODE_DPAD_DOWN)
		{
			olddpads[KeyCodes.down.ordinal()] = true;
			dpads[KeyCodes.down.ordinal()] = false;
		}
	}
	
	public void eventUpdateDown(int i, KeyEvent event)
	{
		if (i == KeyEvent.KEYCODE_DPAD_CENTER)
		{
			olddpads[KeyCodes.center.ordinal()] = false;
			dpads[KeyCodes.center.ordinal()] = true;
		}
		else if (i == KeyEvent.KEYCODE_DPAD_LEFT)
		{
			olddpads[KeyCodes.left.ordinal()] = false;
			dpads[KeyCodes.left.ordinal()] = true;
		}
		else if (i == KeyEvent.KEYCODE_DPAD_RIGHT)
		{
			olddpads[KeyCodes.right.ordinal()] = false;
			dpads[KeyCodes.right.ordinal()] = true;
		}
		else if (i == KeyEvent.KEYCODE_DPAD_UP)
		{
			olddpads[KeyCodes.up.ordinal()] = false;
			dpads[KeyCodes.up.ordinal()] = true;
		}
		else if (i == KeyEvent.KEYCODE_DPAD_DOWN)
		{
			olddpads[KeyCodes.down.ordinal()] = false;
			dpads[KeyCodes.down.ordinal()] = true;
		}
	}
	
	public void eventUpdate(MotionEvent event)
	{
		int action = event.getAction();
		
		int ptrId = event.getPointerId(0);
		if (event.getPointerCount() > 1)
			ptrId = (action & MotionEvent.ACTION_POINTER_ID_MASK) >> MotionEvent.ACTION_POINTER_ID_SHIFT;
		
		action = action & MotionEvent.ACTION_MASK;
		if (action < 7 && action > 4)
			action = action - 5;
		
		if (action == MotionEvent.ACTION_DOWN)
		{
			for (int i = 0; i < event.getPointerCount(); i++)
			{
				int id = event.getPointerId(i);
				
				x[id] = event.getX(i);
				y[id] = event.getY(i);
			}
			
			oldpressed[ptrId] = false;
			pressed[ptrId] = true;
		}
		if (action == MotionEvent.ACTION_MOVE)
		{
			for (int i = 0; i < event.getPointerCount(); i++)
			{
				int id = event.getPointerId(i);
				
				oldx[id] = x[id];
				oldy[id] = y[id];
				
				x[id] = event.getX(i);
				y[id] = event.getY(i);
				
				deltax[id] = x[id] - oldx[id];
				deltay[id] = y[id] - oldy[id];
			}
		}
		if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL)
		{
			oldpressed[ptrId] = true;
			pressed[ptrId] = false;
			
			if (event.getPointerCount() == 1)
				for (int i = 0; i < fingerCount; i++)
					pressed[i] = false;
		}
	}
	
	public float getX(int index)
	{
		return x[index];
	}
	
	public float getY(int index)
	{
		return y[index];
	}
	
	public float getOldx(int index)
	{
		return oldx[index];
	}
	
	public float getOldy(int index)
	{
		return oldy[index];
	}
	
	public float getDeltax(int index)
	{
		return deltax[index];
	}
	
	public float getDeltay(int index)
	{
		return deltay[index];
	}
	
	public boolean getTouched(int index)
	{
		return pressed[index];
	}
	
	public boolean getKeyPressed(int index)
	{
		if (dpads[index] && !olddpads[index])
		{
			olddpads[index] = true;
			return true;
		}
		
		return false;
	}
	
	public boolean getKeyReleased(int index)
	{
		if (!dpads[index] && olddpads[index])
		{
			olddpads[index] = false;
			return true;
		}
		
		return false;
	}
	
	public boolean getPressed(int index)
	{
		if (pressed[index] && !oldpressed[index])
		{
			oldpressed[index] = true;
			
			return true;
		}
		
		return false;
	}
	
	public boolean getReleased(int index)
	{
		if (!pressed[index] && oldpressed[index])
		{
			oldpressed[index] = false;
			return true;
		}
		
		return false;
	}
}
