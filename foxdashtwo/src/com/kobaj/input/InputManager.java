package com.kobaj.input;

import android.view.KeyEvent;
import android.view.MotionEvent;

import com.kobaj.math.Functions;

public class InputManager
{
	public final int finger_count = 4;
	
	private float[] x;
	private float[] y;
	
	private float[] x_old;
	private float[] y_old;
	
	private float[] x_delta;
	private float[] y_delta;
	
	private boolean[] old_pressed;
	private boolean[] pressed;
	
	private boolean[] old_dpads;
	private boolean[] dpads;
	
	public InputManager()
	{
		x = new float[finger_count];
		y = new float[finger_count];
		
		x_old = new float[finger_count];
		y_old = new float[finger_count];
		
		x_delta = new float[finger_count];
		y_delta = new float[finger_count];
		
		old_pressed = new boolean[finger_count];
		pressed = new boolean[finger_count];
		
		// this needs some work
		dpads = new boolean[EnumKeyCodes.values().length];
		old_dpads = new boolean[EnumKeyCodes.values().length];
	}
	
	public void eventUpdateUp(int i, KeyEvent event)
	{
		if (i == KeyEvent.KEYCODE_DPAD_CENTER)
		{
			old_dpads[EnumKeyCodes.center.ordinal()] = true;
			dpads[EnumKeyCodes.center.ordinal()] = false;
		}
		else if (i == KeyEvent.KEYCODE_DPAD_LEFT)
		{
			old_dpads[EnumKeyCodes.left.ordinal()] = true;
			dpads[EnumKeyCodes.left.ordinal()] = false;
		}
		else if (i == KeyEvent.KEYCODE_DPAD_RIGHT)
		{
			old_dpads[EnumKeyCodes.right.ordinal()] = true;
			dpads[EnumKeyCodes.right.ordinal()] = false;
		}
		else if (i == KeyEvent.KEYCODE_DPAD_UP)
		{
			old_dpads[EnumKeyCodes.up.ordinal()] = true;
			dpads[EnumKeyCodes.up.ordinal()] = false;
		}
		else if (i == KeyEvent.KEYCODE_DPAD_DOWN)
		{
			old_dpads[EnumKeyCodes.down.ordinal()] = true;
			dpads[EnumKeyCodes.down.ordinal()] = false;
		}
		else if (i == KeyEvent.KEYCODE_BACK)
		{
			old_dpads[EnumKeyCodes.back.ordinal()] = true;
			dpads[EnumKeyCodes.back.ordinal()] = false;
		}
		else if (i == KeyEvent.KEYCODE_MENU)
		{
			old_dpads[EnumKeyCodes.menu.ordinal()] = true;
			dpads[EnumKeyCodes.menu.ordinal()] = false;
		}
		else if (i == KeyEvent.KEYCODE_SEARCH)
		{
			old_dpads[EnumKeyCodes.search.ordinal()] = true;
			dpads[EnumKeyCodes.search.ordinal()] = false;
		}
	}
	
	public void eventUpdateDown(int i, KeyEvent event)
	{
		if (i == KeyEvent.KEYCODE_DPAD_CENTER)
		{
			old_dpads[EnumKeyCodes.center.ordinal()] = false;
			dpads[EnumKeyCodes.center.ordinal()] = true;
		}
		else if (i == KeyEvent.KEYCODE_DPAD_LEFT)
		{
			old_dpads[EnumKeyCodes.left.ordinal()] = false;
			dpads[EnumKeyCodes.left.ordinal()] = true;
		}
		else if (i == KeyEvent.KEYCODE_DPAD_RIGHT)
		{
			old_dpads[EnumKeyCodes.right.ordinal()] = false;
			dpads[EnumKeyCodes.right.ordinal()] = true;
		}
		else if (i == KeyEvent.KEYCODE_DPAD_UP)
		{
			old_dpads[EnumKeyCodes.up.ordinal()] = false;
			dpads[EnumKeyCodes.up.ordinal()] = true;
		}
		else if (i == KeyEvent.KEYCODE_DPAD_DOWN)
		{
			old_dpads[EnumKeyCodes.down.ordinal()] = false;
			dpads[EnumKeyCodes.down.ordinal()] = true;
		}
		else if (i == KeyEvent.KEYCODE_BACK)
		{
			old_dpads[EnumKeyCodes.back.ordinal()] = false;
			dpads[EnumKeyCodes.back.ordinal()] = true;
		}
		else if (i == KeyEvent.KEYCODE_MENU)
		{
			old_dpads[EnumKeyCodes.menu.ordinal()] = false;
			dpads[EnumKeyCodes.menu.ordinal()] = true;
		}
		else if (i == KeyEvent.KEYCODE_SEARCH)
		{
			old_dpads[EnumKeyCodes.search.ordinal()] = false;
			dpads[EnumKeyCodes.search.ordinal()] = true;
		}
	}
	
	public void eventUpdate(MotionEvent event)
	{
		// rewritten to be more standard and efficient
		int action = event.getActionMasked();
		
		if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN //
				|| action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP //
				|| action == MotionEvent.ACTION_CANCEL) //
		{
			
			int pointer_index = event.getActionIndex();
			int id = event.getPointerId(pointer_index);
			
			if (id >= 0 && id < finger_count)
			{
				
				x[id] = (float) Functions.deviceXToScreenX(event.getX(pointer_index));
				y[id] = (float) Functions.deviceYToScreenY(event.getY(pointer_index));
				
				if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN)
				{
					old_pressed[id] = false;
					pressed[id] = true;
				}
				else
				// action up
				{
					old_pressed[id] = true;
					pressed[id] = false;
				}
			}
		}
		
		// only mildly redundant.
		else if (action == MotionEvent.ACTION_MOVE)
		{
			for (int i = 0; i < event.getPointerCount(); i++)
			{
				int id = event.getPointerId(i);
				
				if (id >= 0 && id < finger_count)
				{
					x_old[id] = x[id];
					y_old[id] = y[id];
					
					x[id] = (float) Functions.deviceXToScreenX(event.getX(i));
					y[id] = (float) Functions.deviceYToScreenY(event.getY(i));
					
					x_delta[id] = x[id] - x_old[id];
					y_delta[id] = y[id] - y_old[id];
				}
			}
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
		return x_old[index];
	}
	
	public float getOldy(int index)
	{
		return y_old[index];
	}
	
	public float getDeltax(int index)
	{
		return x_delta[index];
	}
	
	public float getDeltay(int index)
	{
		return y_delta[index];
	}
	
	public boolean getTouched(int index)
	{
		return pressed[index];
	}
	
	public boolean getKeyPressed(EnumKeyCodes key)
	{
		int index = key.ordinal();
		
		if (dpads[index] && !old_dpads[index])
		{
			old_dpads[index] = true;
			return true;
		}
		
		return false;
	}
	
	public boolean getKeyReleased(EnumKeyCodes key)
	{
		int index = key.ordinal();
		
		if (!dpads[index] && old_dpads[index])
		{
			old_dpads[index] = false;
			return true;
		}
		
		return false;
	}
	
	public boolean getPressed(int index)
	{
		if (pressed[index] && !old_pressed[index])
		{
			old_pressed[index] = true;
			
			return true;
		}
		
		return false;
	}
	
	public boolean getReleased(int index)
	{
		if (!pressed[index] && old_pressed[index])
		{
			old_pressed[index] = false;
			return true;
		}
		
		return false;
	}
}
