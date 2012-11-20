package com.kobaj.level.LevelTypeLight;

import org.simpleframework.xml.Element;

import com.kobaj.foxdashtwo.R;
import com.kobaj.level.EnumLevelCustomLight;
import com.kobaj.opengldrawable.Quad.QuadCompressed;

public class LevelCustomLight extends LevelBloomLight
{
	@Element
	public EnumLevelCustomLight light_object;
	
	@Override
	public void onInitialize()
	{
		int main = -1;
		int alpha = -1;
		int width = 0;
		int height = 0;
		
		if (light_object == EnumLevelCustomLight.test)
		{
			// set your lights here including width and height
			main = R.raw.test_light;
			alpha = R.raw.test_light_alpha;
			width = 413;
			height = 125;
		}
		
		if (main != -1 && alpha != -1)
		{
			quad_light = new QuadCompressed(main, R.raw.black_alpha, width, height);
			if (is_bloom)
				quad_bloom = new QuadCompressed(main, alpha, width, height);
		}
		
		setupPositions();
	}
}
