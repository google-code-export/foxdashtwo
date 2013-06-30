package com.kobaj.opengldrawable.Button;

import com.kobaj.math.Constants;
import com.kobaj.opengldrawable.Quad.QuadCompressed;

public class ImageButton extends Button
{
	private int image;
	private int image_alpha;
	
	// just incase we want to change these for image buttons.
	public int pressed_color;
	public int unpressed_color;
	
	// these are shader coordinates of the center of the button
	// width and height are in screen coordinates
	public ImageButton(int image, int image_alpha, int width, int height)
	{
		this.image = image;
		this.image_alpha = image_alpha;
		this.width = width;
		this.height = height;
		
		this.unpressed_color = Constants.ui_button_unpressed;
		this.pressed_color = Constants.ui_button_pressed;
	}
	
	public void onInitialize()
	{
		super.onInitialize();
		
		// even if we dont draw this, we will need to instantiate it so we have something to check a bounding box with.
		invisible_outline = new QuadCompressed(image, image_alpha, width, height);
		draw_background = true;
	}
	
	public void onDrawConstant()
	{
		if (draw_background)
		{
			int color = unpressed_color;
			if (isTouched())
				color = pressed_color;
			
			invisible_outline.color = color;
			invisible_outline.onDrawAmbient(Constants.my_ip_matrix, true);
		}
	}
}
