package com.kobaj.activity;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

//threading!
public class CustomThread implements Runnable
{
	private Thread thread;
	
	private SurfaceHolder surfaceHolder;
	private ISurface panel;
	private boolean run = false;
	
	public CustomThread(SurfaceHolder surfaceHolder, ISurface panel)
	{
		this.surfaceHolder = surfaceHolder;
		this.panel = panel;
	}
	
	public void start()
	{
		run = true;
		thread = new Thread(this);
		thread.start();
	}
	
	public void restart()
	{
		run = true;
	}
	
	public void stop()
	{
		run = false;
	}
	
	public void run()
	{
		Canvas c;
		while (true)
		{
			if (run)
			{
				c = null;
				panel.onUpdate(System.currentTimeMillis());
				
				try
				{
					c = surfaceHolder.lockCanvas(null);
					synchronized (surfaceHolder)
					{
						panel.onDraw(c);
					}
				}
				catch (Exception e)
				{
					// do nothing?
				}
				finally
				{
					if (c != null)
					{
						surfaceHolder.unlockCanvasAndPost(c);
					}
				}
			}
			else
			{
				try
				{
					Thread.sleep(500);
				}
				catch (InterruptedException ie)
				{
					
				}
			}
		}
	}
}
