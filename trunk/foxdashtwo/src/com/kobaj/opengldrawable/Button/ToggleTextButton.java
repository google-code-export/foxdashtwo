package com.kobaj.opengldrawable.Button;

import com.kobaj.math.Constants;

public class ToggleTextButton extends TextButton
{
	public int label_pointer = 0;
	private int[] labels;
	
	// these are shader coordinates of the center of the button
	public ToggleTextButton(int... resource_label)
	{
		super(resource_label[0], true);
		
		this.labels = resource_label;
		
		int max_width = Integer.MIN_VALUE;
		for (int i = labels.length - 1; i >= 0; i--)
		{
			int measured_size = Constants.text.measureTextWidth(labels[i]);
			if (measured_size > max_width)
				max_width = measured_size;
		}
		
		width = max_width + padding;
	}
	
	@Override
	public boolean isReleased()
	{
		boolean pushed = super.isReleased();
		
		if (pushed)
		{
			label_pointer = (label_pointer + 1) % labels.length;
			label = labels[label_pointer];
		}
		
		return pushed;
	}
}
